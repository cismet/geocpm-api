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
 * GeoCPM maximum water level parser based on GeoCPM API Spec v1.2 (05.10.2011). Simple {@link Result} objects are
 * produced for every result in the GeoCPMMax.aus file. Thus the result of this transformer is completely held in
 * memory. Processes every annuality.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMMaxToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Static fields/initializers ---------------------------------------------

    public static final String MAX_REGEX = "^\\d+ +\\d+\\.\\d+$"; // NOI18N

    //~ Methods ----------------------------------------------------------------

    /**
     * Requires the input object to have results available and triangles set. Moreover, every result must have the
     * GeoCPMMax.aus file set and readable.
     *
     * @param   obj  the GeoCPM object
     *
     * @return  true if the input object has results available and triangles set and the GeoCPMMax.aus file is set and
     *          readable
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
     * Reads every maximum water level from the GeoCPMMax.aus input file of the given object. Does not hold a lock on
     * the input file thus concurrent modification (e.g. with an external editor) will yield unexpected results. <b>Will
     * override existing maximum water levels.</b> Any other possibly existing result will remain as it is.
     *
     * @param   obj  the GeoCPM project to process
     *
     * @return  the very same GeoCPM project but with result entities containing maximum water levels from the input
     *          file.
     *
     * @throws  IllegalStateException  if a result cannot be related to a triangle properly
     * @throws  TransformException     if the results file cannot be read
     */
    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        GeoCPMUtilities.checkMissingTriangles(obj);
        final List<Triangle> triangles = (List)obj.getTriangles();

        for (final GeoCPMResult result : obj.getResults()) {
            try(final BufferedReader br = new BufferedReader(new FileReader(result.getGeocpmMax()))) {
                String line;

                final List<Result> results;
                if (result.getResults() == null) {
                    // init result collection
                    results = new ArrayList<>(triangles.size());
                    for (int i = 0; i < triangles.size(); ++i) {
                        results.add(new Result(i));
                    }
                    result.setResults(results);
                } else {
                    GeoCPMUtilities.sortResults(obj);
                    results = (List)result.getResults();
                }

                result.setResults(results);

                while ((line = br.readLine()) != null) {
                    if (line.matches(MAX_REGEX)) {
                        final String[] s = line.split(" +"); // NOI18N
                        final int tId = Integer.parseInt(s[0]);

                        if (tId >= triangles.size()) {
                            throw new IllegalStateException("wrong triangle reference: " + line); // NOI18N
                        }

                        final Result r;
                        if (tId >= results.size()) {
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
                throw new TransformException("cannot read geocpm max file", ex);                 // NOI18N
            }
        }

        return obj;
    }
}
