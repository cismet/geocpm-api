/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api;

import de.cismet.cids.custom.wupp.geocpm.api.entity.CommonEntity;

/**
 * DOCUMENT ME!
 *
 * @param    <E>  an implementation of the <code>CommonEntity</code>
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface EntityProcessor<E extends CommonEntity> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   entity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean canProcess(E entity);
    /**
     * DOCUMENT ME!
     *
     * @param   entity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    E doProcess(E entity);
}
