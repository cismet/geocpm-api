package de.cismet.cids.custom.wupp.geocpm.api.transformer.impl;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class CountingSleepGeoCPMProjectTransformer implements GeoCPMProjectTransformer {
    
    private final long sleepTime;
    private final int expectedStep;
    

    public CountingSleepGeoCPMProjectTransformer(final long sleepTime, final int expectedStep) {
        this.sleepTime = sleepTime;
        this.expectedStep = expectedStep;
    }
    
    @Override
    public boolean accept(GeoCPMProject obj) {
        return obj.getAnnuality() == expectedStep;
    }

    @Override
    public GeoCPMProject transform(GeoCPMProject obj) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            return obj;
        }
        
        obj.setAnnuality(obj.getAnnuality() + 1);
        obj.setName((obj.getName() == null ? "" : obj.getName() + " ") + expectedStep);
        
        return obj;
    }
}
