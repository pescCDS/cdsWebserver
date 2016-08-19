package org.pesc.sdk.util;

import org.pesc.sdk.sector.academicrecord.v1_7.AddressType;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 8/19/2014
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class PescAddress {
    private AddressType address = null;
    private String streetAddress1 = null;
    private String streetAddress2 = null;
    private String city = null;
    private String state = null;
    private String postalCode = null;
    private String country = null;
    private boolean addressValid = true;
    private boolean addressDomestic = true;

    public PescAddress(PescAddressBuilder builder) {
        this.address = builder.address;
        this.streetAddress1 = builder.streetAddress1;
        this.streetAddress2 = builder.streetAddress2;
        this.city = builder.city;
        this.state = builder.state;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
        this.addressValid = builder.addressValid;
        this.addressDomestic = builder.addressDomestic;
    }

    public AddressType getAddress() {
        return address;
    }

    public void setAddress(AddressType address) {
        this.address = address;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isAddressValid() {
        return addressValid;
    }

    public void setAddressValid(boolean addressValid) {
        this.addressValid = addressValid;
    }

    public boolean isAddressDomestic() {
        return addressDomestic;
    }

    public void setAddressDomestic(boolean addressDomestic) {
        this.addressDomestic = addressDomestic;
    }

    public static class PescAddressBuilder{
        private AddressType address = null;
        private String streetAddress1 = null;
        private String streetAddress2 = null;
        private String city = null;
        private String state = null;
        private String postalCode = null;
        private String country = null;
        private boolean addressValid = true;
        private boolean addressDomestic = true;

        public PescAddressBuilder address(AddressType address) {
            this.address = address;
            return this;
        }

        public PescAddressBuilder streetAddress1(String streetAddress1) {
            this.streetAddress1 = streetAddress1;
            return this;
        }

        public PescAddressBuilder streetAddress2(String streetAddress2) {
            this.streetAddress2 = streetAddress2;
            return this;
        }

        public PescAddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public PescAddressBuilder state(String state) {
            this.state = state;
            return this;
        }

        public PescAddressBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public PescAddressBuilder country(String country) {
            this.country = country;
            return this;
        }

        public PescAddressBuilder addressValid(boolean addressValid) {
            this.addressValid = addressValid;
            return this;
        }

        public PescAddressBuilder addressDomestic(boolean addressDomestic) {
            this.addressDomestic = addressDomestic;
            return this;
        }

        public PescAddress build(){
            return new PescAddress(this);
        }
    }
}
