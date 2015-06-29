package de.cismet.cids.custom.wupp.geocpm.api.transform;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Point;
import de.cismet.cids.custom.wupp.geocpm.api.entity.Triangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
public class GeoCPMEinTriangleToMemoryTransformerNGTest {

    public GeoCPMEinTriangleToMemoryTransformerNGTest() {
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
     * Test of accept method, of class GeoCPMEinTriangleToMemoryTransformer.
     */
    @Test
    public void testAccept() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinTriangleToMemoryTransformer t = new GeoCPMEinTriangleToMemoryTransformer();
        
        assertFalse(t.accept(null));
        
        GeoCPMProject p = new GeoCPMProject();
        assertFalse(t.accept(p));
        
        p.setPoints(new ArrayList<Point>(0));
        assertFalse(t.accept(p));
        
        p.setPoints(Arrays.asList(new Point[] {new Point(0, 0, 0, 0)}));
        assertFalse(t.accept(p));
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        p.setGeocpmEin(f);
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class GeoCPMEinTriangleToMemoryTransformer.
     */
    @Test
    public void testTransform() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinTriangleToMemoryTransformer t = new GeoCPMEinTriangleToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMEinPointToMemoryTransformer_SimpleGeoCPM.ein"));
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        p.setGeocpmEin(f);
        
        // points matching simpleGeoCPM.ein
        p.setPoints(Arrays.asList(new Point[] {
            new Point(0, 0.000, 0.000, 1.000),
            new Point(1, 0.000, 50.000, 3.000),
            new Point(2, 0.000, 25.000, 2.000),
            new Point(3, 0.000, 6.250, 1.250),
            new Point(4, 0.000, 18.750, 1.750),
            new Point(5, 0.000, 31.250, 2.250),
            new Point(6, 0.000, 43.750, 2.750),
            new Point(7, 2.500, 46.875, 2.875),
            new Point(8, 2.500, 40.625, 2.625),
            new Point(9, 2.500, 34.375, 2.375),
            new Point(10, 2.500, 28.125, 2.125),
            new Point(11, 2.500, 21.875, 1.875),
            new Point(12, 2.500, 15.625, 1.625),
            new Point(13, 2.500, 9.375, 1.375),
            new Point(14, 2.500, 3.125, 1.125),
            new Point(15, 5.000, 0.000, 1.000),
            new Point(16, 5.000, 100.000, 5.000),
            new Point(17, 5.000, -50.000, -1.000),
            new Point(18, 5.000, 25.000, 2.000),
            new Point(19, 5.000, 37.500, 2.500),
            new Point(20, 5.000, 12.500, 1.500),
            new Point(21, 5.000, 6.250, 1.250),
            new Point(22, 5.000, 18.750, 1.750),
            new Point(23, 5.000, 31.250, 2.250),
            new Point(24, 5.000, 43.750, 2.750),
            new Point(25, 7.500, 46.875, 2.875),
            new Point(26, 7.500, 40.625, 2.625),
            new Point(27, 7.500, 34.375, 2.375),
            new Point(28, 7.500, 28.125, 2.125),
            new Point(29, 7.500, 21.875, 1.875),
            new Point(30, 7.500, 15.625, 1.625),
            new Point(31, 7.500, 9.375, 1.375),
            new Point(32, 7.500, 3.125, 1.125),
            new Point(33, 10.000, 0.000, 1.000),
            new Point(34, 10.000, 50.000, 3.000),
            new Point(35, 10.000, 25.000, 2.000),
            new Point(36, 10.000, 6.250, 1.250),
            new Point(37, 10.000, 18.750, 1.750),
            new Point(38, 10.000, 31.250, 2.250),
            new Point(39, 10.000, 43.750, 2.750),
            new Point(40, 10.000, 12.500, 1.500),
            new Point(41, 0.000, 12.500, 1.500),
            new Point(42, 10.000, 37.500, 2.500),
            new Point(43, 0.000, 37.500, 2.500)
        }));
        
        p = t.transform(p);
        
        assertTrue(p.getTriangles() != null);
        assertEquals(p.getTriangles().size(), 66);
        
        int beCount = 0;
        for(Triangle tr : p.getTriangles()) {
            if(tr.hasBreakingEdge()) {
                beCount++;
            }
        }
        
        assertEquals(beCount, 10);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransform_missingPoint() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinTriangleToMemoryTransformer t = new GeoCPMEinTriangleToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMEinPointToMemoryTransformer_SimpleGeoCPM.ein"));
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        p.setGeocpmEin(f);
        // point with id 5 is missing
        p.setPoints(Arrays.asList(new Point[] {
            new Point(0, 0.000, 0.000, 1.000),
            new Point(1, 0.000, 50.000, 3.000),
            new Point(2, 0.000, 25.000, 2.000),
            new Point(3, 0.000, 6.250, 1.250),
            new Point(4, 0.000, 18.750, 1.750),
            new Point(6, 0.000, 43.750, 2.750),
            new Point(7, 2.500, 46.875, 2.875)
        }));
        
        t.transform(p);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransform_wrongPointRef() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinTriangleToMemoryTransformer t = new GeoCPMEinTriangleToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMEinPointToMemoryTransformer_SimpleGeoCPM.ein"));
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        p.setGeocpmEin(f);
        p.setPoints(Arrays.asList(new Point[] {
            new Point(0, 0.000, 0.000, 1.000)
        }));
        
        t.transform(p);
    }
}