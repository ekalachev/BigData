package services;

import ch.hsr.geohash.GeoHash;
import dto.Geo;
import org.apache.commons.lang3.Validate;
import providers.LatLngSearchProvider;

import static java.lang.String.format;

public class GeoServiceImpl implements GeoService {
    final private int numberOfGeoHashCharacters = 4;
    private final LatLngSearchProvider latLngSearchProvider;

    public GeoServiceImpl(LatLngSearchProvider latLngSearchProvider) {
        this.latLngSearchProvider = latLngSearchProvider;
    }

    public Geo findGeoCoordinates(String country, String city, String address) {
        Validate.notEmpty(country);
        Validate.notEmpty(city);
        Validate.notEmpty(address);

        String fullAddress = format("%s, %s, %s", address, city, country);

        return latLngSearchProvider.search(fullAddress);
    }

    public String getGeoHash(Geo geo) {
        Validate.notNull(geo);
        Validate.notNull(geo.getLatitude());
        Validate.notNull(geo.getLongitude());

        return GeoHash.geoHashStringWithCharacterPrecision(
                geo.getLatitude(),
                geo.getLongitude(),
                numberOfGeoHashCharacters);
    }
}
