package services;

import dto.Geo;
import dto.Hotel;
import dto.Weather;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.Repository;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelWeatherServiceTests {
    @Mock(serializable = true)
    private GeoService mockedGeoService;
    @Mock(serializable = true)
    private Repository<Hotel> mockedHotelRepository;
    @Mock(serializable = true)
    private Repository<Weather> mockedWeatherRepository;

    private SparkSession spark;

    private HotelWeatherService hotelWeatherService;

    private PodamFactory factory;

    @BeforeEach
    void setUp() {
        this.factory = new PodamFactoryImpl();

        this.spark = SparkSession
                .builder()
                .master("local[1]")
                .appName("jUnit")
                .getOrCreate();

        this.hotelWeatherService = new HotelWeatherServiceImpl(
                this.mockedGeoService, this.mockedHotelRepository, this.mockedWeatherRepository);
    }

    @AfterEach
    void tearDown() {
        this.spark.stop();
    }

    @Test
    @DisplayName("Returns weather with populated geoHash and removed duplicates when: 'getWeatherWithPopulatedGeoHashAndRemovedDuplicates'")
    public void getWeatherWithPopulatedGeoHashAndRemovedDuplicates() {
        // Given
        Encoder<Weather> encoder = Encoders.bean(Weather.class);

        Mockito
                .when(this.mockedWeatherRepository.getEncoder())
                .thenReturn(encoder);

        Weather w1 = factory.manufacturePojo(Weather.class);
        w1.setLat(38.796997);
        w1.setLng(-76.30522);
        w1.setWthr_date("2000-05-30");

        Weather w2 = factory.manufacturePojo(Weather.class);
        w2.setLat(38.796997);
        w2.setLng(-76.30522);
        w2.setWthr_date("2000-05-30");

        List<Weather> weather = new ArrayList<>(asList(w1, w2));

        Dataset<Weather> weatherDataset = this.spark
                .createDataset(weather, encoder);

        Mockito
                .when(this.mockedWeatherRepository.getDataset())
                .thenReturn(weatherDataset);

        lenient()
                .when(this.mockedGeoService.getGeoHash(any()))
                .thenReturn("dqcs");

        // Act
        Dataset<Weather> resultWeatherDataset = this.hotelWeatherService.getWeatherWithPopulatedGeoHashAndRemovedDuplicates();

        // Assert
        assertEquals(1, resultWeatherDataset.count());
        assertEquals("dqcs", resultWeatherDataset.first().getGeoHash());
    }

    @Test
    @DisplayName("Returns hotels with populated missed latitude, longitude, geoHash and removed duplicates when: 'getHotelWithPopulatedGeoHashAndRemovedDuplicates'")
    public void getHotelWithPopulatedGeoHashAndRemovedDuplicates() {
        // Given
        Encoder<Hotel> encoder = Encoders.bean(Hotel.class);

        Mockito
                .when(this.mockedHotelRepository.getEncoder())
                .thenReturn(encoder);

        Hotel h1 = factory.manufacturePojo(Hotel.class);
        h1.setLatitude(null);
        h1.setLongitude(null);
        h1.setCountry("US");
        h1.setCity("Elko");
        h1.setAddress("Super 8 Elko Nv");
        h1.setId("123");

        Hotel h2 = factory.manufacturePojo(Hotel.class);
        h2.setLatitude(null);
        h2.setLongitude(null);
        h1.setCountry("US");
        h1.setCity("Elko");
        h1.setAddress("Rodeway Inn");
        h2.setId("123");

        Hotel h3 = factory.manufacturePojo(Hotel.class);
        h3.setLatitude(38.796997);
        h3.setLongitude(-76.30522);
        h3.setId("1234");

        List<Hotel> hotels = new ArrayList<>(asList(h1, h2, h3));

        Dataset<Hotel> hotelDataset = this.spark
                .createDataset(hotels, encoder);

        Mockito
                .when(this.mockedHotelRepository.getDataset())
                .thenReturn(hotelDataset);

        lenient()
                .when(this.mockedGeoService.getGeoHash(any()))
                .thenReturn("dqcs");

        // Hotel 1
        Geo geo1 = new Geo(40.843764, -115.751013);
        lenient()
                .when(this.mockedGeoService.findGeoCoordinates(h1.getCountry(), h1.getCity(), h1.getAddress()))
                .thenReturn(geo1);
        lenient()
                .when(this.mockedGeoService.getGeoHash(geo1))
                .thenReturn("9rm8");

        // Hotel 2
        Geo geo2 = new Geo(40.83441, -115.760897);
        lenient()
                .when(this.mockedGeoService.findGeoCoordinates(h2.getCountry(), h2.getCity(), h2.getAddress()))
                .thenReturn(geo2);
        lenient()
                .when(this.mockedGeoService.getGeoHash(geo2))
                .thenReturn("9rm8");

        // Act
        Dataset<Hotel> resultHotelDataset = this.hotelWeatherService.getHotelWithPopulatedGeoHashAndRemovedDuplicates();

        // Assert
        assertEquals(2, resultHotelDataset.count());

        Set<String> geoHashes = new HashSet<>(asList("dqcs", "9rm8"));
        for (Hotel hotel : resultHotelDataset.collectAsList()) {
            assertTrue(geoHashes.contains(hotel.getGeoHash()));
        }
    }
}
