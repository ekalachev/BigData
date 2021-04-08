package dto;

public class HotelWeather {

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public Double getAvg_tmpr_f() {
        return avg_tmpr_f;
    }

    public void setAvg_tmpr_f(Double avg_tmpr_f) {
        this.avg_tmpr_f = avg_tmpr_f;
    }

    public Double getAvg_tmpr_c() {
        return avg_tmpr_c;
    }

    public void setAvg_tmpr_c(Double avg_tmpr_c) {
        this.avg_tmpr_c = avg_tmpr_c;
    }

    public String getWthr_date() {
        return wthr_date;
    }

    public void setWthr_date(String wthr_date) {
        this.wthr_date = wthr_date;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
