package org.pesc.cds.service;

import org.pesc.cds.domain.NetworkServerId;
import org.pesc.cds.domain.NetworkServerSettings;
import org.pesc.cds.domain.TransactionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class DatasourceManagerUtil {
	private static NetworkServerId identification;
	private static NetworkServerSettings systemProperties;


	@Autowired
	private static TransactionsDao transactionsDao;
	
	private static TransactionsDao buildTransactions() {
		return transactionsDao;
	}
	
	public static NetworkServerId getIdentification() { return identification; }
	public static void setIdentification(NetworkServerId id) { identification = id; }
	public static NetworkServerSettings getSystemProperties() { return systemProperties; }
	public static void setSystemProperties(NetworkServerSettings settings) { systemProperties = settings; }
}
