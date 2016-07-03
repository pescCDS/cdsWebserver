package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.SchoolCode;
import org.pesc.api.repository.SchoolCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.util.regex.Pattern;

/**
 * Created by james on 3/21/16.
 */
@Service
public class SchoolCodesService {

    private static final Log log = LogFactory.getLog(SchoolCodesService.class);

    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    public SchoolCodesService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Autowired
    private SchoolCodesRepository schoolCodesRepository;

    @Transactional(readOnly=true)
    public Iterable<SchoolCode> findAll(){
        return this.schoolCodesRepository.findAll();
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public void delete(SchoolCode schoolCode)  {
        this.schoolCodesRepository.delete(schoolCode);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organizationId == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN'))")
    public void delete(Integer id)  {
        this.schoolCodesRepository.delete(id);
    }



    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public SchoolCode findById(Integer id)  {
        SchoolCode schoolCode = this.schoolCodesRepository.findOne(id);
        return schoolCode;
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public SchoolCode create(SchoolCode schoolCode) throws IllegalArgumentException {
        validateSchoolCode(schoolCode);
        return this.schoolCodesRepository.save(schoolCode);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public SchoolCode update(SchoolCode schoolCode) throws IllegalArgumentException {
        validateSchoolCode(schoolCode);
        return this.schoolCodesRepository.save(schoolCode);
    }

    //Documentation states that Pattern is thread safe...
    /*
    The Java regular expression API has been designed to allow a single compiled pattern to be shared across multiple match operations.
     */
    private Pattern sixDigitPattern = Pattern.compile("\\d{6}");
    private Pattern eightDigitPattern = Pattern.compile("\\d{8}");
    private Pattern fourDigitPattern = Pattern.compile("\\d{4}");

    private boolean validate6Digits(String code)
    {
        return sixDigitPattern.matcher(code).matches();
    }

    private boolean validate8Digits(String code)
    {
        return eightDigitPattern.matcher(code).matches();
    }

    private boolean validate4Digits(String code)
    {
        return fourDigitPattern.matcher(code).matches();
    }

    public void validateCode(String code, String codeType) throws IllegalArgumentException {
        if (StringUtils.isEmpty(codeType)){
            return;
        }

        if (codeType.equalsIgnoreCase("ATP")) {
            if (!validate6Digits(code)){
                throw new IllegalArgumentException(String.format("Bad ATP code %s. ATP codes must be exactly 6 digits.", code));
            }

        }
        else if (codeType.equalsIgnoreCase("FICE")) {
            if (!validate6Digits(code)){
                throw new IllegalArgumentException(String.format("Bad FICE code %s. FICE codes must be exactly 6 digits.", code));
            }
        }
        else if (codeType.equalsIgnoreCase("IPEDS")) {
            if (!validate6Digits(code)){
                throw new IllegalArgumentException(String.format("Bad IPEDS code %s. IPEDS codes must be exactly 6 digits.", code));
            }
        }
        else if (codeType.equalsIgnoreCase("ACT")) {
            if (!validate4Digits(code)){
                throw new IllegalArgumentException(String.format("Bad ACT code %s. ACT codes must be exactly 4 digits.", code));
            }
        }
        else if (codeType.equalsIgnoreCase("OPEID")) {
            if (!validate8Digits(code)){
                throw new IllegalArgumentException(String.format("Bad OPEID code %s. OPEID codes must be exactly 8 digits.", code));
            }
        }
        else {
            throw new IllegalArgumentException(String.format("%s is not a school code supported by EDExchange.", codeType));
        }
    }

    public void validateSchoolCode(SchoolCode schoolCode) throws IllegalArgumentException  {

          validateCode(schoolCode.getCode(), schoolCode.getCodeType());
    }
}
