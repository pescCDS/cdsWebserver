package org.pesc.cds.xml.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

public class XMLAdapter {
	
	// gets the value as a string and turns it into a Date
	public static Date parseDate(String dateString) {
		return DatatypeConverter.parseDate(dateString).getTime();
	}
	
	// gets the value as a date and turns it into a string
		public static String printDate(Date d) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(d);
			//return DatatypeConverter.printDate(cal);
			return Long.toString(cal.getTimeInMillis());
		}
}
