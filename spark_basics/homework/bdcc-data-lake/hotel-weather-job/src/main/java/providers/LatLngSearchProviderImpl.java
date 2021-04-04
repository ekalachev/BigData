package providers;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import dto.Geo;
import org.apache.commons.lang3.Validate;


public class LatLngSearchProviderImpl implements LatLngSearchProvider {
    // TODO move it to config file
    private final String apiKey = "8b8ee57729df4c2796987adb5b9fd153";

    public Geo search(String fullAddress) {
        Validate.notEmpty(fullAddress);

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(apiKey);
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(fullAddress);
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition();

        return new Geo(firstResultLatLng.getLat(), firstResultLatLng.getLng());
    }
}
