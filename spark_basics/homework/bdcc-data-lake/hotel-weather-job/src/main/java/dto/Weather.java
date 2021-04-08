package dto;

public class Weather {
    private Double lng;
    private Double lat;
    private String geoHash;
    private Double avg_tmpr_f;
    private Double avg_tmpr_c;
    private String wthr_date;
    private Integer year;
    private Integer month;
    private Integer day;

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
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
