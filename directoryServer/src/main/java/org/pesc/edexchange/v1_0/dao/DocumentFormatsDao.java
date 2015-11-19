package org.pesc.edexchange.v1_0.dao;

import org.pesc.edexchange.v1_0.DocumentFormat;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface DocumentFormatsDao extends DBDataSourceDao<DocumentFormat> {
	List<DocumentFormat> search(
			Integer id,
			String formatName,
			String formatDescription,
			Integer formatInuseCount,
			Long createdTime,
			Long modifiedTime);
	
	public List<HashMap<String, Object>> forJson();
}

