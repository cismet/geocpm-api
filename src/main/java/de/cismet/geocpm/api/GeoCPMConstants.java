/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 * Constants related to GeoCPM API Spec v1.2 (05.10.2011) and importer configuration.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class GeoCPMConstants {

    //~ Static fields/initializers ---------------------------------------------

    public static final String SECTION_CONFIG = "Configuration";      // NOI18N
    public static final String SECTION_POINTS = "POINTS";             // NOI18N
    public static final String SECTION_TRIANGLES = "TRIANGLES";       // NOI18N
    public static final String SECTION_CURVES = "CURVES";             // NOI18N
    public static final String SECTION_SOURCE_DRAIN = "SOURCE-DRAIN"; // NOI18N
    public static final String SECTION_MANHOLES = "MANHOLES";         // NOI18N
    public static final String SECTION_MARKED = "MARKED";             // NOI18N
    public static final String SECTION_RAINCURVE = "RAINCURVE";       // NOI18N
    public static final String SECTION_BK_CONNECT = "BK-CONNECT";     // NOI18N

    public static final String DEFAULT_FIELD_SEP = "     "; // NOI18N

    /**
     * Configuration property for the fully qualified name of the importer class to be used. The given class must
     * implement the {@link GeoCPMImportTransformer} interface.
     */
    public static final String CFG_IMPORTER_FQCN = "geocpm.import.importer.fqcn"; // NOI18N
    /** Configuration property for the number of parallel pipeline executions, simple integer required. */
    public static final String CFG_PIPELINE_PARALLEL_EXECS = "geocpm.import.pipeline.noOfParallelExecutions"; // NOI18N
    /**
     * Configuration property prefix for the pipeline transformers that shall be executed in sequence (in the resulting
     * {@link GeoCPMProjectPipeline}. The prefix shall be followed by an integer (the "place" of the configured
     * transformer in the sequence of execution). The value of such a configuration object shall be the fully qualified
     * name of the importer class to be used. The given class must implement the {@link GeoCPMProjectTransformer}
     * interface.<br>
     * <br>
     * E.g. <code>
     * geocpm.import.pipeline.transformer.3=de.cismet.geocpm.api.transform.NoopGeoCPMProjectTransformer</code> would be
     * a valid configuration entry that renders the noop transformer third in the sequence of transformation.
     */
    public static final String CFG_PIPELINE_IMPORTER_FQCN_PREFIX = "geocpm.import.pipeline.transformer.";       // NOI18N
}
