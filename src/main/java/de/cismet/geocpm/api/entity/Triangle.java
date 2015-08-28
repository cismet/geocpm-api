/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * GeoCPM triangle entity (GeoCPM API Spec v1.2 (05.10.2011)) holding three point entities that resemble the triangle
 * corners as well as a possible alternative breaking edge height value.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Triangle extends CommonEntity {

    //~ Instance fields --------------------------------------------------------

    private Point a;
    private Point b;
    private Point c;

    private double breakingEdgeA;
    private double breakingEdgeB;
    private double breakingEdgeC;

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
