package com.example.urlshortener.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoLocationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeoLocationService.class);
    
    @Value("${geoip.database-path}")
    private String databasePath;
    
    @Value("${geoip.enabled}")
    private boolean geoipEnabled;
    
    private DatabaseReader databaseReader;

    @PostConstruct
    public void init() {
        if (!geoipEnabled) {
            logger.info("GeoIP service is disabled");
            return;
        }
        
        try {
            File database = new File(databasePath);
            if (database.exists()) {
                databaseReader = new DatabaseReader.Builder(database).build();
                logger.info("GeoIP database loaded successfully from: {}", databasePath);
            } else {
                logger.warn("GeoIP database not found at: {}. Geographic features will be disabled.", databasePath);
                geoipEnabled = false;
            }
        } catch (IOException e) {
            logger.error("Failed to load GeoIP database: {}", e.getMessage());
            geoipEnabled = false;
        }
    }

    public LocationInfo getLocationInfo(String ipAddress) {
        if (!geoipEnabled || databaseReader == null) {
            return null;
        }
        
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = databaseReader.city(inetAddress);
            
            Country country = response.getCountry();
            City city = response.getCity();
            Location location = response.getLocation();
            
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setCountry(country.getName());
            locationInfo.setCountryCode(country.getIsoCode());
            locationInfo.setCity(city.getName());
            locationInfo.setLatitude(location.getLatitude());
            locationInfo.setLongitude(location.getLongitude());
            
            return locationInfo;
            
        } catch (Exception e) {
            logger.debug("Failed to get location info for IP: {}", ipAddress, e);
            return null;
        }
    }

    public static class LocationInfo {
        private String country;
        private String countryCode;
        private String city;
        private Double latitude;
        private Double longitude;

        public LocationInfo() {}

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        @Override
        public String toString() {
            return "LocationInfo{" +
                    "country='" + country + '\'' +
                    ", city='" + city + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}