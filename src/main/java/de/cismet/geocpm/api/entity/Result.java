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

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Result extends CommonEntity {

    //~ Instance fields --------------------------------------------------------

    private double maxWaterlevel;
    // key=time in seconds since beginning, value waterlevel
    private Map<Double, Double> waterlevels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Result object.
     *
     * @param  id  DOCUMENT ME!
     */
    public Result(final int id) {
        super(id);
    }

    /**
     * Creates a new Result object.
     *
     * @param  id             DOCUMENT ME!
     * @param  maxWaterlevel  DOCUMENT ME!
     * @param  waterlevels    DOCUMENT ME!
     */
    public Result(final int id, final double maxWaterlevel, final Map<Double, Double> waterlevels) {
        super(id);

        this.maxWaterlevel = maxWaterlevel;
        this.waterlevels = waterlevels;
    }
}
