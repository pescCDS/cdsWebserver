package org.pesc.cds.networkserver.service;

import org.pesc.cds.networkserver.domain.TransactionsDao;

public class DatasourceManagerUtil {
	private static TransactionsDao transactions = buildTransactions();
	
	private static TransactionsDao buildTransactions() {
		return new TransactionsDao();
	}
	
	public static TransactionsDao getTransactions() { return transactions; }
}
