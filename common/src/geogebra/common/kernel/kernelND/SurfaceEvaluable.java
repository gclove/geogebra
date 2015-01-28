package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.CoordsFloat3;

/**
 * Surface with parametric equation z=f(x1,x2,...,xn)
 * @author Mathieu
 */
public interface SurfaceEvaluable {

	/**
	 * @param u first parameter
	 * @param v second parameter
	 * @return point for parameters u, v
	 */
	public Coords evaluatePoint(double u, double v);
	
	/**
	 * @param u first parameter
	 * @param v second parameter
	 * @param point point set for parameters u, v
	 */
	public void evaluatePoint(double u, double v, CoordsFloat3 point);
	
	/**
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param normal
	 *            normal. WARNING: the normal may not have norm=1
	 * @return true if the normal is defined
	 */
	public boolean evaluateNormal(double u, double v, CoordsFloat3 normal);

	/**
	 * @param i index of parameter
	 * @return minimal value for i-th parameter
	 */
	public double getMinParameter(int i);
	/**
	 * @param i index of parameter
	 * @return maximal value for i-th parameter
	 */
	public double getMaxParameter(int i);
	
	/**
	 * set derivatives (if not already done)
	 */
	public void setDerivatives();
}
