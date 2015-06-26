package de.cismet.cids.custom.wupp.geocpm.api.transform;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
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
    public void testAccept() {
        printCurrentTestName();
        
        final GeoCPMEinPointToMemoryTransformer t = new GeoCPMEinPointToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        assertFalse(t.accept(p));
        
        p.setGeocpmEinReader(new StringReader("test"));
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class GeoCPMEinPointToMemoryTransformer.
     */
    @Test
    public void testTransform() {
        printCurrentTestName();
        
        final GeoCPMEinPointToMemoryTransformer t = new GeoCPMEinPointToMemoryTransformer();
        GeoCPMProject p = new GeoCPMProject();
        final InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("GeoCPMEinPointToMemoryTransformer_SimpleGeoCPM.ein"));
        p.setGeocpmEinReader(r);
        
        p = t.transform(p);
        
        assertNotNull(p.getPoints());
        assertEquals(p.getPoints().size(), 44);
    }
}