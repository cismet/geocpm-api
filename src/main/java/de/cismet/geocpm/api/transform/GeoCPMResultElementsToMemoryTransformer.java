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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.geocpm.api.GeoCPMConstants;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.entity.Triangle;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMResultElementsToMemoryTransformer implements GeoCPMProjectTransformer {

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
        return (obj != null) && (obj.getGeocpmResultElements() != null) && obj.getGeocpmResultElements().canRead()
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

        try(final BufferedReader br = new BufferedReader(new FileReader(obj.getGeocpmResultElements()))) {
            // zero based linecount in accordance with spec v1.2
            int lineNo = 0;
            String line;
            while((line = br.readLine()) != null) {
                if(lineNo >= triangles.size()) {
                    throw new IllegalStateException("wrong triangle reference: " + line); // NOI18N)
                }

                final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);


                final Map<Double, Double> levels = new HashMap<>();
                for(int i = 1; i < s.length - 1; i+=2) {
                    final double time = Double.parseDouble(s[i]);
                    final double waterlevel = Double.parseDouble(s[i + 1]);

                    levels.put(time, waterlevel);
                }

                triangles.get(lineNo).setWaterlevels(levels);

                lineNo++;
            }

            return obj;
        } catch (final IOException ex) {
            throw new TransformException("cannot read geocpm result elements file", ex); // NOI18N
        }
        //J+
    }
}
