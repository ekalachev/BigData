package dto;

import lombok.Data;

@Data
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
}
