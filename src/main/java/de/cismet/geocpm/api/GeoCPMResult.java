/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import lombok.Data;

import java.io.File;

import java.util.Collection;

import de.cismet.geocpm.api.entity.Result;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
public class GeoCPMResult {

    //~ Instance fields --------------------------------------------------------

    private final int annuality;

    private File geocpmInfo;
    private File geocpmSubinfo;
    private File geocpmMax;
    private File geocpmResultElements;

    private Collection<Result> results;
}
