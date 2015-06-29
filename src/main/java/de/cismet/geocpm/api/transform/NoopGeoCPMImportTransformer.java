/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.geocpm.api.GeoCPMProject;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class NoopGeoCPMImportTransformer implements GeoCPMImportTransformer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final Object obj) {
        return true;
    }

    @Override
    public Collection<GeoCPMProject> transform(final Object obj) {
        return new ArrayList(0);
    }
}
