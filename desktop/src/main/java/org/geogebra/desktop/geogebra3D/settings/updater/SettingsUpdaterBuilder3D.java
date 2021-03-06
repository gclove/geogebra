package org.geogebra.desktop.geogebra3D.settings.updater;

import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.settings.updater.SettingsUpdaterBuilderD;

/**
 * Builds the SettingsUpdater object for the 3D apps.
 */
public class SettingsUpdaterBuilder3D extends SettingsUpdaterBuilderD {

	public SettingsUpdaterBuilder3D(App3D app) {
		super(app);
	}

	@Override
	protected FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdater3D(getApp());
	}

	@Override
	protected App3D getApp() {
		return (App3D) super.getApp();
	}
}
