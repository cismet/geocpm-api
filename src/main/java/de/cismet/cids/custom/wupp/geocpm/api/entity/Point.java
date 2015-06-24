/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.entity;

import lombok.Getter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Getter
public class Point extends CommonEntity {

    //~ Instance fields --------------------------------------------------------

    private final double x;
    private final double y;
    private final double z;

    //~ Constructors -----------------------------------------------------------

    /**
     * lombok is not able to resolve required args from super (yet).
     *
     * @param  id  DOCUMENT ME!
     * @param  x   DOCUMENT ME!
     * @param  y   DOCUMENT ME!
     * @param  z   DOCUMENT ME!
     */
    public Point(final long id, final double x, final double y, final double z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
