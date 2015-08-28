/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api.transform;

/**
 * Base Transformer to transform object <code>I</code> to object <code>O</code>. Transformer implementations should be
 * implemented in a way that they are thread-safe. Moreover, they must have a default (no-args) constructor so that they
 * can be loaded by Service Providers, etc.
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
     * Tests beforehand if the input can be transformed by this transformer instance.
     *
     * @param   obj  the input object
     *
     * @return  whether this instance can handle the input object
     */
    boolean accept(I obj);
    /**
     * Does the actual transformation from object <code>I</code> to object <code>O</code>. Implementing classes should
     * be able to stop operations if interrupted.
     *
     * @param   obj  the input object
     *
     * @return  the output object
     */
    O transform(I obj);
}
