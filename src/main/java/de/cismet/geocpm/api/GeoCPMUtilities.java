/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;

/**
 * Some helpers and utilities for GeoCPM project processing.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMUtilities {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMUtilities object.
     */
    private GeoCPMUtilities() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Sorts the triangles of the given GeoCPMProject.
     *
     * @param  proj  the project in which to sort the triangles
     */
    public static void sortTriangles(final GeoCPMProject proj) {
        final List<Triangle> triangles;

        if (proj.getTriangles() != null) {
            if (proj.getTriangles() instanceof List) {
                triangles = (List)proj.getTriangles();
            } else {
                triangles = new ArrayList<>(proj.getTriangles());
            }
            Collections.sort(triangles);
            proj.setTriangles(triangles);
        }
    }

    /**
     * Sorts the results of a given GeoCPMProject.
     *
     * @param  proj  DOCUMENT ME!
     */
    public static void sortResults(final GeoCPMProject proj) {
        if (proj.getResults() != null) {
            for (final GeoCPMResult gr : proj.getResults()) {
                final List<Result> results;

                if (gr.getResults() != null) {
                    if (gr.getResults() instanceof List) {
                        results = (List)gr.getResults();
                    } else {
                        results = new ArrayList<>(gr.getResults());
                    }
                    Collections.sort(results);
                    gr.setResults(results);
                }
            }
        }
    }

    /**
     * Ensures that a triangle list does not lack a triangle, i.e. the ids don't have holes. E.g. 1,2,4 is missing 3.
     * This operation sorts the triangles beforehand, see {@link #sortTriangles(de.cismet.geocpm.api.GeoCPMProject)}.
     *
     * @param   proj  the project to check
     *
     * @throws  IllegalStateException  if a triangle is missing inbetween
     *
     * @see     #sortTriangles(de.cismet.geocpm.api.GeoCPMProject)
     */
    public static void checkMissingTriangles(final GeoCPMProject proj) {
        sortTriangles(proj);

        final List<Triangle> triangles = (List)proj.getTriangles();

        for (int i = 0; i < triangles.size(); ++i) {
            if (i != triangles.get(i).getId()) {
                throw new IllegalStateException("expected triangle id '" + i + "' but found '" // NOI18N
                            + triangles.get(i).getId() + "', missing triangle?"); // NOI18N
            }
        }
    }
}
