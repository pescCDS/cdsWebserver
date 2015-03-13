package org.pesc.cds.networkserver.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class homeController {
	
	private static final Log log = LogFactory.getLog(homeController.class);
	
	@RequestMapping("/")
	public String viewHome() {
		return "home";
	}
	
	
	@RequestMapping(value="/receiveFile", method=RequestMethod.POST)
    public @ResponseBody HashMap<String, Object> handleFileUpload(@RequestParam("file") MultipartFile file) {
        HashMap<String, Object> retMap = new HashMap<String, Object>(2, 1.0f);
        retMap.put("error", true);
        retMap.put("status", "");
		if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("someFileName")));
                stream.write(bytes);
                stream.close();
                retMap.put("error", false);
                retMap.put("status", "upload successfull");
                return retMap;
            } catch (Exception e) {
            	retMap.put("status", String.format("upload failed" + e.getMessage()));
                return retMap;
            }
        } else {
        	retMap.put("status", "missing file");
            return retMap;
        }
    }
}
