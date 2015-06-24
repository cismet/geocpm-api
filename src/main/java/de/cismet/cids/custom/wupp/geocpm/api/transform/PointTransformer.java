/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMConstants;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Point;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class PointTransformer implements LineTransformer<Point> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean canTransform(final String section) {
        return GeoCPMConstants.SECTION_POINTS.equals(section);
    }

    @Override
    public Point transform(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("line must not be null"); // NOI18N
        }

        final String[] parts = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);

        if (parts.length != 4) {
            throw new IllegalStateException(
                "line does not contain four entries, incompatible geocpm version? " // NOI18N
                        + "[line="
                        + line
                        + "]");                                                     // NOI18N
        }

        return null;
    }
}
