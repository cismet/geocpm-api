package de.cismet.geocpm.api.transform.impl;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
@RequiredArgsConstructor
public class CountingLoopGeoCPMProjectTransformer implements GeoCPMProjectTransformer {
    
    private final long countTo;
    private final int expectedStep;

    public CountingLoopGeoCPMProjectTransformer() {
        this.countTo = 1000;
        this.expectedStep = 2;
    }
    
    @Override
    public boolean accept(GeoCPMProject obj) {
        return obj.getAnnuality() == expectedStep;
    }

    @Override
    public GeoCPMProject transform(GeoCPMProject obj) {
        for(long i = 0; i < countTo; ++i) {
            // making it a little bit slower
            String.valueOf(this.toString() + this.hashCode() + obj.toString());
            if(Thread.interrupted()) {
                return obj;
            }
        }
        
        obj.setAnnuality(obj.getAnnuality() + 1);
        obj.setName((obj.getName() == null ? "" : obj.getName() + " ") + expectedStep);
        
        return obj;
    }
}
