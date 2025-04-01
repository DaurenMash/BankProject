package com.bank.publicinfo.enumtype;

public enum EntityType {


    BANK_DETAILS, BRANCH, ATM, LICENSE, CERTIFICATE;


    public static EntityType entityTypeFromString(String type) {
        if (type.toLowerCase().contains("bankdetails")) {
            return BANK_DETAILS;
        }
        else if (type.toLowerCase().contains("branch")){
            return BRANCH;
        }
        else if (type.toLowerCase().contains("atm")){
            return ATM;
        }
        else if (type.toLowerCase().contains("license")){
            return LICENSE;
        }
        else if (type.toLowerCase().contains("certificate")){
            return CERTIFICATE;
        }
        throw new IllegalArgumentException("No enum constant for type: " + type);
    }


}
