package com.laundryservice.maxcleaners.util;


import com.laundryservice.maxcleaners.constant.MaxcleanerConstants;
import com.laundryservice.maxcleaners.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Map;

/**
 * Author Tejesh
 */
@Component
public class CheckAvailabilityUtil {

    private static final Logger logger = LoggerFactory.getLogger(CheckAvailabilityUtil.class);

    @Autowired
    private RestTemplate restTemplate;
    private final WebClient webClient;

    public CheckAvailabilityUtil(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://geocoding.geo.census.gov").build();
    }
    public Response validateAddressToServe(String address) throws Exception {
        Response response = new Response();
        try {
            String url = UriComponentsBuilder.fromHttpUrl(MaxcleanerConstants.GEO_BASEURL)
                    .queryParam(MaxcleanerConstants.ADDRESS_KEY, address)
                    .queryParam(MaxcleanerConstants.BENCHMARK_KEY, MaxcleanerConstants.BENCHMARK_VAL)
                    .queryParam(MaxcleanerConstants.FORMAT_KEY, MaxcleanerConstants.FORMAT_VAL)
                    .toUriString();

            logger.info("validateAddressToServe URL: {}", url);

            // Make the call using WebClient
            Map<String, Object> resMap = this.webClient.get()
                    .uri(url, address, "Public_AR_Current", "json")
                    .retrieve()
                    .onStatus(HttpStatus::isError, ClientResponse::createException) // Handle HTTP errors
                    .bodyToMono(Map.class) // Expecting a response body as Map
                    .block(); // Blocking for synchronous behavior

            System.out.println("Response from API: " + resMap);
            // Fetch the response from the external service
           // Map<String, Object> res_map = restTemplate.getForObject(url, Map.class);
            logger.info("validateAddressToServe received data: {}", resMap);

            // Extract the match status from the result
            boolean matchStatus = extractAddressMatch((Map<String, Object>) resMap.get(MaxcleanerConstants.RESULT_RES));
            response.setServiceStatus(matchStatus);

            if (matchStatus) {
                response.setMessage(MaxcleanerConstants.ADDRESS_SUCESSES_MSG);
            } else {
                response.setMessage(MaxcleanerConstants.ADDRESS_ERR_MSG);
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error in HTTP request: {}", e.getMessage());
            response.setServiceStatus(false);
            response.setMessage("HTTP Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("General error: {}", e.getMessage());
            response.setServiceStatus(false);
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    public boolean extractAddressMatch(Map<String, Object> res_map) {
        boolean status = false;
        try {
            if (res_map != null && res_map.get(MaxcleanerConstants.ADDRESS_MATCH_RES) != null) {
                List<Map<String, Object>> addMatchLis = (List<Map<String, Object>>) res_map
                        .get(MaxcleanerConstants.ADDRESS_MATCH_RES);
                if (addMatchLis != null && !addMatchLis.isEmpty()) {
                    Map<String, Object> addressMap = addMatchLis.get(0);
                    Map<String, Object> cordinateMap = (Map<String, Object>) addressMap.get(MaxcleanerConstants.COORDINATE_RES);
                    if (cordinateMap != null) {
                        double latitude = cordinateMap.get(MaxcleanerConstants.LAT_RES) != null
                                ? Double.parseDouble(cordinateMap.get(MaxcleanerConstants.LAT_RES).toString())
                                : 0;
                        double longitude = cordinateMap.get(MaxcleanerConstants.LON_RES) != null
                                ? Double.parseDouble(cordinateMap.get(MaxcleanerConstants.LON_RES).toString())
                                : 0;
                        if (latitude != 0 && longitude != 0) {
                            double miles = calculateDistance(latitude, longitude);
                            if (miles >= 0 && miles <= MaxcleanerConstants.MILES) {
                                status = true;
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("Exception occur during extractAddressMatch::", e);
        }

        return status;
    }

    private double calculateDistance(double latitude, double logitude) {
        // Haversine formula to calculate distance between two coordinates
        double earthRadius = 3963.0;
        double miles = 0;
        logger.info("calculateDistance latitude::" + latitude);
        logger.info("calculateDistance logitude::" + logitude);
        try {
            // Convert latitude and longitude from degrees to radians
            double storeLatitude = MaxcleanerConstants.stores.get(0).getLatitude();
            double storeLongititude = MaxcleanerConstants.stores.get(0).getLongitude();
            logger.info("calculateDistance storeLatitude::" + storeLatitude);
            logger.info("calculateDistance storeLongititude::" + storeLongititude);
            double lat1Rad = Math.toRadians(storeLatitude);
            double lon1Rad = Math.toRadians(storeLongititude);
            double lat2Rad = Math.toRadians(latitude);
            double lon2Rad = Math.toRadians(logitude);


            logger.info("lat1Rad::" + lat1Rad);
            logger.info("lon1Rad::" + lon1Rad);
            logger.info("lat2Rad::" + lat2Rad);
            logger.info("lon2Rad::" + lon2Rad);
            // Haversine formula
            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;
            double a = Math.pow(Math.sin(deltaLat / 2), 2)
                    + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            miles = earthRadius * c;
        } catch (Exception e) {
            // TODO: handle exception
        }
        logger.info("calculateDistance miles::" + miles);

        return miles; // Distance in miles
    }
}
