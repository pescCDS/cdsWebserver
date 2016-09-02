package org.pesc.sdk.util;

import org.pesc.sdk.core.coremain.v1_12.CountryCodeType;
import org.pesc.sdk.core.coremain.v1_12.StateProvinceCodeType;
import org.pesc.sdk.sector.academicrecord.v1_7.AddressType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 8/19/2014
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class PescAddressUtils {
    public static final Logger logger = LoggerFactory.getLogger(PescAddressUtils.class);

    private static final Map<String, String> iso3Toiso2CountryCodeMap;

    // used to accommodate non-ISO2/ISO3 or out-of-date country string
    private static final Map<CountryCodeType, List<String>> countryAliases;

    // contains US and CA states
    private static final Map<String, String> stateMap;

    static {
        String[] countries = Locale.getISOCountries();
        iso3Toiso2CountryCodeMap = new HashMap<String, String>(countries.length);
        for (String country : countries) {
            Locale locale = new Locale("", country);
            iso3Toiso2CountryCodeMap.put(locale.getISO3Country(), country);
        }

        countryAliases = new HashMap<CountryCodeType, List<String>>();

        List<String> aliases = new ArrayList<String>();
        aliases.add("USA");
        aliases.add("UNITED STATES");
        aliases.add("UNITED STATES OF AMERICA");
        countryAliases.put(CountryCodeType.US, aliases);

        aliases = new ArrayList<String>();
        aliases.add("CANADA");
        countryAliases.put(CountryCodeType.CA, aliases);

        aliases = new ArrayList<String>();
        aliases.add("YU");
        countryAliases.put(CountryCodeType.MK, aliases);

        aliases = new ArrayList<String>();
        aliases.add("PI");
        countryAliases.put(CountryCodeType.PH, aliases);

        aliases = new ArrayList<String>();
        aliases.add("AC");
        countryAliases.put(CountryCodeType.SH, aliases);

        aliases = new ArrayList<String>();
        aliases.add("TP");
        countryAliases.put(CountryCodeType.TL, aliases);

        // this is an incorrect alias to work around the new country code
        // that is not part of PESC country enumeration list
        aliases = new ArrayList<String>();
        aliases.add("SX");
        countryAliases.put(CountryCodeType.NL, aliases);

        stateMap = new HashMap<String, String>();
        stateMap.put("Alabama","AL");
        stateMap.put("Alaska","AK");
        stateMap.put("Alberta","AB");
        stateMap.put("American Samoa","AS");
        stateMap.put("Arizona","AZ");
        stateMap.put("Arkansas","AR");
        stateMap.put("Armed Forces (AE)","AE");
        stateMap.put("Armed Forces Americas","AA");
        stateMap.put("Armed Forces Pacific","AP");
        stateMap.put("British Columbia","BC");
        stateMap.put("California","CA");
        stateMap.put("Colorado","CO");
        stateMap.put("Connecticut","CT");
        stateMap.put("Delaware","DE");
        stateMap.put("District Of Columbia","DC");
        stateMap.put("Florida","FL");
        stateMap.put("Georgia","GA");
        stateMap.put("Guam","GU");
        stateMap.put("Hawaii","HI");
        stateMap.put("Idaho","ID");
        stateMap.put("Illinois","IL");
        stateMap.put("Indiana","IN");
        stateMap.put("Iowa","IA");
        stateMap.put("Kansas","KS");
        stateMap.put("Kentucky","KY");
        stateMap.put("Louisiana","LA");
        stateMap.put("Maine","ME");
        stateMap.put("Manitoba","MB");
        stateMap.put("Maryland","MD");
        stateMap.put("Massachusetts","MA");
        stateMap.put("Michigan","MI");
        stateMap.put("Minnesota","MN");
        stateMap.put("Mississippi","MS");
        stateMap.put("Missouri","MO");
        stateMap.put("Montana","MT");
        stateMap.put("Nebraska","NE");
        stateMap.put("Nevada","NV");
        stateMap.put("New Brunswick","NB");
        stateMap.put("New Hampshire","NH");
        stateMap.put("New Jersey","NJ");
        stateMap.put("New Mexico","NM");
        stateMap.put("New York","NY");
        stateMap.put("Newfoundland","NF");
        stateMap.put("North Carolina","NC");
        stateMap.put("North Dakota","ND");
        stateMap.put("Northwest Territories","NT");
        stateMap.put("Nova Scotia","NS");
        stateMap.put("Nunavut","NU");
        stateMap.put("Ohio","OH");
        stateMap.put("Oklahoma","OK");
        stateMap.put("Ontario","ON");
        stateMap.put("Oregon","OR");
        stateMap.put("Pennsylvania","PA");
        stateMap.put("Prince Edward Island","PE");
        stateMap.put("Puerto Rico","PR");
        stateMap.put("Quebec","PQ");
        stateMap.put("Rhode Island","RI");
        stateMap.put("Saskatchewan","SK");
        stateMap.put("South Carolina","SC");
        stateMap.put("South Dakota","SD");
        stateMap.put("Tennessee","TN");
        stateMap.put("Texas","TX");
        stateMap.put("Utah","UT");
        stateMap.put("Vermont","VT");
        stateMap.put("Virgin Islands","VI");
        stateMap.put("Virginia","VA");
        stateMap.put("Washington","WA");
        stateMap.put("West Virginia","WV");
        stateMap.put("Wisconsin","WI");
        stateMap.put("Wyoming","WY");
        stateMap.put("Yukon Territory","YT");
    }

    /**
     * It is mainly for converting database address.country field to PESC CountryCodeType.
     * Valid inputs are 2-letter or 3-letter standard ISO country string.  When input string
     * is null or empty, CountryCodeType.US is returned.  For other invalid inputs, null is returned.
     *
     * @param countryStr
     * @return
     */
    public static CountryCodeType getCountryCode(String countryStr) {
        CountryCodeType countryCode = null;

        countryStr = StringUtils.strip(countryStr, " \"");
        if (countryStr==null || countryStr.isEmpty()) {
            logger.debug("null country string: " + countryStr);
            // use US as the default country
            return CountryCodeType.US;
        }
        countryStr = countryStr.toUpperCase();

        Set<String> iso3CountryCodes = iso3Toiso2CountryCodeMap.keySet();
        Collection<String> iso2CountryCodes = iso3Toiso2CountryCodeMap.values();

        for (Map.Entry<CountryCodeType, List<String>> entry: countryAliases.entrySet()) {
            for (String alias: entry.getValue()) {
                if (alias.equals(countryStr)) {
                    countryCode = entry.getKey();
                    return countryCode;
                }
            }
        }

        if (iso3CountryCodes.contains(countryStr)) {
            String iso2CountryCode = iso3Toiso2CountryCodeMap.get(countryStr);
            countryCode = CountryCodeType.valueOf(iso2CountryCode);
        } else if (iso2CountryCodes.contains(countryStr)) {
            countryCode = CountryCodeType.valueOf(countryStr);
        } else {
            logger.warn("Invalid country string: " + countryStr);
            countryCode = null;
        }

        return countryCode;
    }

    /**
     * It is mainly for converting US or CA address.state to PESC StateProvinceCodeType.
     * Valid inputs are 2-letter or fully spelled state string.  Returns null for invalid inputs.
     * @param stateStr
     * @param countryCode
     * @return
     */

    public static StateProvinceCodeType getStateProvinceCode(String stateStr, CountryCodeType countryCode) {
        // only provides StateProvinceCode for US and CA
        if (!(countryCode.equals(CountryCodeType.CA) || countryCode.equals(CountryCodeType.US))) return null;

        stateStr = StringUtils.strip(stateStr, " \"");
        if (stateStr==null || stateStr.isEmpty()) {
            return null;
        }

        int len = stateStr.length();
        StateProvinceCodeType stateProvinceCode = null;

        if (len==2) {
            stateStr = stateStr.toUpperCase();
            try {
                stateProvinceCode = StateProvinceCodeType.valueOf(stateStr);
            } catch (Exception e) {
                logger.warn("Exception thrown in parsing state string: " + stateStr, e);
            }
        } else {
            stateStr = toCamelCase(stateStr);
            if (stateMap.containsKey(stateStr)) {
                try {
                    stateProvinceCode = StateProvinceCodeType.valueOf(stateMap.get(stateStr));
                } catch (Exception e) {
                    logger.warn("Exception thrown in parsing state string: " + stateStr, e);
                }
            }
        }

        return stateProvinceCode;
    }

    private static String toCamelCase(String s) {
        if (s==null || s.trim().length()==0) {
            return s;
        }

        String[] parts = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts){
            String newPart = part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
            sb.append(newPart + " ");
        }
        return sb.toString().trim();
    }

    /**
     * Canada if one of Canadian Provinces otherwise it's US.
     * @param stateProvinceCodeType
     * @return
     */
    public static CountryCodeType getCountry(StateProvinceCodeType stateProvinceCodeType){
        CountryCodeType countryCodeType = null;
        if(stateProvinceCodeType!=null) {
            countryCodeType = CountryCodeType.US;//Canada if one of Canadian Provinces otherwise it's US.
            switch (stateProvinceCodeType) {//notice I don't have breaks, these are all fall through.  Make sure you add breaks if you want different behavior.
                case AB:
                case BC:
                case MB:
                case NB:
                case NS:
                case NT:
                case NU:
                case ON:
                case PE:
                case QC:
                case SK:
                case YT:
                    countryCodeType = CountryCodeType.CA;
                    break;
            }
        }
        return countryCodeType;
    }

    /**
     * Get PescAddress from AddressType, determine if address is valid, and determine if address is domestic vs international
     * @param address
     * @return
     */
    public static PescAddress getPescAddress(AddressType address){
        PescAddress pescAddress = null;
        String streetAddress1 = null;
        String streetAddress2 = null;
        String city = null;
        String state = null;
        String postalCode = null;
        String country = null;
        boolean addressValid = true;
        boolean addressDomestic = true;
        if(address!=null) {
            if (address.getAddressLines() != null && address.getAddressLines().size() > 0) {
                streetAddress1 = address.getAddressLines().get(0);
                if (address.getAddressLines().size() > 1) {
                    streetAddress2 = address.getAddressLines().get(1);
                }
            }
            city = address.getCity();
            /**
             * 1) If StateProvinceCode is present address is Domestic.  PostalCode is also required.
             * 2) If StateProvinceCode is not present, then CountryCodeType must be present to be a valid International address otherwise address is invalid
             * See pesc implementation guide: http://www.pesc.org/library/docs/standards/College%20Transcript/XMLtranREQUESTigV1-2-0.pdf
             */
            StateProvinceCodeType stateProvinceCodeType = address.getStateProvinceCode();//StateProvinceCode - domestic only (required for domestic)
            if(stateProvinceCodeType!=null) {//Domestic
                state = stateProvinceCodeType.toString();
                String zip = address.getPostalCode();//PostalCode required
                if (StringUtils.isBlank(zip)) {
                    addressValid = false;
                } else {
                    postalCode = zip;
                }
                CountryCodeType countryCodeType = PescAddressUtils.getCountry(stateProvinceCodeType);//We can figure out Country based on StateProvinceCode
                if (countryCodeType != null) {
                    country = countryCodeType.toString();
                }
            }else{//International
                CountryCodeType countryCodeType = address.getCountryCode();
                if(countryCodeType!=null){
                    country = countryCodeType.toString();
                    state = address.getStateProvince();//StateProvince optional
                    postalCode = address.getPostalCode();//PostalCode optional
                    addressDomestic = false;
                }else{
                    addressValid = false;
                }
            }
            pescAddress = new PescAddress.PescAddressBuilder()
                    .address(address)
                    .streetAddress1(streetAddress1)
                    .streetAddress2(streetAddress2)
                    .city(city)
                    .state(state)
                    .postalCode(postalCode)
                    .country(country)
                    .addressValid(addressValid)
                    .addressDomestic(addressDomestic)
                    .build();
        }

        return pescAddress;
    }
}
