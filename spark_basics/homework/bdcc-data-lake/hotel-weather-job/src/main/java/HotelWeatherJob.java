import conf.SystemVariables;
import dto.Hotel;
import dto.HotelWeather;
import dto.Weather;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import providers.LatLngSearchProviderImpl;
import repositories.HotelRepositoryImpl;
import repositories.Repository;
import repositories.WeatherRepositoryImpl;
import scala.Tuple2;
import services.GeoService;
import services.GeoServiceImpl;
import services.HotelWeatherService;
import services.HotelWeatherServiceImpl;

import static de.cronn.reflection.util.PropertyUtils.getPropertyName;

public class HotelWeatherJob {

    public static void main(String[] args) {
        SystemVariables systemVariables = SystemVariables.getInstance();

        // TODO move it to IoC
        SparkSession spark = SparkSession
                .builder()
//                .master(systemVariables.getSparkMaster())
//                .config(systemVariables.getBlobReadKeyProperty(), systemVariables.getBlobReadContainerKey())
//                .config(systemVariables.getBlobWriteKeyProperty(), systemVariables.getBlobWriteContainerKey())
                .getOrCreate();

        GeoService geoService = new GeoServiceImpl(new LatLngSearchProviderImpl(systemVariables));

        Repository<Hotel> hotelRepository = new HotelRepositoryImpl(spark, systemVariables.getBlobReadUrl());
        Repository<Weather> weatherRepository = new WeatherRepositoryImpl(spark, systemVariables.getBlobReadUrl());

        HotelWeatherService hotelWeatherService = new HotelWeatherServiceImpl(geoService, hotelRepository, weatherRepository);

        Dataset<Hotel> hotelsDataset = hotelWeatherService.getHotelWithPopulatedGeoHashAndRemovedDuplicates();
        Dataset<Weather> weatherDataset = hotelWeatherService.getWeatherWithPopulatedGeoHashAndRemovedDuplicates();

        Dataset<HotelWeather> hotelWeatherDataset = weatherDataset
                .joinWith(
                        hotelsDataset,
                        weatherDataset
                                .col(getPropertyName(Weather.class, Weather::getGeoHash))
                                .equalTo(hotelsDataset.col(getPropertyName(Hotel.class, Hotel::getGeoHash))), "inner")
                .map((MapFunction<Tuple2<Weather, Hotel>, HotelWeather>) hw ->
                                new HotelWeather(hw._2(), hw._1()),
                        Encoders.bean(HotelWeather.class));

        hotelWeatherDataset
                .write()
                .partitionBy(
                        getPropertyName(HotelWeather.class, HotelWeather::getYear),
                        getPropertyName(HotelWeather.class, HotelWeather::getMonth),
                        getPropertyName(HotelWeather.class, HotelWeather::getDay)
                )
                .mode("overwrite")
                .format("parquet")
                .save(systemVariables.getBlobWriteUrl() + "/hotel-weather");

        spark.stop();
    }
}
