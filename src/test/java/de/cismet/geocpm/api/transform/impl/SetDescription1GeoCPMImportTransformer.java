package de.cismet.geocpm.api.transform.impl;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class SetDescription1GeoCPMImportTransformer implements GeoCPMImportTransformer {

    @Override
    public boolean accept(Object obj) {
        return true;
    }

    @Override
    public Collection<GeoCPMProject> transform(Object obj) {
        final Collection<GeoCPMProject> c = new ArrayList<>();
        
        GeoCPMProject p = new GeoCPMProject();
        p.setDescription("1");
        c.add(p);
        
        p = new GeoCPMProject();
        p.setDescription("1");
        c.add(p);
        
        p = new GeoCPMProject();
        p.setDescription("1");
        c.add(p);
        
        return c;
    }
}
