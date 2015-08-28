/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import java.util.Collection;

import de.cismet.geocpm.api.GeoCPMProject;

/**
 * Transformer for turning arbitrary input objects to a collection of GeoCPM projects that then might be processed by
 * this framework.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface GeoCPMImportTransformer extends Transformer<Object, Collection<GeoCPMProject>> {
}
