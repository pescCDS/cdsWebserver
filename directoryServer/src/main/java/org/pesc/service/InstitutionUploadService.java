package org.pesc.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.*;
import org.pesc.api.repository.InstitutionUploadResultsRepository;
import org.pesc.api.repository.InstitutionUploadsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by james on 5/6/16.
 */
@Service
public class InstitutionUploadService {

    private static final Log log = LogFactory.getLog(InstitutionUploadService.class);

    private static final int MAX_COLUMNS = 11;
    private static String[] REQUIRED_COLUMNS_NAMES = {"name","city","state","atp","act","ipeds","opeid","fice" };
    private static String[] OPTINOAL_COLUMN_NAMES = { "website","street","zip" };


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SchoolCodesService schoolCodesService;

    @Autowired
    private InstitutionUploadsRepository uploadsRepository;

    @Autowired
    private InstitutionUploadResultsRepository resultsRepository;

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#institutionsUpload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public InstitutionsUpload create(InstitutionsUpload institutionsUpload)  {
        return uploadsRepository.save(institutionsUpload);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#institutionsUpload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public InstitutionsUpload update(InstitutionsUpload institutionsUpload)  {
        return uploadsRepository.save(institutionsUpload);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#orgID == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public List<InstitutionsUpload> getUploadsByOrganization(Integer orgID)  {
        return uploadsRepository.findByOrgId(orgID);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organizationId == principal.organizationId AND hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN'))")
    public InstitutionsUpload getUploadById(Integer uploadId)  {
        return uploadsRepository.findOne(uploadId);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<InstitutionsUploadResult> getUploadResultsByUploadId(Integer uploadId)  {
        return resultsRepository.findByUploadId(uploadId);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<InstitutionsUploadResult> getUploadResultsByOrgId(Integer orgID)  {
        return resultsRepository.findByOrgId(orgID);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public InstitutionsUploadResult create(InstitutionsUploadResult institutionsResultUpload)  {
        return resultsRepository.save(institutionsResultUpload);
    }


    @Transactional(readOnly=true)
    public List<CSVStatusDTO> getCSVStatus(Integer uploadID)  {

        String sql = "SELECT iur.line_number, iu.end_time FROM institution_uploads iu JOIN institution_upload_results iur on iur.institution_upload_id = iu.id WHERE iu.id = ? order by iur.line_number DESC LIMIT 1";
        List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, uploadID);

        ArrayList<CSVStatusDTO> statusList = new ArrayList<CSVStatusDTO>();

        for(Map row : rows) {
            CSVStatusDTO status = new CSVStatusDTO();
            status.setLineNumber((Integer) row.get("line_Number"));
            java.sql.Timestamp dateTime = (java.sql.Timestamp)row.get("end_time");

            if (dateTime != null)
                 status.setEndTime(new Date(dateTime.getTime()));
            statusList.add(status);
        }


        return statusList;
    }


    private boolean isValidUploadFile(Map<String,Integer> headerMap) {

        for(String columnName : REQUIRED_COLUMNS_NAMES) {

            if (!headerMap.containsKey(columnName)) {
                return false;
            }
        }
        return true;

    }

    private void addSchoolCode(Set<SchoolCode> schoolCodes, String codeType, String code){
        if (!StringUtils.isEmpty(code)){
            SchoolCode schoolCode = new SchoolCode();
            schoolCode.setCodeType(codeType);
            schoolCode.setCode(code);
            schoolCodesService.validateSchoolCode(schoolCode);
            schoolCodes.add(schoolCode);
        }
    }

    @Async
    public void processCSVFile(InstitutionsUpload institutionsUpload, String filePath, Integer serviceProviderID) {

        try {
            institutionsUpload.setStartTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));

            this.update(institutionsUpload);

            final Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

            if (!isValidUploadFile(parser.getHeaderMap() ) ) {
                InstitutionsUploadResult result = new InstitutionsUploadResult();
                result.setInstitutionUploadID(institutionsUpload.getId());
                result.setOrganizationID(serviceProviderID);

                result.setOutcome(InstitutionUploadResultsRepository.ERROR);
                StringBuilder buf = new StringBuilder("The institutions CSV file must contain a header record with the following case sensitive column names: ");
                buf.append(String.join(",", REQUIRED_COLUMNS_NAMES));
                result.setMessage(buf.toString());
                this.create(result);
                return;
            }

            try {
                for (final CSVRecord record : parser) {

                    Organization organization = new Organization();
                    organization.setName(record.get("name").trim());

                    if (record.isMapped("street"))
                        organization.setStreet(record.get("street").trim());

                    organization.setCity(record.get("city").trim());
                    organization.setState(record.get("state").trim());
                    if (record.isMapped("zip"))
                        organization.setZip(record.get("zip").trim());

                    if (record.isMapped("website"))
                        organization.setWebsite(record.get("website").trim());

                    InstitutionsUploadResult result = new InstitutionsUploadResult();
                    result.setInstitutionUploadID(institutionsUpload.getId());
                    result.setOrganizationID(serviceProviderID);
                    result.setLineNumber((int)parser.getCurrentLineNumber());


                    Set<SchoolCode> schoolCodes = new HashSet<SchoolCode>();

                    try {
                        addSchoolCode(schoolCodes, "ATP", record.get("atp").trim());
                        addSchoolCode(schoolCodes, "FICE", record.get("fice").trim());
                        addSchoolCode(schoolCodes, "IPEDS", record.get("ipeds").trim());
                        addSchoolCode(schoolCodes, "OPEID", record.get("opeid").trim());
                        addSchoolCode(schoolCodes, "ACT", record.get("act").trim());
                    }
                    catch (IllegalArgumentException e) {
                        result.setLineNumber((int) parser.getCurrentLineNumber());

                        result.setOutcome(InstitutionUploadResultsRepository.ERROR);

                        if (e.getMessage() != null) {
                            result.setMessage(e.getMessage());
                        }
                        this.create(result);
                        continue;
                    }

                    List<Organization> institutions = organizationService.findBySchoolCodes(schoolCodes);

                    if (institutions.size() == 1) {
                        organizationService.insecureLinkInstitutionWithServiceProvider(institutions.get(0).getId(), serviceProviderID);
                        result.setOutcome(InstitutionUploadResultsRepository.SUCCESS);
                        result.setMessage(
                                String.format("An institution was found with one or more of school code(s) ATP %s, FICE %s, IPEDS %s, ACT %s, OPEID %s, " +
                                        "and was added to the service provider's serviceable institutions.", record.get("atp"), record.get("fice"), record.get("ipeds"), record.get("act"), record.get("opeid")));
                        result.setInstitutionID(institutions.get(0).getId());
                        result.setInstitutionName(institutions.get(0).getName());
                    }
                    else if (institutions.isEmpty()) {
                        try {
                            organization = organizationService.createInstitution(organization, schoolCodes, serviceProviderID);
                            result.setMessage(String.format("An institution was created with the following school code(s) ATP %s, FICE %s, IPEDS %s, ACT %s, OPEID %s, " +
                                    "and was added to the service provider's serviceable institutions.", record.get("atp"), record.get("fice"), record.get("ipeds"), record.get("act"), record.get("opeid")));

                            result.setOutcome(InstitutionUploadResultsRepository.SUCCESS);
                            result.setInstitutionID(organization.getId());
                            result.setInstitutionName(organization.getName());
                        }
                        catch (Exception e) {
                            log.error("Failed to create institution.", e);
                            result.setOutcome(InstitutionUploadResultsRepository.ERROR);
                            StringBuilder messageBuilder = new StringBuilder(String.format(
                                    "<p>Could not create institution with code(s) ATP %s, FICE %s, IPEDS %s, ACT %s, OPEID %s.  Exception %s</p>",
                                    record.get("atp"), record.get("fice"), record.get("ipeds"), record.get("act"), record.get("opeid"), e.getClass().getCanonicalName()));
                            result.setMessage(messageBuilder.toString());
                            result.setInstitutionID(organization.getId());
                            result.setInstitutionName(organization.getName());
                        }
                    }
                    else {
                        StringBuilder messageBuilder = new StringBuilder(String.format(
                                "<div><p>%s was not processed because multiple institutions were found with the same school code(s) ATP %s, FICE %s, IPEDS %s, ACT %s, OPEID %s.  This indicates data inconsistency.</p>",
                                record.get("name"), record.get("atp"), record.get("fice"), record.get("ipeds"), record.get("act"), record.get("opeid")));

                        for(Organization institution : institutions) {
                            messageBuilder.append(String.format("<div><a href='#organization/%d'>%s</a></div>", institution.getId(), institution.getName()));
                        }

                        messageBuilder.append("</div>");

                        result.setOutcome(InstitutionUploadResultsRepository.ERROR);
                        result.setMessage(messageBuilder.toString());
                        result.setInstitutionID(organization.getId());
                        result.setInstitutionName(organization.getName());
                    }

                    this.create(result);

                }
            }
            catch (Exception e){

                log.error("Failed to process institution upload file. Inserting result record.", e);

                InstitutionsUploadResult result = new InstitutionsUploadResult();
                result.setInstitutionUploadID(institutionsUpload.getId());
                result.setOrganizationID(serviceProviderID);
                result.setLineNumber((int) parser.getCurrentLineNumber());

                result.setOutcome(InstitutionUploadResultsRepository.ERROR);

                if (e.getMessage() != null) {
                    result.setMessage(e.getMessage());
                }
                this.create(result);
            }
            finally {
                parser.close();
                reader.close();

            }
        } catch (Exception e) {
            log.error("Failed to process CSV file for institution creation.", e);
        }
        finally {
            institutionsUpload.setEndTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            this.update(institutionsUpload);
        }

    }
}
