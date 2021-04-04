package services;

import dto.Geo;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import providers.LatLngSearchProvider;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class GeoServiceTests {
    @Mock private LatLngSearchProvider mockedLatLngSearchProvider;

    private GeoService geoService;

    @BeforeEach
    void setUp() {
        this.geoService = new GeoServiceImpl(this.mockedLatLngSearchProvider);
    }

    static Stream<Geo> geoNULLs() {
        Geo geo1 = new Geo(0.0, 0.0);
        geo1.setLatitude(null);

        Geo geo2 = new Geo(0.0, 0.0);
        geo2.setLongitude(null);

        return Stream.of(null, geo1, geo2);
    }

    @ParameterizedTest
    @MethodSource("geoNULLs")
    @DisplayName("Given incorrect arguments, when 'getGeoHash', then throws exception")
    public void getGeoHash_IncorrectArguments(Geo geo) {
        assertThrows(NullPointerException.class, () -> geoService.getGeoHash(geo));
    }


    static Stream<Pair<Geo, String>> geoCorrectData() {
        return Stream.of(
                new Pair<>(new Geo(38.796997, -76.30522), "dqcs"),
                new Pair<>(new Geo(43.36319, -70.47717), "drty"));
    }

    @ParameterizedTest
    @MethodSource("geoCorrectData")
    @DisplayName("Given correct argument, when 'getGeoHash', then returns geo hash")
    public void getGeoHash_CorrectArguments(Pair<Geo, String> geo) {
        String geoHash = geoService.getGeoHash(geo.getValue0());

        assertEquals(geo.getValue1(), geoHash);
    }

    static Stream<Arguments> countryCityAddressNULLs() {
        return Stream.of(
                arguments(null, null, null),
                arguments("1", null, null),
                arguments("1", "2", null)
        );
    }

    @ParameterizedTest
    @MethodSource("countryCityAddressNULLs")
    @DisplayName("Given NULL string arguments, when 'findGeoCoordinates', then throws exception")
    public void findGeoCoordinates_NullStringArguments(String country, String city, String address) {
        assertThrows(NullPointerException.class, () -> geoService.findGeoCoordinates(country, city, address));
    }


    static Stream<Arguments> countryCityAddressEmpties() {
        return Stream.of(
                arguments("", "", ""),
                arguments("1", "", ""),
                arguments("1", "2", "")
        );
    }

    @ParameterizedTest
    @MethodSource("countryCityAddressEmpties")
    @DisplayName("Given empty string arguments, when 'findGeoCoordinates', then throws exception")
    public void findGeoCoordinates_EmptyStringArguments(String country, String city, String address) {
        assertThrows(IllegalArgumentException.class, () -> geoService.findGeoCoordinates(country, city, address));
    }

    @Test
    @DisplayName("Given correct arguments, when 'findGeoCoordinates', then returns geo data")
    public void findGeoCoordinates_CorrectArguments() {
        // Given
        String country = "US";
        String city = "Silverthorne";
        String address = "Melody Lodge Cabins";

        String fullAddress = format("%s, %s, %s", address, city, country);
        Geo expectedGeo = new Geo(39.840855, -106.23464);

        Mockito
                .when(mockedLatLngSearchProvider.search(fullAddress))
                .thenReturn(expectedGeo);

        // Act
        Geo geo = geoService.findGeoCoordinates(country, city, address);

        // Assert
        assertEquals(expectedGeo.getLatitude(), geo.getLatitude());
        assertEquals(expectedGeo.getLongitude(), geo.getLongitude());
    }
}
