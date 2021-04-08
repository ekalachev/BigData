package providers;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import conf.SystemVariables;
import dto.Geo;
import org.apache.commons.lang3.Validate;


public class LatLngSearchProviderImpl implements LatLngSearchProvider {
    private SystemVariables systemVariables;

    public LatLngSearchProviderImpl(SystemVariables systemVariables) {
        Validate.notNull(systemVariables);

        this.systemVariables = systemVariables;
    }

    public Geo search(String fullAddress) {
        Validate.notEmpty(fullAddress);

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(systemVariables.getOpencageApiKey());
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(fullAddress);
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition();

        return new Geo(firstResultLatLng.getLat(), firstResultLatLng.getLng());
    }
}
