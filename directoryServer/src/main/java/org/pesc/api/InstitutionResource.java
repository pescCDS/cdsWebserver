package org.pesc.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.pesc.api.model.*;
import org.pesc.api.repository.ServiceProviderRepository;
import org.pesc.service.OrganizationService;
import org.pesc.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;


/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/institutions")
@Api("/institutions")
public class InstitutionResource {

    private static final Log log = LogFactory.getLog(InstitutionResource.class);

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UploadService uploadService;

    @Value("${directory.uploaded.csv}")
    private String csvDir;


    private Upload persistUploadRecord(String inputFilepath) {

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            AuthUser auth = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Upload upload = new Upload();
            upload.setUserId(auth.getId());
            upload.setOrganizationId(auth.getOrganizationId());
            upload.setInputPath(inputFilepath);

            return uploadService.create(upload);
        }

        return null;
    }

    @POST
    @Path("/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response convertCSVtoJSON(@Multipart("org_id") Integer orgID,
                                     @Multipart("file") Attachment attachment) {




        DataHandler handler = attachment.getDataHandler();
        try {
            InputStream stream = handler.getInputStream();
            MultivaluedMap<String, String> map = attachment.getHeaders();
            //System.out.println("fileName Here" + getFileName(map));


            File outfile = new File(csvDir + File.separator + UUID.randomUUID().toString() + ".csv");
            Files.copy(stream, outfile.toPath());

            Upload upload = persistUploadRecord(outfile.getAbsolutePath());

            processCSVFile(upload, outfile.getPath(), orgID);

        } catch (Exception e) {
            log.error("Failed to save uploaded CSV file", e);

            throw new RuntimeException("Failed to save uploaded CSV file");

        }



        return Response.ok("").build();
    }


    private void addSchoolCode(Set<SchoolCode> schoolCodes, String codeType, String code){
        if (!StringUtils.isEmpty(code)){
            SchoolCode schoolCode = new SchoolCode();
            schoolCode.setCodeType(codeType);
            schoolCode.setCode(code);
            schoolCodes.add(schoolCode);
        }
    }


    private Map<String, Object> addOrgInfo(Map<String,Object> resultMap, String orgName, Integer orgID) {
        resultMap.put("organization_name", orgName);
        resultMap.put("org_id",orgID);
        return resultMap;
    }

    private Map<String,Object> addMessage(Map<String,Object> resultMap, String message) {
        resultMap.put("message", message);
        return  resultMap;
    }

    @Async
    private void processCSVFile(Upload upload, String filePath, Integer serviceProviderID) {

        try {
            upload.setStartTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            BufferedWriter resultLog = null;

            final Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            ObjectMapper mapper = new ObjectMapper();

            try {
                String logPath = csvDir + File.separator + UUID.randomUUID().toString() + ".log";

                upload.setOutputPath(logPath);

                FileWriter fstream = new FileWriter(logPath, true); //true: append to file
                resultLog = new BufferedWriter(fstream);
                resultLog.append('[');


                for (final CSVRecord record : parser) {

                    if (parser.getCurrentLineNumber() > 2) {
                       resultLog.write(',');
                    }
                    Organization organization = new Organization();
                    organization.setName(record.get("name"));
                    organization.setStreet(record.get("street"));
                    organization.setCity(record.get("city"));
                    organization.setState(record.get("state"));
                    organization.setZip(record.get("zip"));
                    organization.setWebsite(record.get("website"));

                    Set<SchoolCode> schoolCodes = new HashSet<SchoolCode>();

                    addSchoolCode(schoolCodes, "ATP", record.get("atp"));
                    addSchoolCode(schoolCodes, "FICE", record.get("fice"));
                    addSchoolCode(schoolCodes, "IPEDS", record.get("ipeds"));
                    addSchoolCode(schoolCodes, "OPEID", record.get("opeid"));
                    addSchoolCode(schoolCodes, "ACT", record.get("act"));

                    Map<String, Object> resultMap = new HashMap<String, Object>();

                    try {
                        organization = organizationService.createInstitution(organization, schoolCodes, serviceProviderID);
                        resultMap.put("level", "INFO");
                        addOrgInfo(resultMap, record.get("name"), organization.getId());
                    }
                    catch (Exception e) {



                        if (e.getMessage() != null && e.getMessage().contains("unique_code_pair")) {

                            List<Organization> institutions = organizationService.findBySchoolCodes(schoolCodes);

                            if (institutions.size() == 1) {
                                organizationService.linkInstitutionWithServiceProvider(institutions.get(0).getId(), serviceProviderID);
                                resultMap.put("level", "WARN");
                                addMessage(resultMap, "A duplicate institution was found and was added to the service provider's serviceable institutions.");
                                addOrgInfo(resultMap, institutions.get(0).getName(), institutions.get(0).getId());
                            }
                            else {
                                resultMap.put("level", "ERROR");
                                addMessage(resultMap,
                                        String.format("Multiple instiutions were found with the same school code(s) ATP %s, FICE %s, IPEDS %s, ACT %s, OPEID %s.  This indicates data inconsistency.",
                                                record.get("atp"), record.get("fice"), record.get("ipeds"), record.get("act"), record.get("opeid")));
                            }

                        }
                        else {
                            resultMap.put("level", "ERROR");
                            addMessage(resultMap, "An error ocurred which prevented the institution from being created.");

                        }

                    }

                    resultLog.write(mapper.writeValueAsString(resultMap));
                }
            }
            catch (Exception e){
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("level", "ERROR");
                addMessage(resultMap, e.getMessage() != null ? e.getMessage() : "Failed to process CSV file, line number " + parser.getCurrentLineNumber());
                resultLog.write(mapper.writeValueAsString(resultMap));
            }
            finally {
                parser.close();
                reader.close();

                resultLog.append(']');

                resultLog.close();


            }
        } catch (Exception e) {
            log.error("Failed to process CSV file for institution creation.", e);
        }
        finally {
            upload.setEndTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            uploadService.update(upload);
        }

    }


    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String exactFileName = name[1].trim().replaceAll("\"", "");
                return exactFileName;
            }
        }

        throw new RuntimeException("Failed to extract file name.");
    }

    @GET
    @ApiOperation("Return the institutions that are serviced by this service provider.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Organization> getServiceProvidersForInstitution(@QueryParam("service_provider_id") @ApiParam(value="The directory identifier for the service provider.", required = true) Integer id) {

        if (id == null) {
            throw new IllegalArgumentException("The service provider's id must be provided.");
        }
        ServiceProvider serviceProvider = serviceProviderRepository.findOne(id);

        if (serviceProvider == null) {
            return new HashSet<>();
        }

        return serviceProvider.getInstitutions();
    }



}
