/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.geogebra3D.web.euclidianForPlane;

import geogebra.common.euclidian.EuclidianControllerCreator;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianControllerCreatorForPlane;
import geogebra.common.kernel.Kernel;
import geogebra.web.euclidian.EuclidianControllerW;

/**
 * controller for view for plane
 */
public class EuclidianControllerForPlaneW extends EuclidianControllerW {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerForPlaneW(Kernel kernel) {
		super(kernel);
	}


	@Override
	protected EuclidianControllerCreator newCreator(){
		return new EuclidianControllerCreatorForPlane(this);
	}
}
