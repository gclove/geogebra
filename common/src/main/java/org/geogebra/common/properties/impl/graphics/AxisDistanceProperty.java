package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.StringProperty;

/**
 * This property controls the distance of an axis numbering
 */
public class AxisDistanceProperty extends AbstractProperty implements StringProperty {

    private EuclidianSettings euclidianSettings;
    private Kernel kernel;
    private int axis;

    /**
     * Constructs an xAxis property.
     *
     * @param localization      localization for the title
     * @param euclidianSettings euclidian settings
     * @param kernel            kernel
     * @param label             label of the axis
     * @param axis              the axis for the numbering distance will be set
     */
	public AxisDistanceProperty(Localization localization, Kernel kernel,
			EuclidianSettings euclidianSettings, String label, int axis) {
        super(localization, label);
        this.euclidianSettings = euclidianSettings;
        this.kernel = kernel;
        this.axis = axis;
    }

    @Override
    public String getValue() {
        return "" + euclidianSettings.getAxisNumberingDistance(axis);
    }

    @Override
    public void setValue(String value) {
        GeoNumberValue distance = null;
        if (!value.trim().equals("")) {
            distance = kernel.getAlgebraProcessor()
                    .evaluateToNumeric(value.trim(), ErrorHelper.silent());
        }
        euclidianSettings.setAxisNumberingDistance(axis, distance);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
