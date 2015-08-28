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
 * Transformer reflecting the filter pattern to enable chaining of transformations of GeoCPM projects so that they
 * ultimately are processed as needed.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface GeoCPMProjectTransformer extends Transformer<GeoCPMProject, GeoCPMProject> {
}
