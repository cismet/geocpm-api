/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

/**
 * DOCUMENT ME!
 *
 * @param    <I>  transformer input type
 * @param    <O>  transformer output type
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface Transformer<I, O> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean accept(I obj);
    /**
     * Implementing classes should be able to stop operations if interrupted.
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    O transform(I obj);
}
