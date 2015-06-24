/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;

import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 * By design changes the input project.
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
                log.debug("transforming [project=" + project + "|transformer=" + transformer + "]");
            }
            project = transformer.transform(project);
        }

        return project;
    }
}
