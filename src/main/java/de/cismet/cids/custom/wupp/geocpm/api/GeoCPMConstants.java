/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
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

    public static final String CFG_IMPORTER_FQCN = "geocpm.import.importer.fqcn";                             // NOI18N
    public static final String CFG_PIPELINE_PARALLEL_EXECS = "geocpm.import.pipeline.noOfParallelExecutions"; // NOI18N
    public static final String CFG_PIPELINE_IMPORTER_FQCN_PREFIX = "geocpm.import.pipeline.transformer.";     // NOI18N
}
