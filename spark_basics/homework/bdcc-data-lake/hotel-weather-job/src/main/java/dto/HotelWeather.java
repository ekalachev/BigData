package dto;

import lombok.Data;

@Data
public class HotelWeather {
    public HotelWeather(Hotel hotel, Weather weather) {

        this.id = hotel.getId();
        this.name = hotel.getName();
        this.country = hotel.getCountry();
        this.city = hotel.getCity();
        this.address = hotel.getAddress();
        this.latitude = hotel.getLatitude();
        this.longitude = hotel.getLongitude();
        this.geoHash = hotel.getGeoHash();
        this.avg_tmpr_f = weather.getAvg_tmpr_f();
        this.avg_tmpr_c = weather.getAvg_tmpr_c();
        this.wthr_date = weather.getWthr_date();
        this.year = weather.getYear();
        this.month = weather.getMonth();
        this.day = weather.getDay();
    }

    private String id;
    private String name;
    private String country;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private String geoHash;
    private Double avg_tmpr_f;
    private Double avg_tmpr_c;
    private String wthr_date;
    private Integer year;
    private Integer month;
    private Integer day;
}
