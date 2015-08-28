/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

import java.util.Collection;

import de.cismet.geocpm.api.entity.Point;
import de.cismet.geocpm.api.entity.Triangle;

/**
 * Base class holding the core info of a GeoCPM project (GeoCPM API Spec v1.2 (05.10.2011)). Basically, it contains the
 * relevant content of a GeoCPM.ein file and serves as IO for the transformers.
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
    private File geocpmEin;

    // NOTE: maybe these collections will consume too much space, then it has to be cached on disk
    private Collection<Triangle> triangles;
    private Collection<Point> points;
    private Collection<GeoCPMResult> results;
}
