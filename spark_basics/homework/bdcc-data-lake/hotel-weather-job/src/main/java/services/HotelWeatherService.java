package services;

import dto.Hotel;
import dto.Weather;
import org.apache.spark.sql.Dataset;

import java.io.Serializable;

public interface HotelWeatherService extends Serializable {
    Dataset<Hotel> getHotelWithPopulatedGeoHashAndRemovedDuplicates();
    Dataset<Weather> getWeatherWithPopulatedGeoHashAndRemovedDuplicates();
}
