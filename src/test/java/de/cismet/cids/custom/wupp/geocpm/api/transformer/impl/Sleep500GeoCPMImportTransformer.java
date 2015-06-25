package de.cismet.cids.custom.wupp.geocpm.api.transformer.impl;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMImportTransformer;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
@RequiredArgsConstructor
public class Sleep500GeoCPMImportTransformer implements GeoCPMImportTransformer {

    public static final String TRANSFORMED_NAME = "sleep500 passed"; // NOI18N
    
    private final int noOfProjects;

    @Override
    public boolean accept(Object obj) {
        return true;
    }

    @Override
    public Collection<GeoCPMProject> transform(Object obj) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            // noop
        }
        
        final Collection<GeoCPMProject> c = new ArrayList<>(noOfProjects);
        
        for(int i = 0; i < noOfProjects; ++i) {
            final GeoCPMProject p = new GeoCPMProject();
            p.setName(TRANSFORMED_NAME);
            c.add(p);
        }
        
        return c;
    }

}
