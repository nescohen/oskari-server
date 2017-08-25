package downloadbasket.data;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class NormalWayDownloads {
	private final Logger LOGGER = LogFactory.getLogger(NormalWayDownloads.class);
	private Set<String> downloads = new HashSet<>();

	/**
	 * Add normal way download.
	 * 
	 * @param download
	 */
	public void addDownload(String download) {
		if (!downloads.contains(download)) {
			downloads.add(download);
		}
	}

	/**
	 * Cheks if dowload is cropped with BBOX. Normal way uses BBOX as the
	 * cropping method. If not, Filter is used.
	 * 
	 * @param croppingMode
	 *            the cropping mode
	 * @param croppingLayer
	 *            the cropping layer
	 * @return
	 */
	public boolean isBboxCropping(String croppingMode, String croppingLayer) {
		boolean isNormalWay = false;

		for (String cropping : downloads) {
			if (cropping.equals(croppingMode)) {
				isNormalWay = true;
				break;
			}
		}

		LOGGER.debug("Checked normal way download. Cropping mode: " + croppingMode + ", cropping layer: "
				+ croppingLayer + ". Is normal way download: " + isNormalWay + ".");

		return isNormalWay;
	}
}
