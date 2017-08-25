package downloadbasket.actions;

import downloadbasket.helpers.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.OskariLayerServiceIbatisImpl;
import fi.nls.oskari.util.ResponseHelper;

/**
 * Handles the cropping of the data before adding it to the download basket.
 * Gets layer attributes and geometry for the cropping process.
 */

@OskariActionRoute("GetFeatureForCropping")
public class GetFeatureForCropping extends ActionHandler {

	private final Logger LOGGER = LogFactory.getLogger(GetFeatureForCropping.class);

	private static final String PARAM_LAYERS = "layers";
	private static final String PARAM_X = "x";
	private static final String PARAM_Y = "y";
	private static final String PARAM_BBOX = "bbox";
	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_SRS = "srs";
	private static final String PARAM_ID = "id";

	private static OskariLayerService mapLayerService;

	@Override
	public void handleAction(final ActionParameters params) throws ActionException {
		OskariLayer oskariLayer = mapLayerService.find(params.getHttpParam(PARAM_ID));

		if (oskariLayer != null) {
			String url = oskariLayer.getUrl();

			String wmsUrl = Helpers.getGetFeatureInfoUrlForProxy(url, params.getHttpParam(PARAM_SRS),
					params.getHttpParam(PARAM_BBOX), params.getHttpParam(PARAM_WIDTH),
					params.getHttpParam(PARAM_HEIGHT), params.getHttpParam(PARAM_X), params.getHttpParam(PARAM_Y),
					params.getHttpParam(PARAM_LAYERS));

			LOGGER.debug("Details of the data cropping feature");
			try {

				HttpURLConnection con = IOHelper.getConnection(wmsUrl, user, pass);
				con.setRequestProperty("Accept-Charset", "UTF-8");
				final String data = IOHelper.readString(con, "UTF-8");

				JSONObject json = new JSONObject(data);

				ResponseHelper.writeResponse(params, json);

			} catch (JSONException e) {
				throw new ActionException("Could not get cropping");
			} catch (MalformedURLException e) {
				throw new ActionException("Could not get cropping");
			} catch (IOException e) {
				throw new ActionException("Could not get cropping");
			}
		} else {
			throw new ActionException("Could not get cropping");
		}

	}

	@Override
	public void init() {
		super.init();

		mapLayerService = new OskariLayerServiceIbatisImpl();
	}
}