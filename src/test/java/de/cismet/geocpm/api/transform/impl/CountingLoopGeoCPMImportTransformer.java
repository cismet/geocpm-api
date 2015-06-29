package de.cismet.geocpm.api.transform.impl;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
@RequiredArgsConstructor
public class CountingLoopGeoCPMImportTransformer implements GeoCPMImportTransformer {

    public static final String TRANSFORMED_NAME = "loop passed"; // NOI18N
    
    private final int noOfProjects;
    private final long countTo;

    public CountingLoopGeoCPMImportTransformer() {
        this.noOfProjects = 5;
        this.countTo = 1000000000;
    }
    
    @Override
    public boolean accept(Object obj) {
        return true;
    }

    @Override
    public Collection<GeoCPMProject> transform(Object obj) {
        final Collection<GeoCPMProject> c = new ArrayList<>(noOfProjects);
        for(long i = 0; i < countTo; ++i) {
            // making it a little bit slower
            String.valueOf(this.toString() + this.hashCode() + obj.toString());
            if(Thread.currentThread().isInterrupted()) {
                return c;
            }
        }
        
        for(int i = 0; i < noOfProjects; ++i) {
            final GeoCPMProject p = new GeoCPMProject();
            p.setName(TRANSFORMED_NAME);
            c.add(p);
        }
        
        return c;
    }

}
