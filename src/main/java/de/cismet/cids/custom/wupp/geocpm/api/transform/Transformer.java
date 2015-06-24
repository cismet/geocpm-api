/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

import de.cismet.cids.custom.wupp.geocpm.api.Cancellable;

/**
 * DOCUMENT ME!
 *
 * @param    <I>  transformer input type
 * @param    <O>  transformer output type
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface Transformer<I, O> extends Cancellable {

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
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    O transform(I obj);
}
