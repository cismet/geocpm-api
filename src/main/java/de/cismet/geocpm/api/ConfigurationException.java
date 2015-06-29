/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import lombok.Getter;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Getter
public class ConfigurationException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final Properties configuration;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>ConfigurationException</code> without detail message.
     */
    public ConfigurationException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public ConfigurationException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the specified detail message and exception.
     *
     * @param  msg  the detail message.
     * @param  ex   the causing exception
     */
    public ConfigurationException(final String msg, final Exception ex) {
        this(msg, ex, null);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the specified detail message and exception.
     *
     * @param  msg   the detail message.
     * @param  ex    the causing exception
     * @param  conf  the improper configuration
     */
    public ConfigurationException(final String msg, final Exception ex, final Properties conf) {
        super(msg, ex);

        this.configuration = conf;
    }
}
