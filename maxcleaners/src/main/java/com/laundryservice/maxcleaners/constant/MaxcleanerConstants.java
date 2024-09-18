package com.laundryservice.maxcleaners.constant;


import com.laundryservice.maxcleaners.model.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Author Tejesh
 */
@Configuration
@ConfigurationProperties(prefix = "spring")
public class MaxcleanerConstants {

    public static String GEO_BASEURL;

    public static List<Store> stores;

    public static int MILES;

    public final static String ADDRESS_KEY = "address";

    public final static String BENCHMARK_KEY = "benchmark";

    public final static String BENCHMARK_VAL = "Public_AR_Current";

    public final static String FORMAT_KEY = "format";

    public final static String FORMAT_VAL = "json";

    public final static String COORDINATE_RES = "coordinates";

    public final static String RESULT_RES = "result";

    public final static String LON_RES = "x";

    public final static String LAT_RES = "y";

    public final static String ADDRESS_MATCH_RES = "addressMatches";

    public final static String ADDRESS_SUCESSES_MSG = "Providing service for this address";

    public final static String ADDRESS_ERR_MSG = "Unable to provide service for this address";

    public String getGEO_BASEURL() {
        return GEO_BASEURL;
    }

    @Value("${spring.geocode.baseurl}")
    public void setGEO_BASEURL(String gEO_BASEURL) {
        GEO_BASEURL = gEO_BASEURL;
    }


    public List<Store> getStores() {
        return stores;
    }

    // @Value("${app}")
    public void setStores(List<Store> stores) {
        MaxcleanerConstants.stores = stores;
    }

    public int getMILES() {
        return MILES;
    }

    @Value("${spring.miles}")
    public void setMILES(int mILES) {
        MILES = mILES;
    }


}
