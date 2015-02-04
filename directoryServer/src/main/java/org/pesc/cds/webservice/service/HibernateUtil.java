package org.pesc.cds.webservice.service;

import java.util.LinkedHashMap;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static final LinkedHashMap<String, String> aliasMap = buildAliasMap();

	private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory getSessionFactory() { return sessionFactory; }
    
    /**
     * For any date filter criteria that uses the sql restriction, only actual database
     * column names can be used. This method will return a map that can be used to
     * convert the persistence object property name to the table column name
     * @return <code>LinkedHashMap&lt;String, String&gt;</code>
     */
    public static LinkedHashMap<String, String> buildAliasMap() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    	map.put("createdTime", "created_time");
    	map.put("modifiedTime", "modified_time");
    	return map;
    }
    public static LinkedHashMap<String, String> getAliasMap() {
    	return aliasMap;
    }
}
