/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>. H2GIS is developed by CNRS
 * <http://www.cnrs.fr/>.
 *
 * This code is part of the H2GIS project. H2GIS is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; version 3.0 of
 * the License.
 *
 * H2GIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details <http://www.gnu.org/licenses/>.
 *
 *
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */
package org.h2gis.functions.spatial.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueVarchar;
import org.h2gis.api.DeterministicScalarFunction;
import org.h2gis.utilities.GeometryMetaData;
import org.h2gis.utilities.TableLocation;

public class FindGeometryMetadata extends DeterministicScalarFunction{

    public FindGeometryMetadata() {
        addProperty(PROP_REMARKS, "Extract geometry metadata from its create table signature."
                + "eg : GEOMETRY; GEOMETRY(POINT); GEOMETRY(POINT Z); GEOMETRY(POINTZ, 4326)...");
    }

    @Override
    public String getJavaStaticMethod() {
        return "extractMetadata";
    }

    /**
     * Extract the geometry metadata from its create table signature
     *
     * Examples:
     *
     * POINT
     * POINT Z
     * POINT ZM
     *
     * @param geometryTableSignature
     * @return an array of values with the following values order
     * values[0] =   GEOMETRY_TYPE 
     * values[1] = COORD_DIMENSION 
     * values[2] = SRID 
     * values[3] =   TYPE
     * @throws SQLException
     */
    public static ValueArray extractMetadata(String geometryTableSignature) throws SQLException {
        GeometryMetaData geomMeta = GeometryMetaData.createMetadataFromGeometryType(geometryTableSignature);
        Value[] values = new Value[3];
        values[0] = ValueVarchar.get(String.valueOf(geomMeta.getGeometryTypeCode()));
        values[1] = ValueVarchar.get(String.valueOf(geomMeta.getDimension()));
        values[2] = ValueVarchar.get(geomMeta.getSfs_geometryType());
        return ValueArray.get(values, null);
    }

}
