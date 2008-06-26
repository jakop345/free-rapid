package cz.cvut.felk.erm.binding;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

/**
 * @author Ladislav Vitasek
 */
public final class NullToBooleanConverter extends AbstractConverter {

    public NullToBooleanConverter(ValueModel booleanSubject) {
        super(booleanSubject);
    }


    @Override
    public Object convertFromSubject(Object subjectValue) {
        return convert(subjectValue);
    }

    public void setValue(Object newValue) {
        subject.setValue(convert(newValue));
    }

    private Boolean convert(Object value) {
        return value != null && (Boolean) value;
    }

}
