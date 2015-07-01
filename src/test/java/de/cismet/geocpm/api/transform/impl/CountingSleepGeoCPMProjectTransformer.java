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
public class CountingSleepGeoCPMProjectTransformer implements GeoCPMProjectTransformer {
    
    private final long sleepTime;
    private final int expectedStep;

    public CountingSleepGeoCPMProjectTransformer() {
        this.sleepTime = 0;
        this.expectedStep = 1;
    }
    
    @Override
    public boolean accept(GeoCPMProject obj) {
        return obj.getDescription().equals(String.valueOf(expectedStep));
    }

    @Override
    public GeoCPMProject transform(GeoCPMProject obj) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            return obj;
        }
        
        obj.setDescription(String.valueOf(Integer.parseInt(obj.getDescription()) + 1));
        obj.setName((obj.getName() == null ? "" : obj.getName() + " ") + expectedStep);
        
        return obj;
    }
}
