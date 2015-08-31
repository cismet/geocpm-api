/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.geocpm.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.Lookup;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.cismet.commons.concurrency.CismetConcurrency;
import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.commons.utils.ProgressEvent;
import de.cismet.commons.utils.ProgressListener;

import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.Transformer;

/**
 * Main point of operation of the framework. Does the actual import according to the provided configuration. Every
 * invocation of <code>doImport</code> is separated thus it is safe to call from multiple threads arbitrary times.
 * However, the number of parallel executions is limited to not exceed resources thus it might not yield better results
 * if called too often. Please note, that this "limitation" is independent of the
 * {@link GeoCPMConstants#CFG_PIPELINE_PARALLEL_EXECS} configuration.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class GeoCPMImportOrchestrator {

    //~ Static fields/initializers ---------------------------------------------

    private static final ThreadGroup GEOCPM_THREADGROUP;

    static {
        GEOCPM_THREADGROUP = new ThreadGroup("geocpm-import-group"); // NOI18N
    }

    //~ Instance fields --------------------------------------------------------

    private final Properties defaultConfiguration;

    private final ExecutorService internalExecutor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMImportOrchestrator object.
     */
    private GeoCPMImportOrchestrator() {
        // java 8 not supported yet
        this.internalExecutor = CismetExecutors.newCachedLimitedThreadPool(
                5,
                new CismetConcurrency.CismetThreadFactory(
                    GEOCPM_THREADGROUP,
                    "geocpm-import-orchestrator", // NOI18N
                    new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread t, final Throwable e) {
                            log.error("uncaught exception in thread, operation result unknown [thread=" + t + "]", e); // NOI18N
                        }
                    }),
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
                        log.error(
                            "cannot execute internal task, too few resources? operation result unknown " // NOI18N
                                    + "[runnable="
                                    + r                                                                  // NOI18N
                                    + "|executor="
                                    + executor                                                           // NOI18N
                                    + "]");                                                              // NOI18N
                    }
                });

        this.defaultConfiguration = buildDefaultConfiguration();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GeoCPMImportOrchestrator newInstance() {
        return new GeoCPMImportOrchestrator();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Properties buildDefaultConfiguration() {
        final Properties config = new Properties();

        // NOTE: We load the first import transformer we find assuming there is only one. This operation is only
        // convenience for the simple case of single transformer.

        final GeoCPMImportTransformer importTransformer = Lookup.getDefault().lookup(GeoCPMImportTransformer.class);
        if (importTransformer == null) {
            config.put(
                GeoCPMConstants.CFG_IMPORTER_FQCN,
                "de.cismet.geocpm.api.transform.NoopGeoCPMImportTransformer"); // NOI18N
        } else {
            config.put(GeoCPMConstants.CFG_IMPORTER_FQCN, importTransformer.getClass().getCanonicalName());
        }

        config.put(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS, String.valueOf(1));

        // NOTE: We load the first project transformer we find assuming there is only one. We cannot lookup all
        // instances and put them one after another as we cannot rely on the order they will occur. For a proper
        // transformation pipeline order is crucial. So this operation is only convenience for the simple case of single
        // transformer.

        final Collection<? extends GeoCPMProjectTransformer> c = Lookup.getDefault()
                    .lookupAll(GeoCPMProjectTransformer.class);

        if (log.isWarnEnabled() && (c.size() > 1)) {
            log.warn("found multiple project transformers, transformation may yield unexpected results: " + c); // NOI18N
        }

        if (c.size() < 1) {
            config.put(
                GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX
                        + "1", // NOI18N
                "de.cismet.geocpm.api.transform.NoopGeoCPMProjectTransformer"); // NOI18N
        } else {
            config.put(
                GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX
                        + "1", // NOI18N
                c.iterator().next().getClass().getCanonicalName());
        }

        return config;
    }

    /**
     * Starts a new import process using the default configuration, no progress listener and the provided import object,
     * see {@link #doImport(java.util.Properties, java.lang.Object, de.cismet.commons.utils.ProgressListener)}.
     *
     * @param   importObj  the object to process
     *
     * @return  a future that provides the final state of this import
     */
    public Future<ProgressEvent.State> doImport(@NonNull final Object importObj) {
        return doImport(defaultConfiguration, importObj, null);
    }

    /**
     * Starts a new import process using the default configuration and the provided no progress listener and import
     * object, see {@link #doImport(java.util.Properties, java.lang.Object, de.cismet.commons.utils.ProgressListener)}.
     *
     * @param   importObj         the object to process
     * @param   progressListener  the progresslistener for this import
     *
     * @return  a future that provides the final state of this import
     */
    public Future<ProgressEvent.State> doImport(@NonNull final Object importObj,
            final ProgressListener progressListener) {
        return doImport(defaultConfiguration, importObj, progressListener);
    }

    /**
     * Starts a new import process using no progress listener and the provided configuration and import object, see
     * {@link #doImport(java.util.Properties, java.lang.Object, de.cismet.commons.utils.ProgressListener)}.
     *
     * @param   configuration  the configuration for the import
     * @param   importObj      the object to process
     *
     * @return  a future that provides the final state of this import
     */
    public Future<ProgressEvent.State> doImport(@NonNull final Properties configuration,
            @NonNull final Object importObj) {
        return doImport(configuration, importObj, null);
    }

    /**
     * Starts a new import process using the provided configuration, import object and progress listener. The
     * configuration and the import object must not be null while the progress listener is optional. The import process
     * will start right away if there are import threads available and will be queued otherwise. However, if there are
     * too many requests at once a {@link RejectedExecutionException} will be thrown. Please note, that, apart from the
     * <code>IllegalArgumentException</code> in case of <code>null</code> objects and the <code>
     * RejectedExecutionException</code> no other exception is thrown. Any exception related to the import process
     * itself must be obtained through the future that is returned by this operation. The import process may also be
     * canceled using the future's {@link Future#cancel(boolean)} operation which should be used with <code>true</code>
     * as an argument to ensure that the process will stop as soon as possible. Every transformer is requested to handle
     * interrupts correctly.
     *
     * @param   configuration     the configuration for the import
     * @param   importObj         the object to process
     * @param   progressListener  the progresslistener for this import
     *
     * @return  a future that provides the final state of this import
     *
     * @see     Future
     * @see     Transformer
     */
    public Future<ProgressEvent.State> doImport(@NonNull final Properties configuration,
            @NonNull final Object importObj,
            final ProgressListener progressListener) {
        return internalExecutor.submit(new ImportTask(configuration, importObj, progressListener));
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @author   martin.scholl@cismet.de
     * @version  1.0
     */
    @RequiredArgsConstructor
    private static final class ImportTask implements Callable<ProgressEvent.State> {

        //~ Instance fields ----------------------------------------------------

        // externally submitted resources
        private final Properties configuration;
        private final Object importObj;
        private final ProgressListener progressL;

        // interally used resources
        private ExecutorService importExecutor;
        private Future projectProgressWatch;
        private ProcessJoiner processJoiner;

        // initialised by config
        // -----
        private ExecutorService pipelineExecutor;
        private Collection<GeoCPMProject> geocpmProjects;
        private List<Future<GeoCPMProject>> runningProjects;

        private GeoCPMImportTransformer importTransformer;
        private List<GeoCPMProjectTransformer> projectTransformers;
        // -----

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   config  DOCUMENT ME!
         *
         * @throws  ConfigurationException  DOCUMENT ME!
         */
        private void setup(final Properties config) {
            if (log.isTraceEnabled()) {
                log.trace("begin setup [configuration=" + config + "]"); // NOI18N
            }

            importExecutor = Executors.newFixedThreadPool(
                    2,
                    new CismetConcurrency.CismetThreadFactory(
                        GEOCPM_THREADGROUP,
                        "geocpm-import-task", // NOI18N
                        new Thread.UncaughtExceptionHandler() {

                            @Override
                            public void uncaughtException(final Thread t, final Throwable e) {
                                log.error("uncaught exception in thread, task result unknown [thread=" + t + "]", e); // NOI18N
                            }
                        }));

            runningProjects = new ArrayList<>();

            // <editor-fold defaultstate="collapsed" desc="no of parallel executions">
            final String noOfParallelPipeLineThreads = config.getProperty(GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS);
            if ((noOfParallelPipeLineThreads == null) || noOfParallelPipeLineThreads.isEmpty()) {
                throw new ConfigurationException("# of parallel pipeline executions (" // NOI18N
                            + GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS + ") not configured", // NOI18N
                    null,
                    config);
            }

            try {
                final int parallelExecs = Integer.parseInt(noOfParallelPipeLineThreads, 10);

                if (parallelExecs < 1) {
                    throw new ConfigurationException("# of parallel pipeline executions (" // NOI18N
                                + GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS + ") contains improper value", // NOI18N
                        null,
                        config);
                }
                pipelineExecutor = CismetExecutors.newFixedThreadPool(
                        parallelExecs,
                        new CismetConcurrency.CismetThreadFactory(
                            GEOCPM_THREADGROUP,
                            "geocpm-import-pipeline",          // NOI18N
                            new Thread.UncaughtExceptionHandler() {

                                @Override
                                public void uncaughtException(final Thread t, final Throwable e) {
                                    log.error(
                                        "uncaught exception in thread, operation result unknown [thread="
                                                + t
                                                + "]",
                                        e); // NOI18N
                                }
                            }));
            } catch (final NumberFormatException nfe) {
                throw new ConfigurationException("# of parallel pipeline executions (" // NOI18N
                            + GeoCPMConstants.CFG_PIPELINE_PARALLEL_EXECS + ") contains improper value", // NOI18N
                    nfe,
                    config);
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="import object transformer">
            final String importTransformerFqcn = config.getProperty(GeoCPMConstants.CFG_IMPORTER_FQCN);
            if ((importTransformerFqcn == null) || importTransformerFqcn.isEmpty()) {
                throw new ConfigurationException("import transformer (" // NOI18N
                            + GeoCPMConstants.CFG_IMPORTER_FQCN + ") not configured", // NOI18N
                    null,
                    config);
            }

            try {
                final Class<?> cImportTransformer = Class.forName(importTransformerFqcn);
                if (!GeoCPMImportTransformer.class.isAssignableFrom(cImportTransformer)) {
                    throw new ConfigurationException("import transformer is not of type '" // NOI18N
                                + GeoCPMImportTransformer.class.getCanonicalName() + "' (" // NOI18N
                                + GeoCPMConstants.CFG_IMPORTER_FQCN + "): " + importTransformerFqcn, // NOI18N
                        null,
                        config);
                }
                importTransformer = (GeoCPMImportTransformer)cImportTransformer.newInstance();
            } catch (final ClassNotFoundException ex) {
                throw new ConfigurationException("import transformer not found (" // NOI18N
                            + GeoCPMConstants.CFG_IMPORTER_FQCN + "): " + importTransformerFqcn, // NOI18N
                    ex,
                    config);
            } catch (final InstantiationException | IllegalAccessException ex) {
                throw new ConfigurationException("import transformer cannot be instantiated (" // NOI18N
                            + GeoCPMConstants.CFG_IMPORTER_FQCN + "): " + importTransformerFqcn, // NOI18N
                    ex,
                    config);
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="project transformers">

            projectTransformers = new ArrayList<>();
            boolean continueSearch = true;
            int sequentialNumber = 1;
            while (continueSearch) {
                final String currentTransformerFqcnProp = GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX
                            + String.valueOf(sequentialNumber);
                final String projectTransformerFqcn = config.getProperty(currentTransformerFqcnProp);

                if (projectTransformerFqcn == null) {
                    continueSearch = false;
                } else {
                    if (projectTransformerFqcn.isEmpty()) {
                        throw new ConfigurationException("project transformer (" // NOI18N
                                    + currentTransformerFqcnProp + ") not configured", // NOI18N
                            null,
                            config);
                    }

                    try {
                        final Class<?> cProjectTransformer = Class.forName(projectTransformerFqcn);
                        if (!GeoCPMProjectTransformer.class.isAssignableFrom(cProjectTransformer)) {
                            throw new ConfigurationException("project transformer is not of type '" // NOI18N
                                        + GeoCPMProjectTransformer.class.getCanonicalName() + "' (" // NOI18N
                                        + currentTransformerFqcnProp + "): " + projectTransformerFqcn, // NOI18N
                                null,
                                config);
                        }
                        projectTransformers.add((GeoCPMProjectTransformer)cProjectTransformer.newInstance());
                    } catch (final ClassNotFoundException ex) {
                        throw new ConfigurationException("project transformer not found (" // NOI18N
                                    + currentTransformerFqcnProp + "): " + projectTransformerFqcn, // NOI18N
                            ex,
                            config);
                    } catch (final InstantiationException | IllegalAccessException ex) {
                        throw new ConfigurationException("project transformer cannot be instantiated (" // NOI18N
                                    + currentTransformerFqcnProp + "): " + projectTransformerFqcn, // NOI18N
                            ex,
                            config);
                    }

                    sequentialNumber++;
                }
            }

            if (projectTransformers.isEmpty()) {
                throw new ConfigurationException("project transformer (" // NOI18N
                            + GeoCPMConstants.CFG_PIPELINE_IMPORTER_FQCN_PREFIX + "1) not configured", // NOI18N
                    null,
                    config);
            }
            // </editor-fold>

            if (log.isTraceEnabled()) {
                log.trace("end setup [configuration=" + config + "]"); // NOI18N
            }
        }

        @Override
        public ProgressEvent.State call() throws Exception {
            if (log.isTraceEnabled()) {
                log.trace("begin import [configuration=" + configuration // NOI18N
                            + "|importObj=" + importObj.toString() // NOI18N
                            + "|progresslistener=" + progressL + "]"); // NOI18N
            }

            if (progressL != null) {
                progress(progressL, new ProgressEvent(this, ProgressEvent.State.STARTED, "Starting import")); // NOI18N
            }

            if (Thread.interrupted()) {
                return doCancel("import cancelled before configure", importObj, progressL); // NOI18N
            }

            setup(configuration);

            if (!importTransformer.accept(importObj)) {
                throw new ConfigurationException("import transformer does not accept import object: " + importObj, // NOI18N
                    null,
                    new Properties(configuration));
            }

            if (progressL != null) {
                progress(
                    progressL,
                    new ProgressEvent(this, ProgressEvent.State.PROGRESSING, "Configuration finished")); // NOI18N
            }

            if (Thread.interrupted()) {
                return doCancel("import cancelled before import transformation", importObj, progressL); // NOI18N
            }

            geocpmProjects = importTransformer.transform(importObj);

            if (Thread.interrupted()) {
                return doCancel("import cancelled before project import setup", importObj, progressL); // NOI18N
            }

            if (progressL != null) {
                progress(
                    progressL,
                    new ProgressEvent(this, ProgressEvent.State.PROGRESSING, "GeoCPM Projects created")); // NOI18N
            }

            final Collection<Callable<GeoCPMProject>> projectTasks = new ArrayList<>(geocpmProjects.size());
            for (final GeoCPMProject project : geocpmProjects) {
                projectTasks.add(new GeoCPMProjectPipeline(project, projectTransformers));
            }

            if (Thread.interrupted()) {
                return doCancel("import cancelled before project import", importObj, progressL); // NOI18N
            }

            for (final Callable<GeoCPMProject> task : projectTasks) {
                runningProjects.add(pipelineExecutor.submit(task));
            }

            if (progressL != null) {
                progress(
                    progressL,
                    new ProgressEvent(
                        this,
                        ProgressEvent.State.PROGRESSING,
                        0,
                        runningProjects.size(),
                        "GeoCPM Projects are being processed")); // NOI18N
            }

            pipelineExecutor.shutdown();

            if (progressL != null) {
                projectProgressWatch = importExecutor.submit(new ProjectProgressWatch(progressL));
            }

            processJoiner = new ProcessJoiner();

            final Future<ProgressEvent.State> taskFuture = importExecutor.submit(processJoiner);

            return taskFuture.get();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  progressListener  DOCUMENT ME!
         * @param  progressEvent     DOCUMENT ME!
         */
        private void progress(final ProgressListener progressListener, final ProgressEvent progressEvent) {
            if (log.isTraceEnabled()) {
                log.trace("progress: " + progressEvent); // NOI18N
            }

            if (EventQueue.isDispatchThread()) {
                progressListener.progress(progressEvent);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressListener.progress(progressEvent);
                        }
                    });
            }
        }

        /**
         * shall always be called from within cancelGuard.
         *
         * @param   message           DOCUMENT ME!
         * @param   importObj         DOCUMENT ME!
         * @param   progressListener  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private ProgressEvent.State doCancel(final String message,
                final Object importObj,
                final ProgressListener progressListener) {
            if (log.isInfoEnabled()) {
                log.info(message + " [importObj=" + importObj + "|progresslistener=" + progressListener + "]"); // NOI18N
            }

            // TODO: release all resources

            // running project might not be initialised if the task has been canceled right at the beginning
            if (runningProjects != null) {
                for (final Future<GeoCPMProject> f : runningProjects) {
                    f.cancel(true);
                }
            }
            runningProjects = null;

            if (progressListener != null) {
                progress(progressListener, new ProgressEvent(ImportTask.this, ProgressEvent.State.CANCELED));
            }

            return ProgressEvent.State.CANCELED;
        }

        //~ Inner Classes ------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @author   martin.scholl@cismet.de
         * @version  1.0
         */
        private final class ProcessJoiner implements Callable<ProgressEvent.State> {

            //~ Methods --------------------------------------------------------

            @Override
            public ProgressEvent.State call() throws Exception {
                while (!runningProjects.isEmpty()) {
                    if (Thread.currentThread().isInterrupted()) {
                        doCancel("import task interupted: " + ImportTask.this, importObj, progressL); // NOI18N

                        return ProgressEvent.State.CANCELED;
                    }

                    for (int i = runningProjects.size() - 1; i >= 0; --i) {
                        final Future f = runningProjects.get(i);
                        if (f.isDone()) {
                            try {
                                f.get(100, TimeUnit.MILLISECONDS);
                                runningProjects.remove(i);
                            } catch (final InterruptedException | TimeoutException ex) {
                                if (log.isErrorEnabled()) {
                                    log.error("pipeline should have been completed", ex); // NOI18N
                                }

                                doCancel("internal error: illegal pipeline state", null, null); // NOI18N

                                return ProgressEvent.State.BROKEN;
                            } catch (final CancellationException ex) {
                                if (log.isErrorEnabled()) {
                                    log.error("outside access to running pipelines", ex); // NOI18N
                                }

                                doCancel("internal error: illegal access to pipelines", null, null); // NOI18N

                                return ProgressEvent.State.BROKEN;
                            } catch (final ExecutionException ex) {
                                if (log.isErrorEnabled()) {
                                    log.error("error during pipeline processing", ex); // NOI18N
                                }

                                doCancel("error during pipeline processing", null, null); // NOI18N

                                return ProgressEvent.State.BROKEN;
                            }
                        }
                    }
                }

                // maybe implement shutdown operation or similar
                // ensure the progress watch stopped so that the last event is indeed finish
                if (projectProgressWatch != null) {
                    projectProgressWatch.get();
                }

                if (progressL != null) {
                    progress(
                        progressL,
                        new ProgressEvent(this, ProgressEvent.State.FINISHED, "GeoCPM Import Finished")); // NOI18N
                }

                return ProgressEvent.State.FINISHED;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @author   martin.scholl@cismet.de
         * @version  1.0
         */
        private final class ProjectProgressWatch implements Runnable {

            //~ Instance fields ------------------------------------------------

            private final ProgressListener progressListener;
            private ProgressEvent lastEvent;

            //~ Constructors ---------------------------------------------------

            /**
             * Creates a new ProjectProgressWatch object.
             *
             * @param  progressListener  DOCUMENT ME!
             */
            ProjectProgressWatch(final ProgressListener progressListener) {
                this.progressListener = progressListener;
            }

            //~ Methods --------------------------------------------------------

            @Override
            public void run() {
                while (!pipelineExecutor.isTerminated()) {
                    if (Thread.currentThread().isInterrupted()) {
                        if (log.isDebugEnabled()) {
                            log.debug("progress watch is interrupted during watch, event propagation stopped"); // NOI18N
                        }

                        return;
                    }

                    int doneCount = 0;
                    for (final Future<GeoCPMProject> f : runningProjects) {
                        if (f.isDone() && !f.isCancelled()) {
                            doneCount++;
                        }
                    }

                    if ((lastEvent != null) && (lastEvent.getStep() < doneCount)) {
                        lastEvent = new ProgressEvent(
                                ImportTask.this,
                                ProgressEvent.State.PROGRESSING,
                                doneCount,
                                runningProjects.size());
                        progress(progressListener, lastEvent);
                    }

                    try {
                        Thread.sleep(1500);
                    } catch (final InterruptedException ex) {
                        if (log.isDebugEnabled()) {
                            log.debug("progress watch is interrupted during sleep, event propagation stopped", ex); // NOI18N
                        }

                        return;
                    }
                }
            }
        }
    }
}
