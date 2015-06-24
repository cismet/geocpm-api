/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Reader;

import java.util.Collection;

import de.cismet.cids.custom.wupp.geocpm.api.entity.Triangle;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoCPMProject {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String description;
    private int annuality;
    private Reader geocpmEinReader;
    private Reader geocpmSubinfoReader;
    private Reader geocpmInfoReader;
    private Reader geocpmMaxReader;
    private Reader geocpmResultElementsReader;

    // NOTE: maybe this collection will consume too much space, then it has to be cached on disk
    private Collection<Triangle> triangles;
}
