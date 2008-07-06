package cz.cvut.felk.erm.swing.components;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import cz.cvut.felk.erm.swing.ComponentFactory;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.swing.painters.ColorPainterFactory;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.PatternModel;
import org.jdesktop.swingx.action.AbstractActionExt;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public class FindBar extends JXFindBar {
    private final JTextComponent component;
    private Highlighter.HighlightPainter colorHighlighter;
    private CaretListener caretListener;

    public FindBar(JTextComponent component) {
        super(new DocumentSearchableWrapper(component));
        this.component = component;
        colorHighlighter = ColorPainterFactory.createColorPainter(Color.YELLOW);
        getPatternModel().setFoundIndex(component.getCaretPosition());
        this.caretListener = new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                getPatternModel().setFoundIndex(e.getMark());
            }
        };
        component.addCaretListener(this.caretListener);
        init();
    }

    public void deinstall() {
        component.removeCaretListener(caretListener);
        setSearchable(null);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        component.getHighlighter().removeAllHighlights();
        component.select(component.getCaretPosition(), component.getCaretPosition());
    }

    @Override
    protected void bindSearchLabel(Locale locale) {

    }

    public JTextComponent getSearchField() {
        return this.searchField;
    }


    @Override
    protected void refreshModelFromDocument() {
        updateFindAll();
        super.refreshModelFromDocument();
    }

    private void updateFindAll() {
        final DefaultHighlighter h = (DefaultHighlighter) component.getHighlighter();
        h.setDrawsLayeredHighlights(false);
        h.removeAllHighlights();

        final String raw = searchField.getText();
        //final boolean caseSensitive = getPatternModel().isCaseSensitive();
        //System.out.println("caseSensitive = " + caseSensitive);
        final int foundIndex = getPatternModel().getFoundIndex();
        if (!raw.isEmpty()) {
            final String text = Pattern.quote(raw);
            final Pattern pattern = Pattern.compile(text, !getPatternModel().isCaseSensitive() ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
            int i = 0;
            final int size = raw.length();
            while ((i = searchable.search(pattern, i)) != -1) {
                //System.out.println("i = " + i);
                try {
                    h.addHighlight(i, i + size, colorHighlighter);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                i += size;
            }
        }
        getPatternModel().setFoundIndex(foundIndex);
    }

    @Override
    protected void build() {

    }

    @Override
    protected void bind() {
        searchField.addActionListener(getAction(JXDialog.EXECUTE_ACTION_COMMAND));
        findNext.setAction(getAction(FIND_NEXT_ACTION_COMMAND));
        findPrevious.setAction(getAction(FIND_PREVIOUS_ACTION_COMMAND));
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke,
                JXDialog.CLOSE_ACTION_COMMAND);

        searchField.getDocument().addDocumentListener(getSearchFieldListener());
//        matchCheck.setAction(getAction(PatternModel.MATCH_CASE_ACTION_COMMAND));
        getActionContainerFactory().configureButton(matchCheck,
                (AbstractActionExt) getActionMap().get(PatternModel.MATCH_CASE_ACTION_COMMAND),
                null);
        matchCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFindAll();
                findNext();
            }
        });
        final ResourceMap map = Swinger.getResourceMap(FindBar.class);
        map.injectComponents(this);

        InputMap inputMap = component.getInputMap();
        final KeyStroke k1 = map.getKeyStroke("findNextAction.accelerator");
        inputMap.put(k1, findNext.getAction());
        final KeyStroke k2 = map.getKeyStroke("findPreviousAction.accelerator");
        inputMap.put(k2, findPrevious.getAction());
        component.setInputMap(WHEN_FOCUSED, inputMap);
        inputMap = searchField.getInputMap();
        inputMap.put(k1, findNext.getAction());
        inputMap.put(k2, findPrevious.getAction());
        searchField.setInputMap(WHEN_FOCUSED, inputMap);

    }

    public Action getMatchCaseAction() {
        return matchCheck.getAction();
    }

    @Override
    protected void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("FindBar");
        JPanel findBar = this;
        searchField = ComponentFactory.getTextField();
        findPrevious = ComponentFactory.getToolbarButton();
        findNext = ComponentFactory.getToolbarButton();
        matchCheck = new JCheckBox();
        CellConstraints cc = new CellConstraints();
        searchField.setName("searchField");
        //======== findBar ========
        {
            //findBar.setBorder(new EmptyBorder(0, 3, 0, 0));

            //---- searchField ----
            //searchField.setPreferredSize(new Dimension(100, 21));

            //---- findPrevious ----

            findPrevious.setName("findPrevious");

            //---- findNext ----

            findNext.setName("findNext");

            //---- matchCheck ----
            matchCheck.setName("matchCheck");

            PanelBuilder findBarBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec("max(pref;60dlu):grow"),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.PREF_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC
                    },
                    RowSpec.decodeSpecs("default")), findBar);
            ((FormLayout) findBar.getLayout()).setColumnGroups(new int[][]{{3, 5}});

            findBarBuilder.add(searchField, cc.xy(1, 1));
            findBarBuilder.add(findPrevious, cc.xy(3, 1));
            findBarBuilder.add(findNext, cc.xy(5, 1));
            findBarBuilder.add(matchCheck, cc.xy(7, 1));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}
