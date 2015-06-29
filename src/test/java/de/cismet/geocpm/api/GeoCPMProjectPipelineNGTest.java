package de.cismet.geocpm.api;

import de.cismet.geocpm.api.GeoCPMProjectPipeline;
import de.cismet.geocpm.api.ConfigurationException;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.impl.Sleep500GeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.impl.CountingLoopGeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.impl.CountingSleepGeoCPMProjectTransformer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class GeoCPMProjectPipelineNGTest {
    
    public GeoCPMProjectPipelineNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    public void printCurrentTestName() {
        System.out.println("TEST " + new Throwable().getStackTrace()[1].getMethodName());
    }

    /**
     * Test of call method, of class GeoCPMProjectPipeline.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCall_nullGeoCPMProject() throws Exception {
        printCurrentTestName();
        
        new GeoCPMProjectPipeline(null, null);
    }
    

    /**
     * Test of call method, of class GeoCPMProjectPipeline.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCall_nullTransformerList() throws Exception {
        printCurrentTestName();
        
        new GeoCPMProjectPipeline(new GeoCPMProject(), null);
    }
    

    /**
     * Test of call method, of class GeoCPMProjectPipeline.
     */
    @Test
    public void testCall_singleSleepTransformer() throws Exception {
        printCurrentTestName();
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(
                new GeoCPMProject(), 
                Arrays.asList((GeoCPMProjectTransformer)new Sleep500GeoCPMProjectTransformer()));
        
        final GeoCPMProject proj = pl.call();
        
        assertEquals(proj.getName(), Sleep500GeoCPMProjectTransformer.TRANSFORMED_NAME);
    }
     
    @Test
    public void testCall_multipleSleepTransformer_ensureCorrectOrder1() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(100, 0),
                new CountingSleepGeoCPMProjectTransformer(100, 1),
                new CountingSleepGeoCPMProjectTransformer(100, 2),
                new CountingSleepGeoCPMProjectTransformer(100, 3),
                new CountingSleepGeoCPMProjectTransformer(100, 4)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        proj = pl.call();
        
        assertEquals(proj.getName(), "0 1 2 3 4");
    }
     
    @Test(expectedExceptions = ConfigurationException.class)
    public void testCall_multipleSleepTransformer_ensureCorrectOrder2() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(100, 0),
                new CountingSleepGeoCPMProjectTransformer(100, 4),
                new CountingSleepGeoCPMProjectTransformer(100, 1),
                new CountingSleepGeoCPMProjectTransformer(100, 2),
                new CountingSleepGeoCPMProjectTransformer(100, 3)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        pl.call();
    }
     
    @Test(expectedExceptions = ConfigurationException.class)
    public void testCall_multipleSleepTransformer_noAccept1() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(100, 4),
                new CountingSleepGeoCPMProjectTransformer(100, 4),
                new CountingSleepGeoCPMProjectTransformer(100, 1),
                new CountingSleepGeoCPMProjectTransformer(100, 2),
                new CountingSleepGeoCPMProjectTransformer(100, 3)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        pl.call();
    }
     
    @Test(expectedExceptions = ConfigurationException.class)
    public void testCall_multipleSleepTransformer_noAccept2() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(100, 0),
                new CountingSleepGeoCPMProjectTransformer(100, 1),
                new CountingSleepGeoCPMProjectTransformer(100, 2),
                new CountingSleepGeoCPMProjectTransformer(100, 4),
                new CountingSleepGeoCPMProjectTransformer(100, 3)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        pl.call();
    }
     
    @Test()
    public void testCall_properCancel1() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(10000, 0)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        ExecutorService exec = Executors.newSingleThreadExecutor();
        
        final Future<GeoCPMProject> f = exec.submit(pl);
        
        Thread.sleep(100);
        f.cancel(true);
        
        assertEquals(proj.getAnnuality(), 0);
    }    
    
    @Test
    public void testCall_properCancel2() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingLoopGeoCPMProjectTransformer(100000000000000l, 0)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        ExecutorService exec = Executors.newSingleThreadExecutor();
        
        final Future<GeoCPMProject> f = exec.submit(pl);
        
        Thread.sleep(100);
        f.cancel(true);
        
        assertEquals(proj.getAnnuality(), 0);
    }
     
    @Test()
    public void testCall_properCancel3() throws Exception {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        proj.setAnnuality(0);
        
        final List<GeoCPMProjectTransformer> trans = Arrays.asList((GeoCPMProjectTransformer)
                new CountingSleepGeoCPMProjectTransformer(100, 0),
                new CountingSleepGeoCPMProjectTransformer(100, 1),
                new CountingSleepGeoCPMProjectTransformer(10000, 2)
        );
        
        final GeoCPMProjectPipeline pl = new GeoCPMProjectPipeline(proj, trans);
        
        ExecutorService exec = Executors.newSingleThreadExecutor();
        
        final Future<GeoCPMProject> f = exec.submit(pl);
        
        Thread.sleep(1000);
        f.cancel(true);
        
        try {
            f.get();
            fail();
        } catch (CancellationException ex) {
            assertEquals(proj.getAnnuality(), 2);
        }
    }  
}
