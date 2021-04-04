package dto;

import lombok.Data;
import org.apache.commons.lang3.Validate;
import scala.Serializable;

@Data
public class Geo implements Serializable {
    public Geo(Double latitude, Double longitude) {
        Validate.notNull(latitude);
        Validate.notNull(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    private Double latitude;
    private Double longitude;
}
