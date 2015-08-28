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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.geocpm.api.GeoCPMConstants;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.GeoCPMUtilities;
import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;

/**
 * GeoCPM water level over time parser based on GeoCPM API Spec v1.2 (05.10.2011). Simple {@link Result} objects are
 * produced for every result from the input files. Thus the result of this transformer is completely held in memory.
 * Processes every annuality.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMResultElementsToMemoryTransformer implements GeoCPMProjectTransformer {

    //~ Methods ----------------------------------------------------------------

    /**
     * Requires the input object to have results available and triangles set. Moreover, every result must have the
     * ResultElements.aus file set and readable.
     *
     * @param   obj  the GeoCPM object
     *
     * @return  true if the input object has results available and triangles set and the ResultElements.aus file is set
     *          and readable
     */
    @Override
    public boolean accept(final GeoCPMProject obj) {
        boolean accept = (obj != null) && (obj.getResults() != null) && (obj.getTriangles() != null);

        if (accept) {
            for (final GeoCPMResult result : obj.getResults()) {
                accept = accept && (result.getGeocpmResultElements() != null)
                            && result.getGeocpmResultElements().canRead();
            }
        }

        return accept;
    }

    /**
     * Reads every water level from the ResultsElements.aus input file of the given object. Does not hold a lock on the
     * input file thus concurrent modification (e.g. with an external editor) will yield unexpected results. <b>Will
     * override existing water level time series.</b> Any other possibly existing result will remain as it is.
     *
     * @param   obj  the GeoCPM project to process
     *
     * @return  the very same GeoCPM project but with result entities containing water level time series from the input
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
            try(final BufferedReader br = new BufferedReader(new FileReader(result.getGeocpmResultElements()))) {
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

                // zero based linecount in accordance with spec v1.2
                int tId = 0;
                String line;
                while ((line = br.readLine()) != null) {
                    if (tId >= triangles.size()) {
                        throw new IllegalStateException("wrong triangle reference: " + line); // NOI18N)
                    }

                    final String[] s = line.split(GeoCPMConstants.DEFAULT_FIELD_SEP);

                    final Map<Double, Double> levels = new HashMap<>();
                    for (int i = 1; i < (s.length - 1); i += 2) {
                        final double time = Double.parseDouble(s[i]);
                        final double waterlevel = Double.parseDouble(s[i + 1]);

                        levels.put(time, waterlevel);
                    }

                    final Result r;
                    if (tId >= results.size()) {
                        // ensure that if results are there they are of equal count
                        throw new IllegalStateException("wrong results reference: " + line); // NOI18N
                    } else {
                        r = results.get(tId);
                    }
                    r.setWaterlevels(levels);
                    results.set(tId, r);

                    tId++;
                }
            } catch (final IOException ex) {
                throw new TransformException("cannot read geocpm result elements file", ex); // NOI18N
            }
        }

        return obj;
    }
}
