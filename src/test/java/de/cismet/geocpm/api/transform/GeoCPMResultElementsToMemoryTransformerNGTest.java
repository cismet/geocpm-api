package de.cismet.geocpm.api.transform;

import de.cismet.geocpm.api.transform.GeoCPMResultElementsToMemoryTransformer;
import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.entity.Triangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        p.setGeocpmResultElements(f);
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class GeoCPMResultElementsToMemoryTransformer.
     */
    @Test
    public void testTransform() throws Exception {
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
        
        p.setGeocpmResultElements(f);
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 66; ++i) {
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
        
        for(Triangle tr : triangles) {
            assertNotNull(tr.getWaterlevels());
        }
        
        assertEquals(triangles.get(1).getWaterlevels().size(), 3254);
        assertEquals(triangles.get(34).getWaterlevels().size(), 3104);
        assertEquals(triangles.get(62).getWaterlevels().size(), 106);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransform_missingTriangle() throws Exception {
        printCurrentTestName();
        
        final GeoCPMResultElementsToMemoryTransformer t = new GeoCPMResultElementsToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        p.setGeocpmResultElements(f);
        
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
        
        p.setGeocpmResultElements(f);
        
        final List<Triangle> triangles = new ArrayList<>(66);
        for(int i = 0; i < 34; ++i) {
            triangles.add(new Triangle(i, null, null, null));
        }
        p.setTriangles(triangles);
        
        t.transform(p);
    }
}