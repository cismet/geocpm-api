package de.cismet.geocpm.api;

import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
public class GeoCPMUtilitiesNGTest {

    public GeoCPMUtilitiesNGTest() {
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
     * Test of sortTriangles method, of class GeoCPMUtilities.
     */
    @Test
    public void testSortTriangles() {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        Collection<Triangle> t = new ArrayList<>();
        
        t.add(new Triangle(1, null, null, null));
        t.add(new Triangle(0, null, null, null));
        t.add(new Triangle(3, null, null, null));
        t.add(new Triangle(2, null, null, null));
        t.add(new Triangle(4, null, null, null));
        
        proj.setTriangles(t);
        
        GeoCPMUtilities.sortTriangles(proj);
        
        t = proj.getTriangles();
        assertTrue(t instanceof List);
        for(int i = 0; i < t.size(); ++i) {
            assertEquals(((List<Triangle>)t).get(i).getId(), i);
        }
        
        t = new HashSet<>();
        
        t.add(new Triangle(1, null, null, null));
        t.add(new Triangle(0, null, null, null));
        t.add(new Triangle(3, null, null, null));
        t.add(new Triangle(2, null, null, null));
        t.add(new Triangle(4, null, null, null));
        
        proj.setTriangles(t);
        
        GeoCPMUtilities.sortTriangles(proj);
        
        t = proj.getTriangles();
        assertTrue(t instanceof List);
        for(int i = 0; i < t.size(); ++i) {
            assertEquals(((List<Triangle>)t).get(i).getId(), i);
        }
    }

    /**
     * Test of sortResults method, of class GeoCPMUtilities.
     */
    @Test
    public void testSortResults() {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        Collection<GeoCPMResult> gResults = new ArrayList<>();
        
        GeoCPMResult gr = new GeoCPMResult(1);
        Collection<Result> results = new ArrayList<>();
        results.add(new Result(4));
        results.add(new Result(3));
        results.add(new Result(1));
        results.add(new Result(2));
        results.add(new Result(0));
        gr.setResults(results);
        gResults.add(gr);
        
        gr = new GeoCPMResult(2);
        results = new HashSet<>();
        results.add(new Result(0));
        results.add(new Result(1));
        results.add(new Result(2));
        results.add(new Result(4));
        results.add(new Result(3));
        gr.setResults(results);
        gResults.add(gr);
        
        proj.setResults(gResults);
        
        GeoCPMUtilities.sortResults(proj);
        
        for(GeoCPMResult gRes : proj.getResults()) {
            
            assertTrue(gRes.getResults() instanceof List);
            final List<Result> r = (List<Result>)gRes.getResults();
            
            for(int i = 0; i < r.size(); ++i) {
                assertEquals(r.get(i).getId(), i);
            }
        }
    }

    /**
     * Test of checkMissingTriangles method, of class GeoCPMUtilities.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testCheckMissingTriangles_hole() {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        Collection<Triangle> t = new ArrayList<>();
        
        t.add(new Triangle(1, null, null, null));
        t.add(new Triangle(0, null, null, null));
        t.add(new Triangle(3, null, null, null));
        t.add(new Triangle(2, null, null, null));
        t.add(new Triangle(5, null, null, null));
        
        proj.setTriangles(t);
        
        GeoCPMUtilities.checkMissingTriangles(proj);
    }

    /**
     * Test of checkMissingTriangles method, of class GeoCPMUtilities.
     */
    @Test
    public void testCheckMissingTriangles() {
        printCurrentTestName();
        
        GeoCPMProject proj = new GeoCPMProject();
        Collection<Triangle> t = new ArrayList<>();
        
        t.add(new Triangle(1, null, null, null));
        t.add(new Triangle(0, null, null, null));
        t.add(new Triangle(3, null, null, null));
        t.add(new Triangle(2, null, null, null));
        t.add(new Triangle(4, null, null, null));
        
        proj.setTriangles(t);
        
        GeoCPMUtilities.checkMissingTriangles(proj);
        
        assertTrue(t instanceof List);
        for(int i = 0; i < t.size(); ++i) {
            assertEquals(((List<Triangle>)t).get(i).getId(), i);
        }
    }

}