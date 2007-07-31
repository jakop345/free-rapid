package cz.cvut.felk.timejuggler.swing;

import com.jgoodies.forms.layout.ColumnSpec;
import cz.cvut.felk.timejuggler.swing.components.EditorPaneLinkDetector;
import cz.cvut.felk.timejuggler.swing.renderers.ComboBoxRenderer;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.FocusListener;
import java.text.DateFormat;

/**
 * Trida slouzici k instanciovani upravenych zakladnich komponent
 * @author Vity
 */

public class ComponentFactory {
    private FocusListener focusListener;

    private static ComponentFactory instance;
    public final static ColumnSpec DATEPICKER_COLUMN_SPEC = new ColumnSpec("max(pref;65dlu)");

    private synchronized static ComponentFactory getInstance() {
        if (instance == null) {
            instance = new ComponentFactory();
        }
        return instance;
    }

    private ComponentFactory() {
        focusListener = new Swinger.SelectAllOnFocusListener();
    }

    private FocusListener getFocusListener() {
        return focusListener;
    }

    public static JSpinner getTimeSpinner() {
        final JSpinner spinner = new JSpinner(new SpinnerDateModel());
        ((JSpinner.DateEditor) spinner.getEditor()).getTextField().setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(DateFormat.getTimeInstance(DateFormat.SHORT))));
        return spinner;
    }

    public static JXDatePicker getDatePicker() {
        final JXDatePicker picker = new JXDatePicker();
        picker.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));
        return picker;
    }

    public static JComboBox getComboBox() {
        JComboBox combo = new JComboBox(new NaiiveComboModel());
        combo.setRenderer(new ComboBoxRenderer());
        return combo;
    }

    public static JTextArea getTextArea() {
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        return textArea;
    }

    public static JTextField getTextField() {
        final JTextField field = new JTextField();
        field.addFocusListener(ComponentFactory.getInstance().getFocusListener());
        return field;
    }


    public static EditorPaneLinkDetector getEmailsEditorPane() {
        return new EditorPaneLinkDetector();
    }

}
