/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api.transform;

import de.cismet.cids.custom.wupp.geocpm.api.entity.CommonEntity;

/**
 * DOCUMENT ME!
 *
 * @param    <E>  the specific type of entity this transformer produces
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface LineTransformer<E extends CommonEntity> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   section  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean canTransform(final String section);
    /**
     * DOCUMENT ME!
     *
     * @param   line  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    E transform(final String line);
}
