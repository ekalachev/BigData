package utilities;

import lombok.Data;

@Data
public class Hotel {
    private String id;
    private String name;
    private String country;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private String geoHash;
}
