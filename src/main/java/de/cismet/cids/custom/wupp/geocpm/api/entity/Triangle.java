/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.entity;

import lombok.Data;

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
public class Triangle extends CommonEntity {

    //~ Instance fields --------------------------------------------------------

    private Point a;
    private Point b;
    private Point c;

    private double breakingEdgeA;
    private double breakingEdgeB;
    private double breakingEdgeC;

    private double maxWaterlevel;
    // key=time in seconds since beginning, value waterlevel
    private Map<Double, Double> waterlevels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Triangle object.
     *
     * @param  id  DOCUMENT ME!
     * @param  a   DOCUMENT ME!
     * @param  b   DOCUMENT ME!
     * @param  c   DOCUMENT ME!
     */
    public Triangle(final int id, final Point a, final Point b, final Point c) {
        super(id);
        this.a = a;
        this.b = b;
        this.c = c;
        this.breakingEdgeA = 0;
        this.breakingEdgeB = 0;
        this.breakingEdgeC = 0;
    }

    /**
     * Creates a new Triangle object.
     *
     * @param  id             DOCUMENT ME!
     * @param  a              DOCUMENT ME!
     * @param  b              DOCUMENT ME!
     * @param  c              DOCUMENT ME!
     * @param  breakingEdgeA  DOCUMENT ME!
     * @param  breakingEdgeB  DOCUMENT ME!
     * @param  breakingEdgeC  DOCUMENT ME!
     */
    public Triangle(final int id,
            final Point a,
            final Point b,
            final Point c,
            final double breakingEdgeA,
            final double breakingEdgeB,
            final double breakingEdgeC) {
        super(id);
        this.a = a;
        this.b = b;
        this.c = c;
        this.breakingEdgeA = breakingEdgeA;
        this.breakingEdgeB = breakingEdgeB;
        this.breakingEdgeC = breakingEdgeC;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasBreakingEdge() {
        return (breakingEdgeA > 0) || (breakingEdgeB > 0) || (breakingEdgeC > 0);
    }
}
