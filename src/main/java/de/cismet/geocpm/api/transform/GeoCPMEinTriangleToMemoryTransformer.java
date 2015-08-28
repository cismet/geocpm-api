/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cismet.geocpm.api.GeoCPMConstants;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.entity.Point;
import de.cismet.geocpm.api.entity.Triangle;

/**
 * Triangle parser based on GeoCPM API Spec v1.2 (05.10.2011). Simple {@link Triangle} objects are produced for every
 * entry in the GeoCPM.ein file. Thus the result of this transformer is completely held in memory.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMEinTriangleToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TRIANGLE_LINE_REGEX = "(?:\\d+)(?:"                   // NOI18N
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+){7}(?:"                // NOI18N
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+\\.\\d\\d\\d){2}(?:(?:" // NOI18N
                + GeoCPMConstants.DEFAULT_FIELD_SEP + "-?\\d+\\.\\d\\d\\d){3})?";    // NOI18N

    //~ Methods ----------------------------------------------------------------

    /**
     * Requires GeoCPM projects to have the GeoCPM.ein file set and readable as well as the point entities from the
     * input file.
     *
     * @param   obj  the GeoCPM project
     *
     * @return  true if GeoCPM.ein file is set and readable as well as point entities are available, false otherwise
     */
    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj != null) && (obj.getGeocpmEin() != null) && obj.getGeocpmEin().canRead()
                    && (obj.getPoints() != null) && (obj.getPoints().size() > 0);
    }

    /**
     * Reads every triangle from the GeoCPM.ein input file of the given object. Does not hold a lock on the input file
     * thus concurrent modification (e.g. with an external editor) will yield unexpected results. <b>Will override
     * triangle entities that already exist.</b>
     *
     * @param   obj  the GeoCPM project to process
     *
     * @return  the very same GeoCPM project but with triangle entities from the input file.
     *
     * @throws  IllegalStateException  if the points for a triangle cannot be found
     * @throws  TransformException     if the input file cannot be read
     */
    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        final List<Point> points;
        if (obj.getPoints() instanceof List) {
            points = (List)obj.getPoints();
        } else {
            points = new ArrayList<>(obj.getPoints());
        }
        Collections.sort(points);

        for (int i = 0; i < points.size(); ++i) {
            if (i != points.get(i).getId()) {
                throw new IllegalStateException("expected point id '" + i + "' but found '" // NOI18N
                            + points.get(i).getId() + "', missing point?"); // NOI18N
            }
        }

        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmEin()))) {
            final List<Triangle> triangles = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(TRIANGLE_LINE_REGEX)) {
                    final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);
                    final int idA = Integer.parseInt(s[1]);
                    final int idB = Integer.parseInt(s[2]);
                    final int idC = Integer.parseInt(s[3]);

                    if ((idA >= points.size()) || (idB >= points.size()) || (idC >= points.size())) {
                        throw new IllegalStateException("wrong point reference: " + line); // NOI18N
                    }

                    final double beA;
                    final double beB;
                    final double beC;
                    // breaking edge present
                    if (s.length > 10) {
                        beA = Double.parseDouble(s[10]);
                        beB = Double.parseDouble(s[11]);
                        beC = Double.parseDouble(s[12]);
                    } else {
                        beA = beB = beC = 0;
                    }

                    triangles.add(new Triangle(
                            Integer.parseInt(s[0]),
                            points.get(idA),
                            points.get(idB),
                            points.get(idC),
                            beA,
                            beB,
                            beC));
                }
            }

            obj.setTriangles(triangles);

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm.ein", ex); // NOI18N
        }
    }
}
