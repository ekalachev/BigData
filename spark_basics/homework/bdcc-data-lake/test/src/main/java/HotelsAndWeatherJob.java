import ch.hsr.geohash.GeoHash;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.*;
import utilities.Hotel;
import utilities.Weather;


import static de.cronn.reflection.util.PropertyUtils.getPropertyName;
import static java.lang.String.format;

public class HotelsAndWeatherJob {
    private static final String containerName = "m6sparkbasics";
    private static final String accountName = "bd201stacc";
    private static final String endpoint = "https://bd201stacc.blob.core.windows.net";
    private static final String sasToken = "sv=2020-04-08&st=2021-03-30T12%3A46%3A05Z&se=2031-03-31T12%3A46%3A00Z&sr=c&sp=rl&sig=H7H5Q2Tq5KL21wXPvIaI2mnVZYdoUEotK1Y%2Bca0NGNE%3D";


    public static void main(String[] args) {

        StructType hotelSchema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getId), DataTypes.LongType, false),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getAddress), DataTypes.StringType, false),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getCountry), DataTypes.StringType, false),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getCity), DataTypes.StringType, false),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getName), DataTypes.StringType, false),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getLatitude), DataTypes.DoubleType, true),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getLongitude), DataTypes.DoubleType, true),
                DataTypes.createStructField(getPropertyName(Hotel.class, Hotel::getGeoHash), DataTypes.StringType, true)
        });

        StructType weatherSchema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getLng), DataTypes.DoubleType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getLat), DataTypes.DoubleType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getGeoHash), DataTypes.StringType, true),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getAvg_tmpr_f), DataTypes.DoubleType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getAvg_tmpr_c), DataTypes.DoubleType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getWthr_date), DataTypes.StringType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getYear), DataTypes.IntegerType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getMonth), DataTypes.IntegerType, false),
                DataTypes.createStructField(getPropertyName(Weather.class, Weather::getDay), DataTypes.IntegerType, false)
        });

        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("HotelsAndWeatherJob")
                .getOrCreate();

        String baseUrl = "abfss://" + containerName + "@" + accountName + ".dfs.core.windows.net";

        Dataset<Hotel> hotelsDataset = getHotelDataset(hotelSchema, spark, baseUrl);
        System.out.println("Hotels count: " + hotelsDataset.count());

        Dataset<Weather> weatherDataset = getWeatherDataset(weatherSchema, spark, baseUrl);
        System.out.println("Weather count: " + weatherDataset.count());

        weatherDataset
                .drop(getPropertyName(Weather.class, Weather::getLat))
                .drop(getPropertyName(Weather.class, Weather::getLng))
                .joinWith(
                        hotelsDataset,
                        weatherDataset
                                .col(getPropertyName(Weather.class, Weather::getGeoHash))
                                .equalTo(hotelsDataset.col(getPropertyName(Hotel.class, Hotel::getGeoHash))), "inner")
                .show();

        spark.stop();
    }

    private static Dataset<Weather> getWeatherDataset(StructType schema, SparkSession spark, String baseUrl) {
        Encoder<Weather> weatherEncoder = Encoders.bean(Weather.class);

        Dataset<Weather> weatherDataset = spark
                .read()
                .schema(schema)
                .format("parquet")
                .option("header", "true")
                .load(baseUrl + "/weather")
                .as(weatherEncoder)
                .map((MapFunction<Weather, Weather>) weather -> {
                    weather.setGeoHash(GeoHash.geoHashStringWithCharacterPrecision(
                            weather.getLat(),
                            weather.getLng(),
                            4));

                    return weather;
                }, weatherEncoder);

        return weatherDataset
                .dropDuplicates(new String[]{
                        getPropertyName(Weather.class, Weather::getGeoHash),
                        getPropertyName(Weather.class, Weather::getWthr_date)
                });
    }

    private static Dataset<Hotel> getHotelDataset(StructType schema, SparkSession spark, String baseUrl) {
        Encoder<Hotel> hotelEncoder = Encoders.bean(Hotel.class);

        Dataset<Hotel> hotelsDataset = spark
                .read()
                .schema(schema)
                .format("csv")
                .option("header", "true")
                .load(baseUrl + "/hotels")
                .as(hotelEncoder)
                .map((MapFunction<Hotel, Hotel>) hotel -> {
                    if (hotel.getLongitude() == null || hotel.getLatitude() == null) {
                        findLatitudeAndLongitude(hotel);
                    }

                    hotel.setGeoHash(GeoHash.geoHashStringWithCharacterPrecision(
                            hotel.getLatitude(),
                            hotel.getLongitude(),
                            4));

                    return hotel;
                }, hotelEncoder);

        return hotelsDataset.dropDuplicates(getPropertyName(Hotel.class, Hotel::getId));
    }

    private static void findLatitudeAndLongitude(Hotel hotel) {
        String address = format("%s, %s, %s", hotel.getAddress(), hotel.getCity(), hotel.getCountry());

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("8b8ee57729df4c2796987adb5b9fd153");
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(address);
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition();

        hotel.setLatitude(firstResultLatLng.getLat());
        hotel.setLongitude(firstResultLatLng.getLng());
    }
}
