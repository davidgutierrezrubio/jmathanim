/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class stores info for drawing a curve with control points, tension...
 * It's independent of the renderer, which should translate it to proper drawing
 * commands
 *
 * @author David Gutiérrez davidgutierrezrubio@gmail.com
 */
public class JMPath implements Updateable, Stateable, Boxable {

	static public final int MATHOBJECT = 1; // Arc, line, segment...
	static public final int SVG_PATH = 2; // SVG import, LaTeX object...
	static public final int CONNECTED_COMPONENT = 3; // Connected, open component. Every path should be able to put in
														// this way
	public final CircularArrayList<JMPathPoint> jmPathPoints; // points from the curve
	public final CircularArrayList<Boolean> visiblePoints;// Whether this point is visible or not
	public int pathType; // Default value

	private JMPath pathBackup;

	public JMPath() {
		this(new ArrayList<Point>());
	}

	public JMPath(ArrayList<Point> points) {
		this.jmPathPoints = new CircularArrayList<>();
		this.setPoints(points);
//        this.controlPoints1 = new CircularArrayList<>();
//        this.controlPoints2 = new CircularArrayList<>();
		this.visiblePoints = new CircularArrayList<>();
		pathType = JMPath.MATHOBJECT;// Default value
	}

	public ArrayList<Point> getPoints() {
		ArrayList<Point> resul = new ArrayList<>();
		for (JMPathPoint jmp : jmPathPoints) {
			resul.add(jmp.p);
		}
		return resul;
	}

	public final void setPoints(ArrayList<Point> points) {
		this.jmPathPoints.clear();
		for (Point p : points) {
			this.jmPathPoints.add(new JMPathPoint(p, true, JMPathPointType.VERTEX));
		}
	}

	public JMPathPoint getJMPoint(int n) {
		return jmPathPoints.get(n);
	}

	public Point getControlPoint2(int n) {
		return jmPathPoints.get(n).cpEnter;
	}

	public int size() {
		return jmPathPoints.size();
	}

	public void addPoint(Point... points) {
		for (Point p : points) {
			jmPathPoints.add(new JMPathPoint(p, true, JMPathPointType.VERTEX));
		}
	}

	public void addJMPoint(JMPathPoint... points) {
		jmPathPoints.addAll(Arrays.asList(points));
	}

	public void addCPoint1(Point e) {
		jmPathPoints.get(jmPathPoints.size() - 1).cpExit.v.copyFrom(e.v);
	}

	public void addCPoint2(Point e) {
		jmPathPoints.get(jmPathPoints.size() - 1).cpEnter.v.copyFrom(e.v);
	}

	public void clear() {
		jmPathPoints.clear();
	}

	/**
	 * Remove interpolation points from path and mark it as no interpolated
	 */
	public void removeInterpolationPoints() {
		ArrayList<JMPathPoint> toRemove = new ArrayList<>();
		for (JMPathPoint p : jmPathPoints) {
			if (p.type == JMPathPointType.INTERPOLATION_POINT) {
				toRemove.add(p);
			}
		}
		jmPathPoints.removeAll(toRemove);// Remove all interpolation points
		// Now, restore old control points
		// for curved paths control points are modified so that a backup is necessary
		for (JMPathPoint p : jmPathPoints) {
			if (p.cpExitvBackup != null) {
				p.cpExit.v.copyFrom(p.cpExitvBackup);
				p.cpExitvBackup = null;
			}
			if (p.cpEntervBackup != null) {
				p.cpEnter.v.copyFrom(p.cpEntervBackup);
				p.cpEntervBackup = null;
			}
		}

//        generateControlPoints();//Recompute control points
	}

	@Override
	public String toString() {
		String resul = "#" + jmPathPoints.size() + ":  ";
		int counter = 0;
		for (JMPathPoint p : jmPathPoints) {
			resul += "< " + counter + " " + p.toString() + "> ";
			counter++;

		}
		return resul;
	}

	/**
	 * Add all points from a given path
	 *
	 * @param jmpathTemp
	 */
	public void addJMPointsFrom(JMPath jmpathTemp) {
		jmPathPoints.addAll(jmpathTemp.jmPathPoints);
	}

	/**
	 * Proceeds to subdidivide paths to ensure the path has exactly the given number
	 * of elements Invisible pieces of path are not interpolated. New generated
	 * points are marked as INTERPOLATION_POINT
	 *
	 * @param newNumPoints New number of points. Must be greater or equal than
	 *                     actual number of points in the path
	 */
	public void alignPathsToGivenNumberOfElements(int newNumPoints) {
		if (newNumPoints <= this.size()) {
			return; // Nothing to do here!
		}
		// First compute how many visible segments are
		ArrayList<JMPathPoint> pointsToInterpolate = new ArrayList<>();
		// Loop is from 1 because I want to add extra point to the first segment (point
		// 1) and not the last (point 0)
		for (int n = 1; n < 1 + jmPathPoints.size(); n++) {
			JMPathPoint p = jmPathPoints.get(n);
			if (p.isThisSegmentVisible) {
				pointsToInterpolate.add(p);
			}
		}
		int numVisibleSegments = pointsToInterpolate.size();
		int numPoints = jmPathPoints.size();
		int toCreate = newNumPoints - numPoints;// Number of points to create, to put in the numVisibleSegments segments

		int numDivs = (toCreate / numVisibleSegments); // Euclidean quotient
		int rest = toCreate % numVisibleSegments;// Euclidean rest

		for (int n = 0; n < pointsToInterpolate.size(); n++) {
			JMPathPoint p = pointsToInterpolate.get(n);
			p.numDivisions = numDivs + 1;// it is number of divisions, not number of points to be created. 1 new point
											// means divide in 2
			p.numDivisions += (n < rest ? 1 : 0);
		}
		// Once I have the number of segments to interpolate, subdivide all visible
		// segments

		for (JMPathPoint p : pointsToInterpolate) {
			int k = jmPathPoints.indexOf(p);// Position of this point in the path
			dividePathSegment(k, p.numDivisions);
		}

	}

	/**
	 * Divide path from point(k) to point(k-1) into an equal number of parts.
	 *
	 * @param k                   index of end point
	 * @param numDivForThisVertex Number of subdivisions
	 */
	public synchronized void dividePathSegment(int k, int numDivForThisVertex) {
		if (numDivForThisVertex < 2) {
			return;
		}
		double alpha = 1.0d / numDivForThisVertex;
		interpolateBetweenTwoPoints(k, alpha);
		dividePathSegment(k + 1, numDivForThisVertex - 1);// Keep subdividing until numDivForThisVertex=1
	}

//    public Point getPointAt(double t){
//        Point pointAt;
//        //First, get the segment
//         int numVisibleSegments=(int)(jmPathPoints.stream().filter((x)->x.isThisSegmentVisible).count()-1);
//        double tTotal=numVisibleSegments*t;
//        int n1=(int) Math.floor(tTotal);
//        double alpha=tTotal-n1;
//        JMPathPoint jmp1 = getJMPoint(n1);
//        JMPathPoint jmp2 = getJMPoint(n1+1);
//        if (jmp2.isCurved) {
//            //De Casteljau's Algorithm: https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
//            Point E = jmp1.p.interpolate(jmp1.cp1, alpha); //New cp1 of v1
//            Point G = jmp2.cp2.interpolate(jmp2.p, alpha); //New cp2 of v2
//            Point F = jmp1.cp1.interpolate(jmp2.cp2, alpha);
//            Point H = E.interpolate(F, alpha);//cp2 of interpolation point
//            Point J = F.interpolate(G, alpha);//cp1 of interpolation point
//            pointAt = H.interpolate(J, alpha); //Interpolation point
//        } else {
//            //Straight interpolation
//            pointAt = new Point(jmp1.p.v.interpolate(jmp2.p.v, alpha));
//        }
//        
//        return pointAt;
//    }
//    
	/**
	 * Adds an interpolation point at alpha parameter between point(k-1) and
	 * point(k) This method alters the control points of the points k-1 and k,
	 * storing them into cp1vbackup and cp2vbackup
	 *
	 * @param k     inded of the point to be interpolated
	 * @param alpha Alpha parameter
	 * @return The new JMPathPoint generated, and added to the Path
	 */
	public JMPathPoint interpolateBetweenTwoPoints(int k, double alpha) {
		JMPathPoint jmp1 = getJMPoint(k - 1);
		JMPathPoint jmp2 = getJMPoint(k);
		JMPathPoint interpolate;
		if (jmp2.isCurved) {
			// De Casteljau's Algorithm:
			// https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
			Point E = jmp1.p.interpolate(jmp1.cpExit, alpha); // New cp1 of v1
			Point G = jmp2.cpEnter.interpolate(jmp2.p, alpha); // New cp2 of v2
			Point F = jmp1.cpExit.interpolate(jmp2.cpEnter, alpha);
			Point H = E.interpolate(F, alpha);// cp2 of interpolation point
			Point J = F.interpolate(G, alpha);// cp1 of interpolation point
			Point K = H.interpolate(J, alpha); // Interpolation point
			interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
			interpolate.cpExit.v.copyFrom(J.v);
			interpolate.cpEnter.v.copyFrom(H.v);
			// Change control points from v1 and v2,save
			// backup values to restore after removing interpolation points
			if (jmp1.cpExitvBackup == null) {
				jmp1.cpExitvBackup = jmp1.cpExit.v;
			}
			if (jmp2.cpEntervBackup == null) {
				jmp2.cpEntervBackup = jmp2.cpEnter.v;
			}

			jmp1.cpExit.v.copyFrom(E.v);
			jmp2.cpEnter.v.copyFrom(G.v);

		} else {
			// Straight interpolation
			Point interP = new Point(jmp1.p.v.interpolate(jmp2.p.v, alpha));
			// Interpolation point is visible iff v2 is visible
			// Control points are by default the same as v1 and v2 (straight line)
			interpolate = new JMPathPoint(interP, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
		}
		interpolate.isCurved = jmp2.isCurved; // The new point is curved iff v2 is
		jmPathPoints.add(k, interpolate); // Now v2 is in position k+1!
		return interpolate;
	}

	/**
	 * Get point (interpolated if necessary) that lies at position alpha where
	 * alpha=0 denotes beginning of path and alpha=1 denotes the end
	 *
	 * @param alpha from 0 to 1, relative position inside the path
	 * @return A (copy of) point that lies in the curve at relative position alpha.
	 */
	public JMPathPoint getPointAt(double alpha) {
		while (alpha > 1) {
			alpha -= 1;
		}
		while (alpha < 0) {
			alpha += 1;
		}
		JMPathPoint resul;
		final double size = (jmPathPoints.get(0).isThisSegmentVisible ? alpha * size() : alpha * (size() - 1));
		int k = (int) Math.floor(size);
		double t = size - k;
		JMPathPoint v1 = getJMPoint(k);
		JMPathPoint v2 = getJMPoint(k + 1);
		resul = getJMPointBetween(v1, v2, t);
		return resul;
	}

	private JMPathPoint getJMPointBetween(JMPathPoint v1, JMPathPoint v2, double t) {
		JMPathPoint resul;
		if (v1.isCurved) {
			// De Casteljau's Algorithm:
			// https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
			Point E = v1.p.interpolate(v1.cpExit, t); // New cp1 of v1
			Point G = v2.cpEnter.interpolate(v2.p, t); // New cp2 of v2
			Point F = v1.cpExit.interpolate(v2.cpEnter, t);
			Point H = E.interpolate(F, t);// cp2 of interpolation point
			Point J = F.interpolate(G, t);// cp1 of interpolation point
//            resul = H.interpolate(J, t); //Interpolation point
			resul = JMPathPoint.curveTo(H.interpolate(J, t));

			resul.cpExit.copyFrom(J);
			resul.cpEnter.copyFrom(H);
		} else {
			resul = JMPathPoint.lineTo(v1.p.interpolate(v2.p, t));
			resul.cpExit.copyFrom(v1.p);
			resul.cpEnter.copyFrom(v2.p);
		}
		return resul;
	}

	/**
	 * Returns a full copy of the path. JMPathPoint objects are also copied
	 *
	 * @return A copy of the path
	 */
	public JMPath rawCopy() {
		JMPath resul = new JMPath();

		for (int n = 0; n < jmPathPoints.size(); n++) {
			resul.addJMPoint(jmPathPoints.get(n).copy());
		}
		resul.pathType = pathType;
		return resul;
	}

	/**
	 * Creates a copy of the path, with all their attributes. JMPathPoint objects
	 * are referenced instead of copied
	 *
	 * @return A copy of the path
	 */
	public JMPath copy() {
		JMPath resul = new JMPath();
		resul.jmPathPoints.addAll(jmPathPoints);

		// Copy attributes
		resul.pathType = pathType;
		return resul;

	}

	/**
	 * Cycle points in path.Point(0) becomes Point(step), Point(1) becomes
	 * Point(step+1)... Useful to align paths minimizing distances
	 *
	 * @param step
	 * @param direction
	 */
	public void cyclePoints(int step, int direction) {

//        if (!isClosed) {
//            if (direction == -1) {
//                step = this.size() - 1;
//            }
//        }
		JMPath tempPath = this.copy();
		jmPathPoints.clear();

		for (int n = 0; n < tempPath.size(); n++) {
			JMPathPoint point = tempPath.jmPathPoints.get(direction * n + step);
			if (direction < 0) // If reverse the path, we must swap control points
			{

				double cpTempX = point.cpExit.v.x;
				double cpTempY = point.cpExit.v.y;
				double cpTempZ = point.cpExit.v.z;
				point.cpExit.v.copyFrom(point.cpEnter.v);
				point.cpEnter.v.x = cpTempX;
				point.cpEnter.v.y = cpTempY;
				point.cpEnter.v.z = cpTempZ;
			}
			jmPathPoints.add(point);
		}

	}

	/**
	 * Reverse the points of the path
	 */
	public void reverse() {
		// TODO: implement this for more general path, using canonical form
		if (getNumberOfConnectedComponents() == 0) {
			this.cyclePoints(0, -1);
		}
		if (getNumberOfConnectedComponents() == 1) {
			this.jmPathPoints.get(0).isThisSegmentVisible = true;
			this.jmPathPoints.get(-1).isThisSegmentVisible = false;
			this.cyclePoints(-1, -1);
		}
	}

	/**
	 * Compute the sum of distance of points from aligned paths This distance should
	 * be minimized in order to Transform more smoothly. The paths should have the
	 * same number of points.
	 *
	 * @param path2 The other path
	 * @return Distance. Null if paths have different number of points
	 */
	public Double sumDistance(JMPath path2) {
		if (this.size() != path2.size()) {
			return null;
		}
		double resul = 0;
//        double sumSq = 0;
		double sum = 0;
		for (int n = 0; n < this.size(); n++) {
			Vec v1 = jmPathPoints.get(n).p.v;
			Vec v2 = path2.jmPathPoints.get(n).p.v;
			double dist = v1.minus(v2).norm();
//            sumSq += dist;
			sum += dist;
		}
		sum /= this.size();
//        resul = sumSq / this.size() - (sum * sum);
		resul = sum;
		return resul;
	}

	void shift(Vec shiftVector) {
		for (JMPathPoint p : jmPathPoints) {
			p.shift(shiftVector);
		}
	}

	/**
	 * Returns the width of the path
	 *
	 * @return The width. If the path is empty, returns 0.
	 */
	public double getWidth() {
		Rect r = getBoundingBox();
		if (r == null) {
			return 0;
		} else {
			return r.getWidth();
		}
	}

	/**
	 * Returns the height of the path
	 *
	 * @return The height. If the path is empty, returns 0.
	 */
	public double getHeight() {
		Rect r = getBoundingBox();
		if (r == null) {
			return 0;
		} else {
			return r.getHeight();
		}
	}

	public ArrayList<Point> getCriticalPoints() {
		ArrayList<Point> criticalPoints = new ArrayList<>();
		for (int n = 0; n < jmPathPoints.size(); n++) {
			JMPathPoint jmp = jmPathPoints.get(n);
			if (jmp.isThisSegmentVisible) {
				if (jmp.isCurved) {
					criticalPoints.addAll(getCriticalPoints(jmPathPoints.get(n - 1), jmp));
				}
				criticalPoints.add(jmp.p);

			}
		}
		return criticalPoints;
	}

	@Override
	public Rect getBoundingBox() {
		if (jmPathPoints.isEmpty()) {
			return null;
		}
		ArrayList<Point> points = new ArrayList<>();
		for (JMPathPoint jmp : jmPathPoints) {
			points.add(jmp.p.copy().thickness(2));
		}
//        List<Point> points = jmPathPoints.stream().map(p -> p.p).collect(Collectors.toList());
		points.addAll(getCriticalPoints());
//        for (Point p: getCriticalPoints()) {
//            System.out.println(""+p);
//        }
//        System.out.println("Rect "+Rect.make(points));

		return Rect.make(points);
	}

	@Override
	public boolean isEmpty() {
		return jmPathPoints.isEmpty();
	}

	/**
	 * Determine orientation of the path
	 *
	 * @return 1 if clockwise, -1 if counterwise
	 */
	public int getOrientation() {
		// https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order/1180256#1180256

		// get the point with lowest y and, in case of tie, max x
		int nmax = 0;
		double ymin = jmPathPoints.get(0).p.v.y;
		double xmax = jmPathPoints.get(0).p.v.x;
		for (int n = 0; n < jmPathPoints.size(); n++) {
			double y0 = jmPathPoints.get(n).p.v.y;
			double x0 = jmPathPoints.get(n).p.v.x;

			if ((y0 < ymin) || ((ymin == y0) && (x0 > xmax))) {
				ymin = y0;
				xmax = x0;
				nmax = n;
			}
		}
		Vec A = jmPathPoints.get(nmax).p.v;
		Vec B = jmPathPoints.get(nmax - 1).p.v;
		Vec C = jmPathPoints.get(nmax + 1).p.v;

		Vec AB = B.minus(A);
		Vec AC = C.minus(A);
		double cross = AB.cross(AC).z;
		int resul = (Math.signum(cross) < 0 ? -1 : 1);

		return resul;
	}

	@Override
	public int getUpdateLevel() {
		int resul = -1;
		for (JMPathPoint p : jmPathPoints) {
			resul = Math.max(resul, p.getUpdateLevel());
		}
		return resul;
	}

	@Override
	public void update(JMathAnimScene scene) {
		// This should do nothing, let their points to update by themselves
	}

	/**
	 * Replaces the JMPathPoints of the path with copies of points from another
	 * path. path.
	 *
	 * @param path The path with the JMPathPoints to add.
	 */
	public void setJMPoints(JMPath path) {
		this.clear();
		this.addJMPointsFrom(path.rawCopy());
		this.pathType = path.pathType;
		this.visiblePoints.clear();
		this.visiblePoints.addAll(path.visiblePoints);

	}

	@Override
	public void restoreState() {
		for (JMPathPoint p : jmPathPoints) {
			p.restoreState();
		}
		this.pathType = pathBackup.pathType;
		this.visiblePoints.clear();
		this.visiblePoints.addAll(pathBackup.visiblePoints);

	}

	@Override
	public void saveState() {
		pathBackup = new JMPath();
		for (JMPathPoint p : jmPathPoints) {
			p.saveState();
		}
		pathBackup.pathType = this.pathType;
		pathBackup.visiblePoints.clear();
		pathBackup.visiblePoints.addAll(pathBackup.visiblePoints);
	}

	/**
	 * Compute and returns a copy of the path (points referenced), given in
	 * canonical form. The canonical form is an array of open, connected paths. If
	 * the original path is closed, duplicates first vertex and opens it For each
	 * invisible segment, separate the path in two. This method allows better
	 * handling for Transform animations
	 *
	 * @return The array of paths
	 */
	public CanonicalJMPath canonicalForm() {
		if (this.size() == 0) {
			return new CanonicalJMPath();
		}
		ArrayList<JMPath> resul = new ArrayList<>();
		JMPath workPath = this.copy();
		Integer offset = null;
		// Find backwards first invisible segment, if there is not, we have a closed
		// path, so open it
		for (int n = 0; n < jmPathPoints.size(); n++) {
			JMPathPoint p = jmPathPoints.get(-n);
			if (!p.isThisSegmentVisible) {
				offset = n;
				break;
			}
		}
		if (offset == null) {
			// Ok, we have a CLOSED path with no invisible segments
			workPath.separate(0);
			offset = -1;
		}

		// A new path always begins with invisible point (that is, invisible segment TO
		// that point)
		// and ends with the previous to an invisible point
		JMPath connectedComponent = new JMPath();
		connectedComponent.pathType = JMPath.CONNECTED_COMPONENT;
		for (int n = 0; n < workPath.size(); n++) {
			JMPathPoint p = workPath.jmPathPoints.get(n - offset);
			if (!p.isThisSegmentVisible && connectedComponent.size() > 0) {
				resul.add(connectedComponent);
				connectedComponent = new JMPath();
				connectedComponent.pathType = JMPath.CONNECTED_COMPONENT;
			}
			connectedComponent.addJMPoint(p);
		}
		// add last component
		resul.add(connectedComponent);
		return new CanonicalJMPath(resul);
	}

	/**
	 * Separate the path in 2 disconnected components at point k Creates a new point
	 * a position k+1 Point k and k+1 share the same coordinates, k+1 is not
	 * visible.
	 *
	 * @param k Where to separate path
	 */
	public void separate(int k) {
		JMPathPoint p = getJMPoint(k);
		JMPathPoint pnew = p.copy();

		pnew.isThisSegmentVisible = false;
//        pnew.cp2.v.copyFrom(p.cp2.v);
		pnew.type = JMPathPointType.INTERPOLATION_POINT;
//        pnew.cp1.v.copyFrom(p.cp1.v);

		jmPathPoints.add(k + 1, pnew);
	}

	/**
	 * Return the number of connected components. A circle has number 0, which means
	 * a closed curve. An arc or segment has number 1 (an open curve) A figure with
	 * 2 separate curves has number 2, etc.
	 *
	 * @return The number of connected components.
	 */
	public int getNumberOfConnectedComponents() {
//        int resul = 0;
//        for (JMPathPoint p : jmPathPoints) {
//            if (!p.isThisSegmentVisible) {
//                resul++;
//            }
//        }
//        return resul;
		return (int) jmPathPoints.stream().filter((x) -> !x.isThisSegmentVisible).count();
	}

	/**
	 * Remove all hidden points followed by another hidden point This case may lead
	 * to 0-length segments, which can cause errors when transforming, so these
	 * (unnecessary) points are removed.
	 */
	public void removeConsecutiveHiddenVertices() {
		ArrayList<JMPathPoint> toRemove = new ArrayList<>();
		for (int n = 0; n < jmPathPoints.size(); n++) {
			JMPathPoint p1 = jmPathPoints.get(n);
			JMPathPoint p2 = jmPathPoints.get(n + 1);
			if (!p1.isThisSegmentVisible & !p2.isThisSegmentVisible) {
				toRemove.add(p1);
			}
		}
		jmPathPoints.removeAll(toRemove);
	}

	/**
	 * Returns a path with all points visible. This is used mainly for filling it
	 * properly
	 *
	 * @return A raw copy of the path with all points visible
	 */
	public JMPath allVisible() {
		JMPath resul = new JMPath();
		for (JMPathPoint p : jmPathPoints) {
			JMPathPoint pNew = p.copy();
			pNew.isThisSegmentVisible = true;
			resul.addJMPoint(pNew);
		}
		return resul;
	}

	/**
	 * Removes unnecessary points from the path. Duplicated points or consecutive
	 * hidden ones.
	 */
	public void distille() {
		// Delete points that are separated
		this.removeConsecutiveHiddenVertices();
		double epsilon = .000001;
		int n = 0;
		while (n < this.size()) {
			JMPathPoint p1 = this.getJMPoint(n);
			JMPathPoint p2 = this.getJMPoint(n + 1);
			if (p1.p.isEquivalentTo(p2.p, epsilon)) {
				p2.cpEnter.copyFrom(p1.cpEnter);
				p1.isThisSegmentVisible = true;
				p2.isCurved = p1.isCurved;
				this.jmPathPoints.remove(p1);
				n = 0;
			} else {
				n++;
			}
		}
	}

	/**
	 * Performs a comparison point-to-point with another path. This method is used
	 * to determine if another path is the affine transformation of another, for
	 * example.
	 *
	 * @param obj     The other path to compare.
	 * @param epsilon A threshold value to compare.
	 * @return True if all distances are smaller than the threshold value. False
	 *         otherwise
	 */
	public boolean isEquivalentTo(JMPath obj, double epsilon) {
		if (size() != obj.size()) {
			return false;
		}
		for (int n = 0; n < size(); n++) {
			JMPathPoint pa1 = getJMPoint(n);
			JMPathPoint pa2 = obj.getJMPoint(n);
			if (!pa1.isEquivalentTo(pa2, epsilon)) {
				return false;
			}
		}
		return true;
	}

	private Vec evaluateBezier(Vec P0, Vec P1, Vec P2, Vec P3, double t) {
		Vec a = P3.add(P2.mult(-3)).add(P1.mult(3)).add(P0.mult(-1));
		Vec b = P2.mult(3).add(P1.mult(-6)).add(P0.mult(3));
		Vec c = P1.mult(3).add(P0.mult(-3));
		Vec d = P0.copy();
		return d.add(c.mult(t)).add(b.mult(t * t)).add(a.mult(t * t * t));
	}

	private ArrayList<Point> getCriticalPoints(JMPathPoint pOrig, JMPathPoint pDst) {
		// https://floris.briolas.nl/floris/2009/10/bounding-box-of-cubic-bezier/
		ArrayList<Point> resul = new ArrayList<>();
		Vec P0 = pOrig.p.v;
		Vec P1 = pOrig.cpExit.v;
		Vec P2 = pDst.cpEnter.v;
		Vec P3 = pDst.p.v;
		Vec v;
		// a,b,c are the coefficients of the derivative of the Bezier function
		// f'(t)=at^2+bt+c
		Vec a = P3.add(P2.mult(-3)).add(P1.mult(3)).add(P0.mult(-1)).mult(3);
		Vec b = P2.mult(3).add(P1.mult(-6)).add(P0.mult(3)).mult(2);
		Vec c = P1.mult(3).add(P0.mult(-3));
		// We compute the roots for this derivative
		// First, for x
		double[] solsX = quadraticSolutions(a.x, b.x, c.x);
		for (double tCrit : solsX) {
			if ((tCrit > 0) && (tCrit < 1)) {
//                v = getJMPointBetween(pOrig, pDst, tCrit).p.v;
				if (pDst.isCurved) {
					v = evaluateBezier(P0, P1, P2, P3, tCrit);
				} else {
					v = pOrig.p.interpolate(pDst.p, tCrit).v;
				}

				resul.add(Point.at(v.x, v.y).drawColor(JMColor.BLUE));
			}
		}
		// Now for y
		double[] solsY = quadraticSolutions(a.y, b.y, c.y);
		for (double tCrit : solsY) {
			if ((tCrit > 0) && (tCrit < 1)) {
//                Vec v = getJMPointBetween(pOrig, pDst, tCrit).p.v;
				if (pDst.isCurved) {
					v = evaluateBezier(P0, P1, P2, P3, tCrit);
				} else {
					v = pOrig.p.interpolate(pDst.p, tCrit).v;
				}
				resul.add(Point.at(v.x, v.y).drawColor(JMColor.RED));
			}
		}
		return resul;
	}

	/**
	 * Computes the roots of a quadratic (maybe linear) equation
	 *
	 * @param a Coefficient of term with degree 2
	 * @param b Coefficient of term with degree 1
	 * @param c Coefficient of term with degree 0
	 * @return An array of results (empty if no solutions found).
	 */
	public double[] quadraticSolutions(double a, double b, double c) {
		// If a=0 we are dealing with a lineal
		if (a == 0) {
			if (c != 0) {
				return new double[] { -c / b };
			} else {
				return new double[] {};// No solutions
			}
		}
		// We deal with the case a!=0 so we have a quadratic equation
		double discriminant = b * b - 4 * a * c;

		if (discriminant < 0) {
			return new double[] {};// No real solutions
		}
		if (discriminant == 0) {// One solution
			return new double[] { -.5 * b / a };
		}
		if (discriminant > 0) {// two solutions
			double rdisc = Math.sqrt(discriminant);
			return new double[] { -.5 * (b + rdisc) / a, -.5 * (b - rdisc) / a };
		}

		return new double[] {};
	}

	/**
	 * Gets the points of the shape that lies in the boundary of the bounding box
	 *
	 * @param type What side of the bounding box: UPPER, LOWER, RIGHT or LEFT. The
	 *             other types return null.
	 * @return A List with all the points that lies in the specified side of the
	 *         boundary box
	 */
	public List<Point> getBorderPoints(Anchor.Type type) {
		Stream<Point> stream = getCriticalPoints().stream();
		Rect bb = getBoundingBox();
		List<Point> li = null;
		switch (type) {
		case UPPER:
			li = stream.filter(p -> p.v.y == bb.ymax).collect(Collectors.toList());
			break;
		case LOWER:
			li = stream.filter(p -> p.v.y == bb.ymin).collect(Collectors.toList());
			break;
		case RIGHT:
			li = stream.filter(p -> p.v.x == bb.xmax).collect(Collectors.toList());
			break;
		case LEFT:
			li = stream.filter(p -> p.v.x == bb.xmin).collect(Collectors.toList());
			break;
		}

		return li;
	}

}
