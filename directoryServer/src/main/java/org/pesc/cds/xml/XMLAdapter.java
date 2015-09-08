package org.pesc.cds.xml;

import java.util.Calendar;
import java.sql.Timestamp;

import javax.xml.bind.DatatypeConverter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class XMLAdapter {

	// Parse/Print Date from string
	public static Calendar parseDate(String dateString) {
		return DatatypeConverter.parseDate(dateString);
		//return ISODateTimeFormat.dateTime().parseDateTime(dateString);
	}
	public static String printDate(Calendar c) {
		return Long.toString( c.getTime().getTime() );
	}
	
	// Parse/Print DateTime
	public static Calendar parseDateTime(String dateTimeString) {
		return DatatypeConverter.parseDateTime(dateTimeString);
	}
	public static String printDateTime(Calendar c) {
		return Long.toString( c.getTime().getTime() );
	}
	
	// Parse/Print Timestamp
	public static Calendar parseTimestamp(String timestampString) {
		// string format should be "yyyy-MM-dd HH:mm:ss"
		
		Calendar c = DatatypeConverter.parseDate(timestampString);
		DateTime conv = new DateTime(c.getTimeInMillis(), DateTimeZone.UTC);
		return conv.toGregorianCalendar();
		
		//return DatatypeConverter.parseDate(timestampString);
		//return Timestamp.valueOf(timestampString);
		
		// an alternative to just use the seconds
		//return DatatypeConverter.parseDate(timestampString).getTime().getTime();
	}
	public static String printTimestamp(Calendar ts) {
		//return Long.toString( ts.getTime() );
		return Long.toString( ts.getTime().getTime() );
	}
	
	
	// 
	public static Integer parseInteger(String bigIntString) {
		return Integer.parseInt(bigIntString);
	}
	
	
	public static Boolean parseBoolean(String boolString) {
		Boolean b = DatatypeConverter.parseBoolean(boolString);
		return boolString==null ? null : Boolean.valueOf(boolString);
	}
	public static String printBoolean(Boolean b) {
		return b.toString();
	}
	/*
	public static Integer parseBoolean(String boolString) {
		Boolean b = DatatypeConverter.parseBoolean(boolString);
		return new Integer(b?1:0);
	}
	public static String printBoolean(Integer i) {
		return i.toString();
	}
	*/
}
