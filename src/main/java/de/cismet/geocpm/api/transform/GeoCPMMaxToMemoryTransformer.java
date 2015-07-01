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
import java.util.List;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.GeoCPMUtilities;
import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;

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
        boolean accept = (obj != null) && (obj.getResults() != null) && (obj.getTriangles() != null);

        if (accept) {
            for (final GeoCPMResult result : obj.getResults()) {
                accept = accept && (result.getGeocpmMax() != null) && result.getGeocpmMax().canRead();
            }
        }

        return accept;
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
        GeoCPMUtilities.checkMissingTriangles(obj);
        final List<Triangle> triangles = (List)obj.getTriangles();

        for (final GeoCPMResult result : obj.getResults()) {
            //J-
            // jalopy only supports java 1.6
            try(final BufferedReader br = new BufferedReader(new FileReader(result.getGeocpmMax()))) {
                String line;

                final List<Result> results;
                if(result.getResults() == null) {
                    // init result collection
                    results = new ArrayList<>(triangles.size());
                    for(int i = 0; i < triangles.size(); ++i) {
                        results.add(new Result(i));
                    }
                    result.setResults(results);
                } else {
                    GeoCPMUtilities.sortResults(obj);
                    results = (List)result.getResults();
                }

                result.setResults(results);

                while((line = br.readLine()) != null) {
                    if(line.matches(MAX_REGEX)) {
                        final String[] s = line.split(" +"); // NOI18N
                        final int tId = Integer.parseInt(s[0]);

                        if(tId >= triangles.size()) {
                            throw new IllegalStateException("wrong triangle reference: " + line); // NOI18N
                        }

                        final Result r;
                        if(tId >= results.size()) {
                            // ensure that if results are there they are of equal count
                            throw new IllegalStateException("wrong results reference: " + line); // NOI18N
                        } else {
                            r = results.get(tId);
                        }
                        r.setMaxWaterlevel(Double.parseDouble(s[1]));
                        results.set(tId, r);
                    }
                }


            } catch (final IOException ex) {
                throw new TransformException("cannot read geocpm max file", ex); // NOI18N
            }
            //J+
        }

        return obj;
    }
}
