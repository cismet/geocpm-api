package de.cismet.geocpm.api.transform;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class GeoCPMResultElementsToMemoryTransformerNGTest {

    public GeoCPMResultElementsToMemoryTransformerNGTest() {
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
     * Test of accept method, of class GeoCPMResultElementsToMemoryTransformer.
     */
    @Test
    public void testAccept() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        
        assertFalse(t.accept(null));
        
        GeoCPMProject p = new GeoCPMProject();
        assertFalse(t.accept(p));
        
        p.setTriangles(new ArrayList<Triangle>(0));
        assertFalse(t.accept(p));
        
        p.setTriangles(Arrays.asList(new Triangle[] {new Triangle(0, null, null, null)}));
        assertFalse(t.accept(p));
        
        p.setResults(new ArrayList<GeoCPMResult>());
        assertTrue(t.accept(p));
        
        p.getResults().add(new GeoCPMResult(0));
        assertFalse(t.accept(p));
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        p.getResults().iterator().next().setGeocpmResultElements(f);
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class GeoCPMResultElementsToMemoryTransformer.
     */
    @Test
    public void testTransform_single() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMResultElementsToMemoryTransformer_SimpleGeoCPMResultsElements.aus"));
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        GeoCPMResult gr = new GeoCPMResult(1);
        gr.setGeocpmResultElements(f);
        p.setResults(Arrays.asList(gr));
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 66; ++i) {
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
        
        Collection<Result> results = p.getResults().iterator().next().getResults();
        
        assertNotNull(results);
        assertTrue(results instanceof List);
        
        for(Result result : results) {
            assertNotNull(result.getWaterlevels());
        }
        
        assertEquals(((List<Result>)results).get(1).getWaterlevels().size(), 3254);
        assertEquals(((List<Result>)results).get(34).getWaterlevels().size(), 3104);
        assertEquals(((List<Result>)results).get(62).getWaterlevels().size(), 106);
    }
    
    @Test
    public void testTransform_multiple() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMResultElementsToMemoryTransformer_SimpleGeoCPMResultsElements.aus"));
        final File f1 = File.createTempFile("test", "geocpmtests");
        f1.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f1));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMResultElementsToMemoryTransformer_SimpleGeoCPMResultsElements.aus"));
        final File f2 = File.createTempFile("test", "geocpmtests");
        f2.deleteOnExit();
        
        o = new BufferedOutputStream(new FileOutputStream(f2));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMResultElementsToMemoryTransformer_SimpleGeoCPMResultsElements.aus"));
        final File f3 = File.createTempFile("test", "geocpmtests");
        f3.deleteOnExit();
        
        o = new BufferedOutputStream(new FileOutputStream(f3));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        GeoCPMResult gr1 = new GeoCPMResult(1);
        GeoCPMResult gr2 = new GeoCPMResult(2);
        GeoCPMResult gr3 = new GeoCPMResult(3);
        gr1.setGeocpmResultElements(f1);
        gr2.setGeocpmResultElements(f2);
        gr3.setGeocpmResultElements(f3);
        p.setResults(Arrays.asList(gr1, gr2, gr3));
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 66; ++i) {
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
        
        for(GeoCPMResult gr : p.getResults()) {
            Collection<Result> results = gr.getResults();

            assertNotNull(results);
            assertTrue(results instanceof List);

            for(Result result : results) {
                assertNotNull(result.getWaterlevels());
            }

            assertEquals(((List<Result>)results).get(1).getWaterlevels().size(), 3254);
            assertEquals(((List<Result>)results).get(34).getWaterlevels().size(), 3104);
            assertEquals(((List<Result>)results).get(62).getWaterlevels().size(), 106);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransform_missingTriangle() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        GeoCPMResult gr = new GeoCPMResult(1);
        gr.setGeocpmResultElements(f);
        p.setResults(Arrays.asList(gr));
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 65; ++i) {
            if(i == 43) {
                i++;
            }
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransform_wrongTriangleRef() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMResultElementsToMemoryTransformer_SimpleGeoCPMResultsElements.aus"));
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        GeoCPMResult gr = new GeoCPMResult(1);
        gr.setGeocpmResultElements(f);
        p.setResults(Arrays.asList(gr));
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 34; ++i) {
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
    }
}