package dto;

import org.apache.commons.lang3.Validate;
import scala.Serializable;

public class Geo implements Serializable {
    public Geo(Double latitude, Double longitude) {
        Validate.notNull(latitude);
        Validate.notNull(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    private Double latitude;
    private Double longitude;

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
}
