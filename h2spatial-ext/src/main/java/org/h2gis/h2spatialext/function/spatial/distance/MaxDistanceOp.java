/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * h2patial is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * h2spatial is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * h2spatial. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.h2gis.h2spatialext.function.spatial.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.Arrays;
import java.util.HashSet;

/**
 * An operation to compute the maximum distance between two geometries.
 * If the geometry 1 and geometry 2 is the same geometry the operation will 
 * return the distance between the two vertices most far from each other in that geometry.
 * 
 * @author Erwan Bocher
 */
public class MaxDistanceOp {

    private final Geometry geomA;
    private final Geometry geomB;
    private MaxDistanceFilter maxDistanceFilter;
    private boolean sameGeom = false;

    public MaxDistanceOp(Geometry geomA, Geometry geomB) {
        this.geomA = geomA;
        this.geomB = geomB;
    }

    /**
     * Compute the max distance
     */
    private void computeMaxDistance() {
        HashSet<Coordinate> coordinatesA = new HashSet<Coordinate>();
        coordinatesA.addAll(Arrays.asList(geomA.convexHull().getCoordinates()));
        Geometry fullHull = geomA.getFactory().createGeometryCollection(new Geometry[]{geomA, geomB}).convexHull();
        maxDistanceFilter = new MaxDistanceFilter(coordinatesA);
        fullHull.apply(maxDistanceFilter);
    }

    /**
     * Return the max distance
     *
     * @return
     */
    public Double getDistance() {
        if (geomA == null || geomB == null) {
            return null;
        }
        if (geomA.isEmpty() || geomB.isEmpty()) {
            return 0.0;
        }
        if (geomA.equals(geomB)) {
            sameGeom = true;
        }
        if (maxDistanceFilter == null) {
            computeMaxDistance();
        }
        return maxDistanceFilter.getDistance();

    }

    /**
     * Return the two coordinates to build the max distance line
     *
     * @return
     */
    public Coordinate[] getCoordinatesDistance() {
        if (geomA == null || geomB == null) {
            return null;
        }
        if (geomA.isEmpty() || geomB.isEmpty()) {
            return null;
        }
        if (geomA.equals(geomB)) {
            sameGeom = true;           
        }

        if (maxDistanceFilter == null) {
            computeMaxDistance();
        }
        return maxDistanceFilter.getCoordinatesDistance();

    }

    public class MaxDistanceFilter implements CoordinateFilter {

        private double distance = Double.MIN_VALUE;
        private final HashSet<Coordinate> coordsToExclude;
        private Coordinate startCoord = null;
        private Coordinate endCoord = null;

        /**
         * Compute the max distance between two geometries
         *
         * @param coordsToExclude
         */
        public MaxDistanceFilter(HashSet<Coordinate> coordsToExclude) {
            this.coordsToExclude = coordsToExclude;
        }

        @Override
        public void filter(Coordinate coord) {
            if (sameGeom) {
                coordsToExclude.remove(coord);
                updateDistance(coord);
            } else {
                if (!coordsToExclude.contains(coord)) {
                    updateDistance(coord);
                }
            }
        }

        /**
         * Update the distance and the coordinates
         *
         * @param coord
         */
        private void updateDistance(Coordinate coord) {
            for (Coordinate coordinate : coordsToExclude) {
                double currentDistance = coord.distance(coordinate);
                if (currentDistance > distance) {
                    distance = currentDistance;
                    startCoord = coordinate;
                    endCoord = coord;
                }
            }
        }

        /**
         * Return the maximum distance
         *
         * @return
         */
        public double getDistance() {
            return distance;
        }

        /**
         * Return the maximum distance as two coordinates. 
         * Usefull to draw it as a line
         *
         * @return
         */
        public Coordinate[] getCoordinatesDistance() {
            if (startCoord == null || endCoord == null) {
                return null;
            }
            return new Coordinate[]{startCoord, endCoord};
        }
    }

}