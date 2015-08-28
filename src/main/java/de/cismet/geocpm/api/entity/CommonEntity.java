/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Base entity of GeoCPM entities (GeoCPM API Spec v1.2 (05.10.2011)).
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@RequiredArgsConstructor
@Getter
public abstract class CommonEntity implements Comparable<CommonEntity> {

    //~ Instance fields --------------------------------------------------------

    private final int id;

    //~ Methods ----------------------------------------------------------------

    @Override
    public int compareTo(final CommonEntity o) {
        if (o.getId() > id) {
            return -1;
        } else if (o.getId() < id) {
            return 1;
        } else {
            return 0;
        }
    }
}
