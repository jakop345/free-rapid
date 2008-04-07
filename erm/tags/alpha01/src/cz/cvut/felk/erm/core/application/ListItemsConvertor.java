package cz.cvut.felk.erm.core.application;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

/**
 * @author Ladislav Vitasek
 */
public class ListItemsConvertor extends ResourceConverter {
    public ListItemsConvertor() {
        super(String[].class);
    }

    public Object parseString(String s, ResourceMap r) throws ResourceConverterException {
        return s.split("\\|");
    }
}
