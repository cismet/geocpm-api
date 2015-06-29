package de.cismet.geocpm.api.transform;

import de.cismet.geocpm.api.transform.GeoCPMEinPointToMemoryTransformer;
import de.cismet.geocpm.api.GeoCPMProject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
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
public class GeoCPMEinPointToMemoryTransformerNGTest {

    public GeoCPMEinPointToMemoryTransformerNGTest() {
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
     * Test of accept method, of class GeoCPMEinPointToMemoryTransformer.
     */
    @Test
    public void testAccept() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinPointToMemoryTransformer t = new GeoCPMEinPointToMemoryTransformer();
        
        assertFalse(t.accept(null));
        
        GeoCPMProject p = new GeoCPMProject();
        assertFalse(t.accept(p));
        
        final File f = File.createTempFile("test", "geocpmtests");
        f.deleteOnExit();
        
        p.setGeocpmEin(f);
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class GeoCPMEinPointToMemoryTransformer.
     */
    @Test
    public void testTransform() throws Exception {
        printCurrentTestName();
        
        final GeoCPMEinPointToMemoryTransformer t = new GeoCPMEinPointToMemoryTransformer();
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
        
        p = t.transform(p);
        
        assertNotNull(p.getPoints());
        assertEquals(p.getPoints().size(), 44);
    }
}