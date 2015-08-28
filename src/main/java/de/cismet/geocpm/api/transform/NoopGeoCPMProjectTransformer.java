/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import de.cismet.geocpm.api.GeoCPMProject;

/**
 * NoOp transformer that accepts any GeoCPM project and returns the input object without any processing.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class NoopGeoCPMProjectTransformer implements GeoCPMProjectTransformer {

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
        return true;
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
        return obj;
    }
}
