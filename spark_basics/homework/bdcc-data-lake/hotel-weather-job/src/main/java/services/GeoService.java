package services;

import dto.Geo;

import java.io.Serializable;

public interface GeoService extends Serializable {
    Geo findGeoCoordinates(String country, String city, String address);
    String getGeoHash(Geo geo);
}
