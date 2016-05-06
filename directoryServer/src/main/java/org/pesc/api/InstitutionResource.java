package org.pesc.api;

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
import org.pesc.api.model.Organization;
import org.pesc.api.model.SchoolCode;
import org.pesc.api.model.ServiceProvider;
import org.pesc.api.repository.ServiceProviderRepository;
import org.pesc.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

    @Value("${directory.uploaded.csv}")
    private String csvDir;


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

            processCSVFile(outfile.getPath(), orgID);

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

    @Async
    private void processCSVFile(String filePath, Integer serviceProviderID) {

        try {
            BufferedWriter resultLog = null;

            final Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
            final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            try {
                String logPath = csvDir + File.separator + UUID.randomUUID().toString() + ".log";

                FileWriter fstream = new FileWriter(logPath, true); //true: append to file
                resultLog = new BufferedWriter(fstream);

                for (final CSVRecord record : parser) {

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


                    try {
                        organizationService.createInstitution(organization, schoolCodes, serviceProviderID);

                    }
                    catch (Exception e) {

                        if (e.getMessage() != null && e.getMessage().contains("unique_code_pair")) {
                            resultLog.write(String.format("CREATE FAILED, %s, %s\n", record.get("name"), "DUPLICATE SCHOOL CODE"));

                            List<Organization> institutions = organizationService.findBySchoolCodes(schoolCodes);

                            if (institutions.size() == 1) {
                                log.info("Linking them up!");
                                organizationService.linkInstitutionWithServiceProvider(institutions.get(0).getId(), serviceProviderID);

                            }
                        }
                        else {
                            resultLog.write(String.format("CREATE FAILED, %s, %s\n", record.get("name"), e.getMessage() != null ? e.getMessage() : e.getCause()));
                        }

                    }
                }
            } finally {
                parser.close();
                reader.close();
                resultLog.close();
            }
        } catch (Exception e) {
            log.error("Failed to process CSV file for institution creation.", e);
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
