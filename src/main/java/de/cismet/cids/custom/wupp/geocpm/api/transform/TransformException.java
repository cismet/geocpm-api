package de.cismet.cids.custom.wupp.geocpm.api.transform;

import lombok.Getter;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class TransformException extends RuntimeException {
    
    @Getter
    private final Object transformedObject;
    
    /**
     * Creates a new instance of <code>TransformException</code> without detail message.
     */
    public TransformException () {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>TransformException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TransformException(String msg) {
        this(null, msg, null);
    }

    /**
     * Constructs an instance of <code>TransformException</code> with the specified detail message and exception.
     * @param msg the detail message.
     * @param ex  the causing exception
     */
    public TransformException(String msg, Exception ex) {
        this(null, msg, ex);
    }

    public TransformException(Object transformedObject, String message, Exception cause) {
        super(message, cause);
        this.transformedObject = transformedObject;
    }
    
    
}
