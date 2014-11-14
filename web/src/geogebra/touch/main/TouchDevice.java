package geogebra.touch.main;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.touch.PhoneGapManager;
import geogebra.touch.gui.dialog.image.ImageInputDialogT;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.image.UploadImageDialog;
import geogebra.web.main.GDevice;

import com.google.gwt.user.client.Window;
import com.googlecode.gwtphonegap.client.connection.Connection;

public abstract class TouchDevice implements GDevice {

	@Override
	public boolean supportsExport() {
	    return false;
	}
	
	@Override
	public void copyEVtoClipboard(EuclidianViewW ev) {
		String image = ev.getExportImageDataUrl(3, true);
		String title = ev.getApplication().getKernel().getConstruction()
		        .getTitle();
		title = "".equals(title) ? "GeoGebraImage" : title;
		nativeShare(image, title);
	}

	native void nativeShare(String base64, String title)/*-{
		if ($wnd.android) {
			$wnd.android.share(base64, title, 'png');
		}
	}-*/;

	@Override
	public void setMinWidth(GeoGebraAppFrame frame) {
		frame.syncPanelSizes();
		frame.setStyleName("minWidth", Window.getClientWidth() <= 760);
	}

	@Override
	public boolean isOffline(AppW app) {
		return PhoneGapManager.getPhoneGap().getConnection().getType()
		        .equals(Connection.NONE);
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialogT(app);
	}
}
