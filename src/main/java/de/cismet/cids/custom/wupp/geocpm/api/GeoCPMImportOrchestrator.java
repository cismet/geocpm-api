/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMImportTransformer;
import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMProjectTransformer;

import de.cismet.commons.concurrency.CismetConcurrency;
import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.commons.utils.ProgressEvent;
import de.cismet.commons.utils.ProgressListener;

// TODO: javadoc
// TODO; test
// NOTE: could be transformed to be parallelized
/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class GeoCPMImportOrchestrator implements Cancellable {

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private Properties configuration;

    private boolean cancel;
    private boolean inProgress;
    private final Object cancelGuard;
    private final ExecutorService internalExecutor;
    private ProjectProgressWatch projectProgressWatch;
    private PipelineJoiner pipelineJoiner;

    // initialised by config
    // -----
    private ExecutorService importExecutor;
    private Collection<GeoCPMProject> geocpmProjects;
    private List<Future<GeoCPMProject>> runningProjects;

    private GeoCPMImportTransformer importTransformer;
    private List<GeoCPMProjectTransformer> projectTransformers;
    // -----

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMImportOrchestrator object.
     */
    public GeoCPMImportOrchestrator() {
        this.cancelGuard = new Object();

        //J-
        // jalopy only supports java 1.6
        this.internalExecutor = CismetExecutors.newCachedLimitedThreadPool(5,
                new CismetConcurrency.CismetThreadFactory(
                        new ThreadGroup(Thread.currentThread().getThreadGroup(), "geocpm-import-group"),  // NOI18N
                        "geocpm-import-orchestrator",                                                     // NOI18N
                        (Thread t, Throwable tw) -> {
                            log.error("uncaught exception in thread, exiting... [thread=" + t + "]", tw); // NOI18N
                            synchronized(cancelGuard) {
                                doCancel("cancelled due to internal error", null, null);                  // NOI18N
                            }
                        }
                ),
                (Runnable r, ThreadPoolExecutor executor) -> {
                    log.error("cannot execute internal task, too few resources? exiting... " // NOI18N
                            + "[runnable=" + r                                               // NOI18N
                            + "|executor=" + executor                                        // NOI18N
                            + "]");                                                          // NOI18N
                    synchronized(cancelGuard) {
                        doCancel("cancelled due to internal error", null, null);             // NOI18N
                    }
        });
        //J+
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void configure() {
        if (log.isTraceEnabled()) {
            log.trace("begin configure [configuration=" + configuration + "]"); // NOI18N
        }

        // TODO: implement

        if (log.isTraceEnabled()) {
            log.trace("end configure [configuration=" + configuration + "]"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   importObj         DOCUMENT ME!
     * @param   progressListener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException   DOCUMENT ME!
     * @throws  ConfigurationException  DOCUMENT ME!
     */
    public Future<ProgressEvent.State> doImport(@NonNull final Object importObj,
            final ProgressListener progressListener) {
        if (log.isTraceEnabled()) {
            log.trace("begin import [" + importObj.toString() + "|progresslistener=" + progressListener + "]"); // NOI18N
        }

        if (progressListener != null) {
            progress(progressListener, new ProgressEvent(this, ProgressEvent.State.STARTED, "Starting import")); // NOI18N
        }

        // using the cancelguard to ensure that the operation is not called if an import is running
        synchronized (cancelGuard) {
            if (inProgress) {
                throw new IllegalStateException("import is running");                       // NOI18N
            }
            inProgress = true;
            if (cancel) {
                doCancel("import cancelled before configure", importObj, progressListener); // NOI18N

                //J-
                // jalopy only supports java 1.6
                return new FutureTask(() -> {}, ProgressEvent.State.CANCELED);
                //J+
            }
        }

        configure();

        if (!importTransformer.accept(importObj)) {
            throw new ConfigurationException("import transformer does not accept import object: " + importObj, // NOI18N
                null,
                new Properties(configuration));
        }

        if (progressListener != null) {
            progress(
                progressListener,
                new ProgressEvent(this, ProgressEvent.State.PROGRESSING, "Configuration finished")); // NOI18N
        }

        synchronized (cancelGuard) {
            if (cancel) {
                doCancel("import cancelled before import transformation", importObj, progressListener); // NOI18N

                //J-
                // jalopy only supports java 1.6
                return new FutureTask(() -> {}, ProgressEvent.State.CANCELED);
                //J+
            }
        }

        geocpmProjects = importTransformer.transform(importObj);

        if (progressListener != null) {
            progress(
                progressListener,
                new ProgressEvent(this, ProgressEvent.State.PROGRESSING, "GeoCPM Projects created")); // NOI18N
        }

        synchronized (cancelGuard) {
            if (cancel) {
                doCancel("import cancelled before project import setup", importObj, progressListener); // NOI18N

                //J-
                // jalopy only supports java 1.6
                return new FutureTask(() -> {}, ProgressEvent.State.CANCELED);
                //J+
            }
        }

        //J-
        // jalopy only supports java 1.6
        final Collection<Callable<GeoCPMProject>> projectTasks = new ArrayList<>(geocpmProjects.size());
        geocpmProjects.stream().parallel().forEach(
                project -> projectTasks.add(new GeoCPMProjectPipeline(project, projectTransformers)));
        //J+

        synchronized (cancelGuard) {
            if (cancel) {
                doCancel("import cancelled before project import", importObj, progressListener); // NOI18N

                //J-
                // jalopy only supports java 1.6
                return new FutureTask(() -> {}, ProgressEvent.State.CANCELED);
                //J+
            }
        }

        //J-
        // jalopy only supports java 1.6
        projectTasks.stream().parallel().forEach(task -> runningProjects.add(importExecutor.submit(task)));
        //J+

        if (progressListener != null) {
            progress(
                progressListener,
                new ProgressEvent(
                    this,
                    ProgressEvent.State.PROGRESSING,
                    0,
                    runningProjects.size(),
                    "GeoCPM Projects are being processed")); // NOI18N
        }

        importExecutor.shutdown();

        if (progressListener != null) {
            projectProgressWatch = new ProjectProgressWatch(progressListener);
            internalExecutor.execute(projectProgressWatch);
        }

        pipelineJoiner = new PipelineJoiner();

        return internalExecutor.submit(pipelineJoiner);
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
            //J-
            // jalopy only supports java 1.6
            EventQueue.invokeLater(() -> progressListener.progress(progressEvent));
            //J+
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isRunning() {
        synchronized (cancelGuard) {
            return inProgress;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCancelled() {
        synchronized (cancelGuard) {
            return cancel;
        }
    }

    @Override
    public void cancel() {
        synchronized (cancelGuard) {
            if (log.isTraceEnabled()) {
                log.trace("import cancel request"); // NOI18N
            }

            cancel = true;
        }
    }

    /**
     * shall always be called from within cancelGuard.
     *
     * @param  message           DOCUMENT ME!
     * @param  importObj         DOCUMENT ME!
     * @param  progressListener  DOCUMENT ME!
     */
    private void doCancel(final String message, final Object importObj, final ProgressListener progressListener) {
        if (log.isInfoEnabled()) {
            log.info(message + " [" + importObj.toString() + "|progresslistener=" + progressListener + "]"); // NOI18N
        }

        // TODO: release all resources
        projectProgressWatch.stop();
        projectProgressWatch = null;

        //J-
        // jalopy only supports java 1.6
        runningProjects.stream().forEach(f -> f.cancel(true));
        //J+

        inProgress = false;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PipelineJoiner implements Callable<ProgressEvent.State> {

        //~ Methods ------------------------------------------------------------

        @Override
        public ProgressEvent.State call() throws Exception {
            while (!runningProjects.isEmpty()) {
                synchronized (cancelGuard) {
                    if (cancel) {
                        return ProgressEvent.State.CANCELED;
                    }
                }

                for (int i = runningProjects.size() - 1; i >= 0; --i) {
                    final Future f = runningProjects.get(i);
                    if (f.isDone()) {
                        //J-
                        // jalopy only supports java 1.6
                        try {
                            f.get(100, TimeUnit.MILLISECONDS);
                            runningProjects.remove(i);
                        } catch (final InterruptedException | TimeoutException ex) {
                            if(log.isErrorEnabled()) {
                                log.error("pipeline should have been completed", ex); // NOI18N
                            }

                            synchronized(cancelGuard) {
                                doCancel("internal error: illegal pipeline state", null, null); // NOI18N
                            }

                            return ProgressEvent.State.BROKEN;
                        } catch (final CancellationException ex) {
                            if(log.isErrorEnabled()) {
                                log.error("outside access to running pipelines", ex);
                            }

                            synchronized(cancelGuard) {
                                doCancel("internal error: illegal access to pipelines", null, null); // NOI18N
                            }
                        } catch (final ExecutionException ex) {
                            if(log.isErrorEnabled()) {
                                log.error("error during pipeline processing", ex);
                            }

                            synchronized(cancelGuard) {
                                doCancel("error during pipeline processing", null, null); // NOI18N
                            }
                        }
                        //J+
                    }
                }
            }

            return ProgressEvent.State.FINISHED;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ProjectProgressWatch implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private boolean stop;
        private final ProgressListener progressListener;
        private ProgressEvent lastEvent;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ProjectProgressWatch object.
         *
         * @param  progressListener  DOCUMENT ME!
         */
        ProjectProgressWatch(final ProgressListener progressListener) {
            this.stop = false;
            this.progressListener = progressListener;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        void stop() {
            // no guard required, sooner or later the change will be known
            stop = true;
        }

        @Override
        public void run() {
            while (!importExecutor.isTerminated() && !stop) {
                //J-
                // jalopy only supports java 1.6
                final int doneCount = Long.valueOf(
                            runningProjects.stream().filter(f -> f.isDone()).count()
                        ).intValue();
                //J+

                if ((lastEvent != null) && (lastEvent.getStep() < doneCount)) {
                    lastEvent = new ProgressEvent(
                            GeoCPMImportOrchestrator.this,
                            ProgressEvent.State.PROGRESSING,
                            doneCount,
                            runningProjects.size());
                    progress(progressListener, lastEvent);
                }

                try {
                    Thread.sleep(1500);
                } catch (final InterruptedException ex) {
                    // ignore
                }
            }

            synchronized (cancelGuard) {
                if (cancel) {
                    progress(
                        progressListener,
                        new ProgressEvent(GeoCPMImportOrchestrator.this, ProgressEvent.State.CANCELED));
                }
            }
        }
    }
}
