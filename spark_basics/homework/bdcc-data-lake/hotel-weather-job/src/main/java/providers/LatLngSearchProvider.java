package providers;

import dto.Geo;

import java.io.Serializable;

public interface LatLngSearchProvider extends Serializable {
    Geo search(String fullAddress);
}
