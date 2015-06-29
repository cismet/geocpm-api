/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

import lombok.Getter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class TransformException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final Object transformedObject;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>TransformException</code> without detail message.
     */
    public TransformException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>TransformException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public TransformException(final String msg) {
        this(null, msg, null);
    }

    /**
     * Constructs an instance of <code>TransformException</code> with the specified detail message and exception.
     *
     * @param  msg  the detail message.
     * @param  ex   the causing exception
     */
    public TransformException(final String msg, final Exception ex) {
        this(null, msg, ex);
    }

    /**
     * Creates a new TransformException object.
     *
     * @param  transformedObject  DOCUMENT ME!
     * @param  message            DOCUMENT ME!
     * @param  cause              DOCUMENT ME!
     */
    public TransformException(final Object transformedObject, final String message, final Exception cause) {
        super(message, cause);
        this.transformedObject = transformedObject;
    }
}
