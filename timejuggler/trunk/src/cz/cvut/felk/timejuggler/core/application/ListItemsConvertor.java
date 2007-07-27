package cz.cvut.felk.timejuggler.core.misc;

import application.ResourceConverter;
import application.ResourceMap;

/**
 * @author Vity
 */
public class ListItemsConvertor extends ResourceConverter {
    public ListItemsConvertor() {
        super(String[].class);
    }

    public Object parseString(String s, ResourceMap r) throws ResourceConverterException {
        return s.split("\\|");
    }
}
