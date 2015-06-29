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
public class GeoCPMEinTriangleToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TRIANGLE_LINE_REGEX = "(?:\\d+)(?:"
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+){7}(?:"
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+\\.\\d\\d\\d){2}(?:(?:"
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+\\.\\d\\d\\d){3})?";

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
        return (obj != null) && (obj.getGeocpmEin() != null) && obj.getGeocpmEin().canRead()
                    && (obj.getPoints() != null) && (obj.getPoints().size() > 0);
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

        final List<Point> points;
        if(obj.getPoints() instanceof List) {
            points = (List)obj.getPoints();
        } else {
            points = new ArrayList<>(obj.getPoints());
        }
        Collections.sort(points);

        for(int i = 0; i < points.size(); ++i) {
            if(i != points.get(i).getId()) {
                throw new IllegalStateException("expected point id '" + i + "' but found '" // NOI18N
                        + points.get(i).getId() + "', missing point?"); // NOI18N
            }
        }

        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmEin()))) {

            final Triangle[] triangles = br.lines()
                    .filter(line -> line.matches(TRIANGLE_LINE_REGEX))
                    .map(line -> {
                        final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);
                        final int idA = Integer.parseInt(s[1]);
                        final int idB = Integer.parseInt(s[2]);
                        final int idC = Integer.parseInt(s[3]);

                        if(idA >= points.size() || idB >= points.size() || idC >= points.size()) {
                            throw new IllegalStateException("wrong point reference: " + line); // NOI18N
                        }

                        final double beA;
                        final double beB;
                        final double beC;
                        // breaking edge present
                        if(s.length > 10) {
                            beA = Double.parseDouble(s[10]);
                            beB = Double.parseDouble(s[11]);
                            beC = Double.parseDouble(s[12]);
                        } else {
                            beA = beB = beC = 0;
                        }

                        return new Triangle(
                                Integer.parseInt(s[0]),
                                points.get(idA),
                                points.get(idB),
                                points.get(idC),
                                beA,
                                beB,
                                beC);
                    })
                    .toArray(Triangle[]::new);
            obj.setTriangles(Arrays.asList(triangles));

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm.ein", ex); // NOI18N
        }
        //J+
    }
}
