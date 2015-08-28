/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.geocpm.api.GeoCPMConstants;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.entity.Point;

/**
 * Point parser based on GeoCPM API Spec v1.2 (05.10.2011). Simple {@link Point} objects are produced for every point in
 * the GeoCPM.ein file. Thus the result of this transformer is completely held in memory.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class GeoCPMEinPointToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String POINT_LINE_REGEX = "(?:\\d+)(?:" + GeoCPMConstants.DEFAULT_FIELD_SEP // NOI18N
                + "-?\\d+\\.\\d\\d\\d){3}";                                                          // NOI18N

    //~ Methods ----------------------------------------------------------------

    /**
     * Requires the GeoCPM.ein file to be available.
     *
     * @param   obj  a GeoCPM project
     *
     * @return  true if the GeoCPM.ein file is set and available for read, false otherwise
     */
    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj != null) && (obj.getGeocpmEin() != null) && obj.getGeocpmEin().canRead();
    }

    /**
     * Reads every point from the GeoCPM.ein input file of the given object. Does not hold a lock on the input file thus
     * concurrent modification (e.g. with an external editor) will yield unexpected results. <b>Will override point
     * entities that already exist.</b>
     *
     * @param   obj  the GeoCPM project to process
     *
     * @return  the very same GeoCPM project but with point entities from the input file.
     *
     * @throws  TransformException  if the input file cannot be read
     */
    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmEin()))) {
            final List<Point> points = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(POINT_LINE_REGEX)) {
                    final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);
                    points.add(new Point(
                            Integer.parseInt(s[0]),
                            Double.parseDouble(s[1]),
                            Double.parseDouble(s[2]),
                            Double.parseDouble(s[3])));
                }
            }

            obj.setPoints(points);

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm ein file", ex); // NOI18N
        }
    }
}
