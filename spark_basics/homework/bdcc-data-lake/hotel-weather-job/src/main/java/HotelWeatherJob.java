import dto.Hotel;
import dto.HotelWeather;
import dto.Weather;
import repositories.HotelRepositoryImpl;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import repositories.Repository;
import repositories.WeatherRepositoryImpl;
import scala.Tuple2;
import services.GeoService;
import services.GeoServiceImpl;
import providers.LatLngSearchProviderImpl;
import services.HotelWeatherService;
import services.HotelWeatherServiceImpl;

import java.io.IOException;

import static de.cronn.reflection.util.PropertyUtils.getPropertyName;

public class HotelWeatherJob {
    // TODO move it to config file
    private static final String containerName = "m6sparkbasics";
    private static final String accountName = "bd201stacc";

    public static void main(String[] args) throws IOException {
//        System.getenv().forEach((k, v) -> {
//            System.out.println(k + ":" + v);
//        });

//        System.out.println(System.getenv("HADOOP_HOME"));

        // TODO move it to IoC
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("HotelsAndWeatherJob")
                .getOrCreate();

        GeoService geoService = new GeoServiceImpl(new LatLngSearchProviderImpl());

        String baseUrl = "abfss://" + containerName + "@" + accountName + ".dfs.core.windows.net";

        Repository<Hotel> hotelRepository = new HotelRepositoryImpl(spark, baseUrl);
        Repository<Weather> weatherRepository = new WeatherRepositoryImpl(spark, baseUrl);

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

//        hotelWeatherDataset.show();

        String outputPath = new java.io.File("./tmp/partitioned_hotel_weather/").getCanonicalPath();

        hotelWeatherDataset
                .write()
                .partitionBy(
                        getPropertyName(HotelWeather.class, HotelWeather::getYear),
                        getPropertyName(HotelWeather.class, HotelWeather::getMonth),
                        getPropertyName(HotelWeather.class, HotelWeather::getDay)
                )
                .mode("overwrite")
                .parquet(outputPath);

        spark.stop();
    }
}
