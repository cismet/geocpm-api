/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMConstants;
import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Point;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Triangle;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMMaxToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    public static final String MAX_REGEX = "^\\d+ +\\d+\\.\\d+$";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj != null) && (obj.getGeocpmMax() != null) && obj.getGeocpmMax().canRead()
                    && (obj.getTriangles() != null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        //J-
        // jalopy only supports java 1.6
        final List<Triangle> triangles;
        if(obj.getTriangles() instanceof List) {
            triangles = (List)obj.getTriangles();
        } else {
            triangles = new ArrayList<>(obj.getTriangles());
        }
        Collections.sort(triangles);

        for(int i = 0; i < triangles.size(); ++i) {
            if(i != triangles.get(i).getId()) {
                throw new IllegalStateException("expected triangle id '" + i + "' but found '" // NOI18N
                        + triangles.get(i).getId() + "', missing triangle?"); // NOI18N
            }
        }

        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmMax()))) {
            br.lines()
                    .filter(line -> line.matches(MAX_REGEX))
                    .forEach(line -> {
                        final String[] s = line.split(" +"); // NOI18N
                        final int tId = Integer.parseInt(s[0]);

                        if(tId >= triangles.size()) {
                            throw new IllegalStateException("wrong triangle reference: " + line); // NOI18N
                        }

                        triangles.get(tId).setWaterlevel(Double.parseDouble(s[1]));
                    });

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm ein file", ex); // NOI18N
        }
        //J+
    }
}
