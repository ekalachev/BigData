package services;

import dto.Geo;
import dto.Hotel;
import dto.Weather;
import org.apache.commons.lang3.Validate;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import repositories.Repository;

import static de.cronn.reflection.util.PropertyUtils.getPropertyName;

public class HotelWeatherServiceImpl implements HotelWeatherService {
    private final GeoService geoService;
    private final Repository<Hotel> hotelRepository;
    private final Repository<Weather> weatherRepository;

    public HotelWeatherServiceImpl(GeoService geoService, Repository<Hotel> hotelRepository, Repository<Weather> weatherRepository) {
        Validate.notNull(geoService);
        Validate.notNull(hotelRepository);
        Validate.notNull(weatherRepository);

        this.geoService = geoService;
        this.hotelRepository = hotelRepository;
        this.weatherRepository = weatherRepository;
    }

    public Dataset<Hotel> getHotelWithPopulatedGeoHashAndRemovedDuplicates() {

        Dataset<Hotel> hotelsDataset = this.hotelRepository
                .getDataset()
                .map((MapFunction<Hotel, Hotel>) hotel -> {
                    if (hotel.getLongitude() == null || hotel.getLatitude() == null) {
                        Geo geo = this.geoService.findGeoCoordinates(
                                hotel.getCountry(), hotel.getCity(), hotel.getAddress());

                        hotel.setLatitude(geo.getLatitude());
                        hotel.setLongitude(geo.getLongitude());
                        hotel.setGeoHash(this.geoService.getGeoHash(geo));
                    } else {
                        hotel.setGeoHash(this.geoService.getGeoHash(
                                new Geo(hotel.getLatitude(), hotel.getLongitude())));
                    }

                    return hotel;
                }, this.hotelRepository.getEncoder());

        return hotelsDataset.dropDuplicates(getPropertyName(Hotel.class, Hotel::getId));
    }

    public Dataset<Weather> getWeatherWithPopulatedGeoHashAndRemovedDuplicates() {

        Dataset<Weather> weatherDataset = this.weatherRepository
                .getDataset()
                .map((MapFunction<Weather, Weather>) weather -> {
                    weather.setGeoHash(this.geoService.getGeoHash(
                            new Geo(weather.getLat(), weather.getLng())));

                    return weather;
                }, this.weatherRepository.getEncoder());

        return weatherDataset
                .dropDuplicates(new String[]{
                        getPropertyName(Weather.class, Weather::getGeoHash),
                        getPropertyName(Weather.class, Weather::getWthr_date)
                });
    }
}
