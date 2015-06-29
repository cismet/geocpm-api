/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;

import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 * By design changes the input project. If a call is canceled the state of the GeoCPMProject will remain as it is at the
 * very point the interrupt took place or the execution stopped respectively. There is no rollback.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@AllArgsConstructor
@Slf4j
public class GeoCPMProjectPipeline implements Callable<GeoCPMProject> {

    //~ Instance fields --------------------------------------------------------

    @NonNull
    private GeoCPMProject project;
    @NonNull
    private final List<GeoCPMProjectTransformer> transformers;

    //~ Methods ----------------------------------------------------------------

    @Override
    public GeoCPMProject call() throws Exception {
        for (final GeoCPMProjectTransformer transformer : transformers) {
            if (Thread.interrupted()) {
                if (log.isDebugEnabled()) {
                    log.debug("project transformer pipeline interrupted"); // NOI18N
                }

                break;
            }

            if (!transformer.accept(project)) {
                throw new ConfigurationException("project transformer does not accept project: " + project); // NOI18N
            }

            if (log.isDebugEnabled()) {
                log.debug("transforming [project=" + project + "|transformer=" + transformer + "]"); // NOI18N
            }
            project = transformer.transform(project);
        }

        return project;
    }
}
