package de.cismet.cids.custom.wupp.geocpm.api.transformer.impl;

import de.cismet.cids.custom.wupp.geocpm.api.GeoCPMProject;
import de.cismet.cids.custom.wupp.geocpm.api.transform.GeoCPMImportTransformer;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class SetAnnuality1GeoCPMImportTransformer implements GeoCPMImportTransformer {

    @Override
    public boolean accept(Object obj) {
        return true;
    }

    @Override
    public Collection<GeoCPMProject> transform(Object obj) {
        final Collection<GeoCPMProject> c = new ArrayList<>();
        
        GeoCPMProject p = new GeoCPMProject();
        p.setDescription("p1");
        p.setAnnuality(1);
        c.add(p);
        
        p = new GeoCPMProject();
        p.setDescription("p2");
        p.setAnnuality(1);
        c.add(p);
        
        p = new GeoCPMProject();
        p.setDescription("p3");
        p.setAnnuality(1);
        c.add(p);
        
        return c;
    }
}
