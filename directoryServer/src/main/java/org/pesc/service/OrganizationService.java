/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.*;
import org.pesc.api.repository.*;
import org.pesc.api.model.OAuthClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.security.x509.X500Name;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 * <p>
 * This service mixes JDBC and JPA.  The database configuration for this app instantiates the JpaTransactionManager
 * and according the documentation for JpaTransactionManager for Spring 4,
 * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html,
 * it (the JpaTransactionManager) supports direct access (JDBC) to the data source within a transaction.
 * <p>
 * From the documentation:
 * <p>
 * This transaction manager also supports direct DataSource access within a transaction
 * (i.e. plain JDBC code working with the same DataSource). This allows for mixing services which access JPA
 * and services which use plain JDBC (without being aware of JPA)! Application code needs to stick to the same
 * simple Connection lookup pattern as with DataSourceTransactionManager
 * (i.e. DataSourceUtils.getConnection(javax.sql.DataSource) or going through a
 * TransactionAwareDataSourceProxy). Note that this requires a vendor-specific JpaDialect to be configured.
 */
@Service
public class OrganizationService {

    private static final Log log = LogFactory.getLog(OrganizationService.class);

    private EntityManagerFactory entityManagerFactory;

    public static final String SECRET_REQUIREMENTS = "The OAuth secret must be at least 15 characters long, contain 1 upper case letter, 1 lower case letter, 1 number and 1 special character @$ _!%*#?&.";

    private Pattern oauthSecretPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$ _!%*#?&])[A-Za-z\\d$@$ _!%*#?&]{15,}$");

    private List<OrganizationType> organizationTypes;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public OrganizationService(EntityManagerFactory entityManagerFactory, OrganizationTypeRepository organizationTypeRepository) {
        this.organizationTypes = (List<OrganizationType>) organizationTypeRepository.findAll();

        //remove the 'System' type, it should never be used.
        //organizationTypes.remove(0);

        this.entityManagerFactory = entityManagerFactory;

    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolCodesService schoolCodesService;


    @Transactional(readOnly = true)
    public Iterable<Organization> findAll() {
        return this.organizationRepository.findAll();
    }

    /**
     * This method is intended to be invoked by a user who has already validated the organization, so it does
     * not require a review/approval, as when a unknown user registers a new organization.
     *
     * @param organization
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') ")
    public Organization create(Organization organization) {
        organization.setEnabled(true);
        organization.setActive(true);
        Date createdTime = Calendar.getInstance().getTime();
        organization.setCreatedTime(createdTime);
        organization.setModifiedTime(createdTime);
        return organizationRepository.save(organization);
    }

    /**
     * Insert a new organization and school codes, associating each school code with the organization and inserting
     * the school codes.
     *
     * @param organization
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("(hasRole('ROLE_SYSTEM_ADMIN') OR (hasRole('ROLE_ORG_ADMIN') AND #serviceProviderID == principal.organizationId) )")
    public Organization securedCreateInstitution(Organization organization, Set<SchoolCode> schoolCodes, Integer serviceProviderID) {
        return createInstitution(organization, schoolCodes, serviceProviderID);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Organization createInstitution(Organization organization, Set<SchoolCode> schoolCodes, Integer serviceProviderID) {
        organization.setEnabled(false);
        organization.setActive(true);

        Date createdTime = Calendar.getInstance().getTime();
        organization.setCreatedTime(createdTime);
        organization.setModifiedTime(createdTime);

        organization.setOrganizationTypes(new HashSet<OrganizationType>());
        organization.getOrganizationTypes().add(this.getOrganizationTypes().get(1));

        Organization savedOrg = organizationRepository.save(organization);
        for (SchoolCode schoolCode : schoolCodes) {
            schoolCode.setOrganizationId(savedOrg.getId());
            schoolCodesService.nonSecuredCreate(schoolCode);
        }
        savedOrg.setSchoolCodes(schoolCodes);

        secureLinkInstitutionWithServiceProvider(savedOrg.getId(), serviceProviderID);

        return savedOrg;
    }


    /**
     * Insert a new organization and school codes, associating each school code with the organization and inserting
     * the school codes.
     *
     * @param organization
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("( hasRole('ROLE_SYSTEM_ADMIN') OR (hasRole('ROLE_ORG_ADMIN') AND !(#organization.organizationTypes.?[#this.name == 'Institution'].empty)) )")
    public Organization createInstitution(Organization organization) {
        organization.setEnabled(false);
        organization.setActive(true);

        Date createdTime = Calendar.getInstance().getTime();
        organization.setCreatedTime(createdTime);
        organization.setModifiedTime(createdTime);
        organization.setOrganizationTypes(new HashSet<OrganizationType>());
        organization.getOrganizationTypes().add(this.getOrganizationTypes().get(1));

        Set<SchoolCode> schoolCodes = organization.getSchoolCodes();
        organization.setSchoolCodes(new HashSet<SchoolCode>());

        Organization savedOrg = organizationRepository.save(organization);
        for (SchoolCode schoolCode : schoolCodes) {

            schoolCode.setOrganizationId(savedOrg.getId());

            try {
                organization.getSchoolCodes().add(schoolCodesService.nonSecuredCreate(schoolCode));
            }
            catch (RuntimeException ex) {

                throw new IllegalArgumentException(String.format("A school code of type %s and value %s already exists and cannot be duplicated.  Run a search for an institution with the given code and code type.", schoolCode.getCodeType(), schoolCode.getCode()));
            }

        }

        return savedOrg;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("(hasRole('ROLE_SYSTEM_ADMIN') OR (hasRole('ROLE_ORG_ADMIN') AND #serviceProviderID == principal.organizationId) )")
    public void secureLinkInstitutionWithServiceProvider(Integer institutionID, Integer serviceProviderID) {
        insecureLinkInstitutionWithServiceProvider(institutionID, serviceProviderID);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insecureLinkInstitutionWithServiceProvider(Integer institutionID, Integer serviceProviderID) {
        try {
            jdbcTemplate.update("insert into institutions_service_providers (institution_id, service_provider_id) values(?,?)", institutionID, serviceProviderID);
        } catch (DuplicateKeyException e) {
            log.warn("A duplicate key was encountered while associating a service provider with an institution.", e);
        }

    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void setSigningCertificate(String pemFormatedCertificate, Integer orgID) {

        jdbcTemplate.update("insert into organizations (signing_certificate) values(?) where id = ?", pemFormatedCertificate, orgID);

    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#organization.id == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public Organization update(Organization organization) {
        return this.organizationRepository.save(organization);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Organization organization) {
        this.organizationRepository.delete(organization);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id) {

        jdbcTemplate.update("DELETE FROM institutions_service_providers where institution_id = ? OR service_provider_id = ?", id, id);
        schoolCodesService.deleteByOrgId(id);
        this.userService.deleteByOrgId(id);
        this.contactService.deleteByOrgId(id);
        this.messageService.deleteByOrgId(id);
        this.organizationRepository.delete(id);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    //TODO: add PreAuthorize
    public List<Organization> findByName(String name) {
        return this.organizationRepository.findByName(name.toUpperCase());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public OAuthClientDetails getOAuthClientDetails(Integer orgID) {
        return this.oAuthClientDetailsRepository.findOne(orgID);
    }


    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    //TODO: add PreAuthorize
    public Organization findById(Integer id) {

        return this.organizationRepository.findOne(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void addEndpointToOrganization(Integer orgID, Integer endpointID) {

        jdbcTemplate.update(
                "insert into endpoint_organization (endpoint_id, organization_id) values (?, ?)",
                endpointID, orgID);

    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeEndpointToOrganization(Integer orgID,
                                             Integer endpointID) {

        jdbcTemplate.update(
                "delete from endpoint_organization where endpoint_id = ? and organization_id =?", endpointID, orgID);
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void setEnabledForOrganization(Integer orgID, Boolean enable) {

        jdbcTemplate.update(
                "update organization set enabled = ? where id = ?", enable, orgID);

        if (enable == false) {
            revokeOAuthTokens(orgID);
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private void revokeOAuthTokens(Integer orgID) {

        jdbcTemplate.update("delete from oauth_access_token where client_id = ?", String.valueOf(orgID));
    }
    /*
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void setProperty(Integer orgID, String propertyName, Object propertyValue) {

        log.info(String.format("Updating %s property to %s.", propertyName, propertyValue));
        jdbcTemplate.update(
                String.format("update organization set %s = ? where id = ?", propertyName), propertyValue, orgID);
    }
    */


    private String convertPublicKeyToPEM(PublicKey key) {
        StringBuilder pem = new StringBuilder();

        pem.append("-----BEGIN PUBLIC KEY-----\r\n");
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        pem.append(encodedKey.replaceAll("(.{64})", "$1\r\n"));

        if (pem.charAt(pem.length() - 1) != '\n') {
            pem.append("\r\n");
        }

        pem.append("-----END PUBLIC KEY-----\r\n");


        return pem.toString();
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public CertificateInfo setSigningCertificate(Integer orgID, String pemCert) throws CertificateException {

        log.info(String.format("Updating signing certificate for org %d\n%s", orgID, pemCert));

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(pemCert.getBytes()));
        PublicKey key = cer.getPublicKey();

        jdbcTemplate.update("update organization set signing_certificate = ?, public_key = ?, enabled = false where id = ?", pemCert, convertPublicKeyToPEM(key), orgID);

        revokeOAuthTokens(orgID);

        return buildCertificateInfo(pemCert);
    }


    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @PreAuthorize("isAuthenticated() AND ( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public String getOAuthSecret(Integer orgID) {

        String oauthSecret = jdbcTemplate.queryForObject("select oauth_secret from organization where id= ?", new Object[]{orgID}, String.class);

        return oauthSecret == null ? "" : oauthSecret;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public String setOAuthSecret(Integer orgID, String secret) {

        validateOAuthSecret(secret);

        String encodedSecret = passwordEncoder.encode(secret);

        jdbcTemplate.update("update organization set oauth_secret = ? where id = ?", encodedSecret, orgID);
        //jdbcTemplate.update("update organization set oauth_secret = ? where id = ?", secret, orgID);

        return encodedSecret;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public CertificateInfo setNetworkCertificate(Integer orgID, String pemCert) throws CertificateException, IOException {

        log.info(String.format("Updating network certificate for org %d\n%s", orgID, pemCert));


        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(pemCert.getBytes()));

        String domainName = X500Name.asX500Name(cer.getSubjectX500Principal()).getCommonName();
        jdbcTemplate.update("update organization set network_certificate = ?, network_domain = ?, enabled = false where id = ?", pemCert, domainName, orgID);

        revokeOAuthTokens(orgID);

        return buildCertificateInfo(pemCert);
    }

    @Transactional(readOnly = true)
    //@PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public CertificateInfo getSigningCertificate(Integer orgID) throws CertificateException {

        log.info(String.format("Retrieving signing certificate for org %d", orgID));

        return buildCertificateInfo(jdbcTemplate.queryForObject("SELECT signing_certificate FROM organization WHERE id = ?", new Object[]{orgID}, String.class));
    }

    @Transactional(readOnly = true)
    //@PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public CertificateInfo getNetworkCertificate(Integer orgID) throws CertificateException {

        log.info(String.format("Retrieving signing certificate for org %d", orgID));

        return buildCertificateInfo(jdbcTemplate.queryForObject("SELECT network_certificate FROM organization WHERE id = ?", new Object[]{orgID}, String.class));
    }

    @Transactional(readOnly = true)
    public String getPEMPublicKey(Integer orgID) throws CertificateException {

        log.info(String.format("Retrieving public key for org %d", orgID));

        return jdbcTemplate.queryForObject("SELECT public_key FROM organization WHERE id = ? and enabled = ?", new Object[]{orgID, true}, String.class);
    }

    private CertificateInfo buildCertificateInfo(String pemCert) throws CertificateException {

        CertificateInfo info = new CertificateInfo();

        if (!StringUtils.isEmpty(pemCert)) {
            X509Certificate cer = convertPEMtoX509(pemCert);


            info.setAlgorithmUsedToSign(cer.getSigAlgName());
            info.setIssuerDN(cer.getIssuerDN().getName());
            info.setNotAfter(cer.getNotAfter());
            info.setNotBefore(cer.getNotBefore());
            info.setSerialNumber(cer.getSerialNumber());
            info.setVersion(cer.getVersion());
            info.setSubjectDN(cer.getSubjectDN().getName());
            info.setPem(pemCert);

            try {
                String domainName = X500Name.asX500Name(cer.getSubjectX500Principal()).getCommonName();
                info.setCommonName(domainName);

            } catch (IOException e) {
                log.error(e);
            }

        }


        return info;
    }

    private X509Certificate convertPEMtoX509(String pemCert) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(pemCert.getBytes()));
        return cer;
    }

    @Transactional(readOnly = true)
    //@PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public CertificateInfo getCertificateInfo(Integer orgID) throws CertificateException {

        log.info(String.format("Retrieving signing certificate for org %d", orgID));


        String pemCert = jdbcTemplate.queryForObject("SELECT signing_certificate FROM organization WHERE id = ?", new Object[]{orgID}, String.class);


        return buildCertificateInfo(pemCert);

    }


    @Transactional(readOnly = true)
    //@PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public String getNetworkDomainName(Integer orgID) {

        return jdbcTemplate.queryForObject("SELECT network_domain FROM organization WHERE id = ?", new Object[]{orgID}, String.class);

    }

    @Transactional(readOnly = true)
    public List<Organization> findBySchoolCodesAndName(String name, Set<SchoolCode> schoolCodesList
    ) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
            Root<Organization> org = cq.from(Organization.class);
            cq.select(org).distinct(true);

            List<Predicate> predicates = new LinkedList<Predicate>();

            //ArrayList<String> codeTypes;

            addSchoolCodesPredicates(predicates,cb,org,schoolCodesList);
            predicates.add(cb.like(cb.lower(org.get("name")), "%" + name + "%"));


            if (schoolCodesList != null) {
                //ArrayList<String> codes = new ArrayList<String>();

                Join<SchoolCode, Organization> join = org.join("schoolCodes");

                Predicate or = cb.or();

                for (SchoolCode sc : schoolCodesList) {
                    Predicate and = cb.and(cb.equal(join.get("code"), sc.getCode()), cb.equal(join.get("codeType"), sc.getCodeType()));
                    or.getExpressions().add(and);
                }

                predicates.add(or);

                //predicates.add(org.get("schoolCodes").get("code").in(schoolCodesList));

            }


            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Organization> q = entityManager.createQuery(cq);

            return q.getResultList();
        } finally {
            entityManager.close();
        }

    }

    void addSchoolCodesPredicates(List<Predicate> predicates, CriteriaBuilder cb, Root<Organization> org, Set<SchoolCode> schoolCodesList){


        if (schoolCodesList != null) {

            Join<SchoolCode, Organization> join = org.join("schoolCodes");

            Predicate or = cb.or();

            for (SchoolCode sc : schoolCodesList) {
                Predicate and = cb.and(cb.equal(join.get("code"), sc.getCode()), cb.equal(join.get("codeType"), sc.getCodeType()));
                or.getExpressions().add(and);
            }

            predicates.add(or);

            //predicates.add(org.get("schoolCodes").get("code").in(schoolCodesList));
        }
    }
    /**
     * @param schoolCodesList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Organization> findBySchoolCodes(Set<SchoolCode> schoolCodesList
    ) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
            Root<Organization> org = cq.from(Organization.class);
            cq.select(org).distinct(true);

            List<Predicate> predicates = new LinkedList<Predicate>();

            //ArrayList<String> codeTypes;

            addSchoolCodesPredicates(predicates,cb,org,schoolCodesList);

            if (schoolCodesList != null) {
                //ArrayList<String> codes = new ArrayList<String>();

                Join<SchoolCode, Organization> join = org.join("schoolCodes");

                Predicate or = cb.or();

                for (SchoolCode sc : schoolCodesList) {
                    Predicate and = cb.and(cb.equal(join.get("code"), sc.getCode()), cb.equal(join.get("codeType"), sc.getCodeType()));
                    or.getExpressions().add(and);
                }

                predicates.add(or);

                //predicates.add(org.get("schoolCodes").get("code").in(schoolCodesList));

            }


            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Organization> q = entityManager.createQuery(cq);

            return q.getResultList();
        } finally {
            entityManager.close();
        }

    }

    @Transactional(readOnly = true)
    public List<Integer> getInstitutionsByServiceProviderId(Integer serviceProviderId) {

        String sql = "select institution_id as id from institutions_service_providers where service_provider_id = ?";

        List<Integer> rows = jdbcTemplate.queryForList(sql, Integer.class, serviceProviderId);

        return rows;
    }

    @Transactional(readOnly = true)
    public PagedData getInstitutionsByServiceProviderId(Integer serviceProviderId, PagedData<OrganizationDTO> pagedData) {

        String sql = "select isp.institution_id as id, org.name as name, org.enabled as enabled " +
                "from institutions_service_providers isp join organization org on isp.institution_id = org.id " +
                "where isp.service_provider_id = ? LIMIT ? OFFSET ?";


        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, serviceProviderId, pagedData.getLimit(), pagedData.getOffset());

        ArrayList<OrganizationDTO> institutionList = new ArrayList<OrganizationDTO>();

        for (Map row : rows) {
            OrganizationDTO org = new OrganizationDTO();
            org.setId((Integer)row.get("id"));
            org.setName((String) row.get("name"));
            org.setEnabled((Boolean)row.get("enabled"));
            institutionList.add(org);
        }

        pagedData.setTotal((Long) jdbcTemplate.queryForObject("SELECT COUNT(*) FROM institutions_service_providers WHERE service_provider_id=?", new Object[]{serviceProviderId}, Long.class));
        pagedData.setData(institutionList);
        return pagedData;
    }

    public Predicate[] createPredicates(CriteriaBuilder cb, Root<Organization> orgRoot, Integer directoryId,
                                        String organizationCode,
                                        String organizationCodeType,
                                        String organizationName,
                                        String organizationSubcode,
                                        String organizationType,
                                        String organizationEin,
                                        Long createdTime,
                                        Long modifiedTime,
                                        Boolean active,
                                        Boolean enabled,
                                        Boolean isServiceProvider,
                                        Boolean isInstitution) {
        List<Predicate> predicates = new LinkedList<Predicate>();

        if (directoryId != null) {
            predicates.add(cb.equal(orgRoot.get("id"), directoryId));
        }


        if (!StringUtils.isEmpty(organizationName)) {
            // tokenize string by whitespace and use each token as
            // a LIKE clause for the organizationName column
            StringTokenizer orgNametokens = new StringTokenizer(organizationName.trim());
            while (orgNametokens.hasMoreElements()) {

                String orgNametoken = orgNametokens.nextToken().toLowerCase();

                predicates.add(cb.like(cb.lower(orgRoot.get("name")), "%" + orgNametoken + "%"));

            }

        }

        if (!StringUtils.isEmpty(organizationSubcode)) {
            StringTokenizer subCodetokens = new StringTokenizer(organizationSubcode.trim());
            while (subCodetokens.hasMoreElements()) {
                String subCodetoken = subCodetokens.nextToken().toLowerCase();
                predicates.add(cb.like(cb.lower(orgRoot.get("subcode")), "%" + subCodetoken + "%"));
            }
        }


        if (!StringUtils.isEmpty(organizationType)) {

            Join<OrganizationType, Organization> join = orgRoot.join("organizationTypes");
            predicates.add(cb.equal(join.get("name"), organizationType));

        }

        if (!StringUtils.isEmpty(organizationEin)) {

            predicates.add(cb.like(cb.lower(orgRoot.get("ein")), "%" + organizationEin.trim().toLowerCase()));

        }

        if (!StringUtils.isEmpty(organizationCode)) {

            Join<SchoolCode, Organization> join = orgRoot.join("schoolCodes");
            predicates.add(cb.equal(join.get("code"), organizationCode));

            if (organizationCodeType != null) {
                predicates.add(cb.and(cb.equal(join.get("codeType"), organizationCodeType)));
            }

        }


        if (createdTime != null) {
            predicates.add(cb.equal(orgRoot.get("createdTime"), new Timestamp(createdTime)));
        }

        if (modifiedTime != null) {
            predicates.add(cb.equal(orgRoot.get("modifiedTime"), new Timestamp(modifiedTime)));
        }


        if (active != null) {
            predicates.add(cb.equal(orgRoot.get("active"), active));
        }

        if (enabled != null) {
            predicates.add(cb.equal(orgRoot.get("enabled"), enabled));
        }

        if (isServiceProvider != null || isInstitution != null) {


            Join<OrganizationType, Organization> types = orgRoot.join("organizationTypes");
            ArrayList<Integer> typeIds = new ArrayList<Integer>();
            if (isServiceProvider != null && isServiceProvider == true)
                typeIds.add(3);
            if (isInstitution != null && isInstitution == true)
                typeIds.add(2);

            predicates.add(types.get("id").in(typeIds));


        }

        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicates.toArray(predicateArray);

        return predicateArray;
    }

    private Long getResultLength(Integer directoryId,
                                 String organizationCode,
                                 String organizationCodeType,
                                 String organizationName,
                                 String organizationSubcode,
                                 String organizationType,
                                 String organizationEin,
                                 Long createdTime,
                                 Long modifiedTime,
                                 Boolean active,
                                 Boolean enabled,
                                 Boolean isServiceProvider,
                                 Boolean isInstitution) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Organization> countRoot = countQuery.from(Organization.class);
            countQuery.select(cb.count(countRoot));
            Predicate[] predicates = createPredicates(cb,
                    countRoot,
                    directoryId,
                    organizationCode,
                    organizationCodeType,
                    organizationName,
                    organizationSubcode,
                    organizationType,
                    organizationEin,
                    createdTime,
                    modifiedTime,
                    active,
                    enabled,
                    isServiceProvider,
                    isInstitution);


            countQuery.where(predicates);

            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            log.error("Failed to retrieve result length.", e);
        } finally {
            entityManager.close();
        }

        return 0L;
    }

    /**
     * @param directoryId
     * @param organizationCode
     * @param organizationCodeType
     * @param organizationName
     * @param organizationSubcode
     * @param organizationType
     * @param organizationEin
     * @param createdTime
     * @param modifiedTime
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PagedData<Organization> search(
            Integer directoryId,
            String organizationCode,
            String organizationCodeType,
            String organizationName,
            String organizationSubcode,
            String organizationType,
            String organizationEin,
            Long createdTime,
            Long modifiedTime,
            Boolean active,
            Boolean enabled,
            Boolean isServiceProvider,
            Boolean isInstitution,
            PagedData<Organization> pagedData
    ) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {


            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
            Root<Organization> orgRoot = cq.from(Organization.class);
            cq.select(orgRoot);

            Predicate[] predicates = createPredicates(cb,
                    orgRoot,
                    directoryId,
                    organizationCode,
                    organizationCodeType,
                    organizationName,
                    organizationSubcode,
                    organizationType,
                    organizationEin,
                    createdTime,
                    modifiedTime,
                    active,
                    enabled,
                    isServiceProvider,
                    isInstitution);


            cq.where(predicates);
            TypedQuery<Organization> q = entityManager.createQuery(cq);
            q.setFirstResult(pagedData.getOffset());
            q.setMaxResults(pagedData.getLimit());
            pagedData.setData(q.getResultList());


            pagedData.setTotal(getResultLength(directoryId,
                    organizationCode,
                    organizationCodeType,
                    organizationName,
                    organizationSubcode,
                    organizationType,
                    organizationEin,
                    createdTime,
                    modifiedTime,
                    active,
                    enabled,
                    isServiceProvider,
                    isInstitution));


            return pagedData;


        } catch (Exception ex) {
            log.error("Failed to execute organization search query.", ex);
        } finally {
            entityManager.close();
        }
        //return new PageImpl<Organization>(new ArrayList<Organization>());

        pagedData.setLimit(0);
        pagedData.setOffset(0);
        pagedData.setTotal(0L);
        pagedData.setData(new ArrayList<Organization>());
        return pagedData;
    }


    public void validateOAuthSecret(String secret) {
        if (!oauthSecretPattern.matcher(secret).matches()) {
            throw new IllegalArgumentException(SECRET_REQUIREMENTS);
        }
    }

}
