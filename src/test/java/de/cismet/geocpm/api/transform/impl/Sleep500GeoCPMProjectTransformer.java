package de.cismet.geocpm.api.transform.impl;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class Sleep500GeoCPMProjectTransformer implements GeoCPMProjectTransformer {
    
    public static final String TRANSFORMED_NAME = "sleep500 passed"; // NOI18N

    @Override
    public boolean accept(GeoCPMProject obj) {
        return true;
    }

    @Override
    public GeoCPMProject transform(GeoCPMProject obj) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            // noop
        }
        
        obj.setName(TRANSFORMED_NAME);
        
        return obj;
    }

}
