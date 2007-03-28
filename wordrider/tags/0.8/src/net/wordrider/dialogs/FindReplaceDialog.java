/*
* Copyright (C) 2004 Ladislav Vitasek
* info@wordrider.net
* http://www.wordrider.net
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package net.wordrider.dialogs;

import net.wordrider.area.AlphaBetaKeyListener;
import net.wordrider.area.RiderEditorKit;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.swing.RecentsComboModel;
import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

public final class FindReplaceDialog extends AppDialog {
    private int mode;

    private JEditorPane editor;

    private Document doc;

    private String searchText,
            phrase,
            newPhrase;


    private int lastPos,
            offset,
            replaceDiff;

    private boolean findInProgress = false;

    private int operation;

    private int replaceChoice;

    private final Vector listeners = new Vector(0);

    private static final Object[] replaceOptions = {Lng.getLabel("dialog.findr.btnYes"), Lng.getLabel("dialog.findr.btnNo"), Lng.getLabel("dialog.findr.btnAll"), Lng.getLabel("dialog.findr.btnDone")};

    /* Constants for method toggleState */
    private static final boolean STATE_LOCKED = false;
    private static final boolean STATE_UNLOCKED = true;

    /* Constants for replaceOptions */
    private static final int RO_YES = 0;
    private static final int RO_NO = 1;
    private static final int RO_ALL = 2;
    private static final int RO_DONE = 3;

    /* Constants for dialog mode */
    private static final int MODE_DOCUMENT = 1;
    private static final int MODE_PROJECT = 2;

    /* Constants for operation */
    private static final int OP_NONE = 0;
    private static final int OP_FIND = 1;
    private static final int OP_REPLACE = 2;

    /* ---- GUI elements start ---------*/

    private final JButton btnFindNext = Swinger.getButton("dialog.findr.findNext");
    private final JCheckBox jcbStartOnTop = Swinger.getCheckBox("dialog.findr.fromStart");
    private final JRadioButton radioButtonDown = new JRadioButton();
    private final JCheckBox jcbWholeWords = Swinger.getCheckBox("dialog.findr.whole");
    private final JPanel panelOptions = new JPanel();
    private final JPanel panelFind = new JPanel();
    private JComboBox textFieldReplace;
    private final JPanel panelMain = new JPanel();
    private final JRadioButton radioButtonUp = new JRadioButton();
    private JComboBox textFieldPhrase;
    private final JCheckBox jcbMatchCase = Swinger.getCheckBox("dialog.findr.case");
    private final JLabel jLabel3 = new JLabel();
    private final JLabel jLabel4 = new JLabel();
    private final GridBagLayout gridBagLayout5 = new GridBagLayout();
    private final JButton btnClose = Swinger.getButton("dialog.findr.closeBtn");
    private final GridBagLayout gridBagLayout6 = new GridBagLayout();
    private final JButton btnReplace = Swinger.getButton("dialog.findr.replaceBtn");
    private final JButton btnCancel = Swinger.getButton("dialog.findr.cancelBtn");
    //    private final JCheckBox jcbUnused = Swinger.getRiderCheckBox();
    private final JCheckBox jcbProject = new JCheckBox();

    private int replaceCount;

    private final static String PHRASE_PREFERENCES = "findPhrase",
            REPLACE_PREFERENCES = "replacePhrase";
    private final static Logger logger = Logger.getLogger(FindReplaceDialog.class.getName());

    public FindReplaceDialog(final Frame owner, final JEditorPane editor) {
        super(owner, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setEditor(editor);
        setMode(MODE_DOCUMENT);
        try {
            init();
            initDialogContents();
            centerDialog(owner);
            this.setTitle(Lng.getLabel("dialog.findr.title"));
            pack();
            setVisible(true);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    protected void processWindowEvent(final WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            logger.info("Saving find/replace strings to preferences");
            saveUsedPhrases();
        }
        super.processWindowEvent(e);    //call to super
    }

    //    public final void addFindReplaceListener(final FindReplaceListener listener) {
    //        listeners.addElement(listener);
    //    }
    //
    //    public final void removeFindReplaceListener(final FindReplaceListener listener) {
    //        listeners.removeElement(listener);
    //    }

    protected AbstractButton getCancelButton() {
        if (btnCancel.isEnabled())
            return btnCancel;
        else
            return btnClose;
    }

    //    private void fireGetNextDocument() {
    //        final Enumeration listenerList = listeners.elements();
    //        while (listenerList.hasMoreElements()) {
    //            ((FindReplaceListener) listenerList.nextElement()).getNextDocument(new FindReplaceEvent(this));
    //        }
    //    }
    //
    //    private void fireGetFirstDocument() {
    //        final Enumeration listenerList = listeners.elements();
    //        while (listenerList.hasMoreElements()) {
    //            ((FindReplaceListener) listenerList.nextElement()).getFirstDocument(new FindReplaceEvent(this));
    //        }
    //    }

    // --Commented out by Inspection START (4.2.05 16:20):
    //    public final void resumeOperation() {
    //        this.doc = editor.getDocument();
    //        findInProgress = false;
    //        initFind();
    //        switch (operation) {
    //            case OP_FIND:
    //                find();
    //                break;
    //            case OP_REPLACE:
    //                replace();
    //                break;
    //            default :
    //                break;
    //        }
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:20)

    private void terminateOperation() {
        switch (operation) {
            case OP_FIND:
                message(Lng.getLabel("dialog.findr.notfound"));
                toggleState(STATE_UNLOCKED);
                btnReplace.setEnabled(true);
                break;
            case OP_REPLACE:
                switch (replaceChoice) {
                    case RO_YES:
                    case RO_NO:
                        message(Lng.getLabel("dialog.findr.notfound"));
                        break;
                    case RO_ALL:
                        message(Lng.getLabel("dialog.findr.replaceCount", replaceCount));
                        break;
                    default:
                        break;
                }
                toggleState(STATE_UNLOCKED);
                editor.firePropertyChange("undoredo", true, false);
                setVisible(true);
                break;
        }
        operation = OP_NONE;
    }

    private void btnFindNext_actionPerformed() {
        operation = OP_FIND;
        btnReplace.setEnabled(false);
        //setVisible(false);
        if (mode == MODE_PROJECT &&
                jcbProject.isSelected() &&
                !listeners.isEmpty() &&
                !findInProgress) {
            //   fireGetFirstDocument();
        } else {
            initFind();
            find();
        }
    }

    private void btnReplace_actionPerformed() {
        operation = OP_REPLACE;
        replaceChoice = RO_YES;
        setVisible(false);
        if (mode == MODE_PROJECT &&
                jcbProject.isSelected() &&
                !listeners.isEmpty()) {
            //fireGetFirstDocument();
        } else {
            initFind();
            replace();
        }
        Swinger.inputFocus(textFieldReplace);
    }

    private void btnCancel_actionPerformed() {
        toggleState(STATE_UNLOCKED);
        btnReplace.setEnabled(true);
        Swinger.inputFocus(textFieldPhrase);
    }

    private void storeProperties(final String keyProperties, final JComboBox combo) {
        final Collection collection = ((RecentsComboModel) combo.getModel()).getList();
        int counter = collection.size() - 1;
        for (final Iterator it = collection.iterator(); it.hasNext(); --counter) {
            AppPrefs.storeProperty(keyProperties + counter, it.next().toString());
        }
    }

    private void saveUsedPhrases() {
        storeProperties(PHRASE_PREFERENCES, textFieldPhrase);
        storeProperties(REPLACE_PREFERENCES, textFieldReplace);
    }

    private void btnClose_actionPerformed() {
        saveUsedPhrases();
        //result = JOptionPane.OK_OPTION;
        dispose();
    }

    private void setEditor(final JEditorPane editor) {
        this.editor = editor;
        this.doc = editor.getDocument();
    }

    private void setMode(final int mode) {
        this.mode = mode;
        if (mode == MODE_PROJECT) {
            jcbProject.setVisible(true);
        } else {
            jcbProject.setVisible(false);
        }
    }

    private void initFind() {
        if (!findInProgress) {
            try {
                searchText = doc.getText(0, doc.getLength());
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
            phrase = textFieldPhrase.getEditor().getItem().toString();
            ((RecentsComboModel) textFieldPhrase.getModel()).addElement(phrase);
            newPhrase = textFieldReplace.getEditor().getItem().toString();
            ((RecentsComboModel) textFieldReplace.getModel()).addElement(newPhrase);
            replaceDiff = newPhrase.length() - phrase.length();
            offset = 0;
            if (!jcbMatchCase.isSelected()) {
                phrase = phrase.toLowerCase();
                searchText = searchText.toLowerCase();
            }
            if (jcbStartOnTop.isSelected()) {
                if (radioButtonUp.isSelected()) {
                    lastPos = doc.getLength();
                } else {
                    lastPos = 0;
                }
            } else {
                lastPos = editor.getSelectionStart();
            }
            toggleState(STATE_LOCKED);
        }
    }

    private void find() {
        if (!doFind()) {
            if (mode == MODE_PROJECT &&
                    jcbProject.isSelected() &&
                    !listeners.isEmpty()) {
                //fireGetNextDocument();
            } else {
                terminateOperation();
            }
            Swinger.inputFocus(textFieldPhrase);
        } else {
            btnFindNext.setEnabled(true);
            Swinger.inputFocus(btnFindNext);
        }
    }

    private boolean doFind() {
        boolean found = false;
        int start = findNext();
        if (jcbWholeWords.isSelected()) {
            start = findWholeWords(start);
        }
        if (start >= 0) {
            lastPos = start;
            if (radioButtonDown.isSelected()) {
                start += offset;
            }
            editor.select(start, start + phrase.length());
            found = true;
        }
        return found;
    }

    private int findNext() {
        final int start;
        if (radioButtonUp.isSelected()) {
            if (lastPos < doc.getLength()) {
                start = searchText.lastIndexOf(phrase, lastPos - 1);
            } else {
                start = searchText.lastIndexOf(phrase, lastPos);
            }
        } else {
            if (lastPos > 0) {
                start = searchText.indexOf(phrase, lastPos + phrase.length());
            } else {
                start = searchText.indexOf(phrase, lastPos);
            }
        }
        return start;
    }

    private int findWholeWords(int start) {
        while ((start > 0) &&
                ((!RiderEditorKit.WORD_SEPARATORS.get(searchText.charAt(start - 1))) ||
                        (!RiderEditorKit.WORD_SEPARATORS.get(searchText.charAt(start + phrase.length()))))) {
            lastPos = start;
            start = findNext();
        }
        return start;
    }


    private void replace() {
        editor.firePropertyChange("undoredo", false, true);
        while (doFind() && replaceChoice != RO_DONE) {
            if (replaceChoice != RO_ALL)
                replaceChoice = getReplaceChoice();
            switch (replaceChoice) {
                case RO_YES:
                    replaceOne();
                    break;
                case RO_ALL:
                    replaceOne();
                    replaceCount = 1;
                    while (doFind()) {
                        replaceOne();
                        ++replaceCount;
                    }
                    break;
            }
        }
        if (mode == MODE_PROJECT &&
                jcbProject.isSelected() &&
                !listeners.isEmpty()) {
            switch (replaceChoice) {
                case RO_YES:
                case RO_NO:
                case RO_ALL:
                    //fireGetNextDocument();
                    break;
                case RO_DONE:
                    terminateOperation();
                    break;
                default:
                    terminateOperation();
            }
        } else
            terminateOperation();
    }

    private int getReplaceChoice() {
        return JOptionPane.showOptionDialog(this,
                Lng.getLabel("dialog.findr.confirmR", phrase),
                Lng.getLabel("dialog.findr.title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                replaceOptions,
                null);
    }

    private void replaceOne() {
        editor.replaceSelection(newPhrase);
        offset += replaceDiff;
    }

    private void initDialogContents() {
        btnCancel.setEnabled(false);
        radioButtonUp.setSelected(false);
        radioButtonDown.setSelected(true);
        jcbWholeWords.setSelected(false);
        jcbMatchCase.setSelected(false);
        jcbStartOnTop.setSelected(true);
        jcbProject.setSelected(false);
        textFieldPhrase.getEditor().setItem(editor.getSelectedText());
        btnFindNext.setEnabled(!Swinger.isEmpty(textFieldPhrase));
        btnReplace.setEnabled(btnFindNext.isEnabled());
        textFieldReplace.getEditor().setItem("");
    }

    private void centerDialog(final Frame owner) {
        final Dimension dlgSize = getPreferredSize();
        final Dimension frmSize = owner.getSize();
        final Point loc = owner.getLocation();
        setLocation((frmSize.width - dlgSize.width) / 2 + loc.x + 150,
                (frmSize.height - dlgSize.height) / 2 + loc.y - 50);
    }

    private void toggleState(final boolean unlocked) {
        btnCancel.setEnabled(!unlocked);
        btnFindNext.setEnabled(unlocked && !Swinger.isEmpty(textFieldPhrase));
        textFieldPhrase.setEnabled(unlocked);
        textFieldReplace.setEnabled(unlocked);
        jLabel3.setEnabled(unlocked);
        jLabel4.setEnabled(unlocked);
        jcbWholeWords.setEnabled(unlocked);
        jcbMatchCase.setEnabled(unlocked);
        jcbStartOnTop.setEnabled(unlocked);
        radioButtonUp.setEnabled(unlocked);
        radioButtonDown.setEnabled(unlocked);
        jcbProject.setEnabled(unlocked);
        findInProgress = !unlocked;
    }

    private void message(final String msgText) {
        JOptionPane.showMessageDialog(this,
                msgText,
                Lng.getLabel("dialog.findr.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /* ----------- Helper methods end ------- */

    private final class ActionKeyListener extends AlphaBetaKeyListener {
        public void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                final JTextField field = ((JTextField) e.getSource());
                e.consume();
                if (field.equals(textFieldPhrase.getEditor().getEditorComponent())) {
                    btnFindNext.doClick();
                    btnFindNext_actionPerformed();
                } else {
                    btnReplace.doClick();
                    btnReplace_actionPerformed();
                }
            } else super.keyPressed(e);
        }
    }

    private final class InputFieldsMethodListener implements DocumentListener {
        public void inputMethodTextChanged() {
            final boolean isEmpty = Swinger.isEmpty(textFieldPhrase);
            if (btnFindNext.isEnabled() != !isEmpty)
                btnFindNext.setEnabled(!isEmpty);
            if (btnReplace.isEnabled() != !isEmpty)
                btnReplace.setEnabled(!isEmpty);
        }

        public void changedUpdate(final DocumentEvent e) {
            inputMethodTextChanged();
        }

        public void insertUpdate(final DocumentEvent e) {
            inputMethodTextChanged();
        }

        public void removeUpdate(final DocumentEvent e) {
            inputMethodTextChanged();
        }
    }

    /**
     * GUI builder init
     */
    private void init() {
        final JPanel panelBtn = new JPanel(new EqualsLayout(EqualsLayout.TOP, 5));
        textFieldPhrase = new JComboBox(Swinger.loadSearchUsedList(PHRASE_PREFERENCES));
        textFieldReplace = new JComboBox(Swinger.loadSearchUsedList(REPLACE_PREFERENCES));
        textFieldPhrase.setEditable(true);
        textFieldReplace.setEditable(true);

        // textFieldReplace.getEditor().addActionListener();
        textFieldPhrase.setMaximumRowCount(Consts.MAX_RECENT_PHRASES_COUNT);
        textFieldReplace.setMaximumRowCount(Consts.MAX_RECENT_PHRASES_COUNT);
        final Border titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(142, 142, 142)), Lng.getLabel("dialog.findr.options"));
        final ButtonGroup bgSearchDirection = new ButtonGroup();
        btnFindNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                btnFindNext_actionPerformed();
            }
        });

        radioButtonDown.setText(Lng.getLabel("dialog.findr.searchDown"));
        radioButtonDown.setMnemonic(Lng.getMnemonic("dialog.findr.searchDown"));
        panelOptions.setBorder(BorderFactory.createCompoundBorder(titledBorder1, BorderFactory.createEmptyBorder(3, 2, 4, 2)));
        panelOptions.setLayout(new GridBagLayout());
        //   panelOptions.setPreferredSize(new Dimension(305, 88));
        panelFind.setLayout(gridBagLayout5);

        textFieldReplace.setMinimumSize(new Dimension(4, 12));
        textFieldReplace.setPreferredSize(new Dimension(30, 12));
        //textFieldReplace.setText("textFieldReplace");
        panelMain.setLayout(gridBagLayout6);
        radioButtonUp.setText(Lng.getLabel("dialog.findr.searchup"));
        radioButtonUp.setMnemonic(Lng.getMnemonic("dialog.findr.searchup"));
        //btnClose.setRolloverEnabled(true);

        textFieldPhrase.setMinimumSize(new Dimension(4, 12));
        textFieldPhrase.setPreferredSize(new Dimension(44, 12));
        final FocusListener focusListener = new Swinger.SelectAllOnFocusListener();
        final KeyListener keyListener = new ActionKeyListener();
        textFieldPhrase.addKeyListener(keyListener);
        Component editorComponent = textFieldPhrase.getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            final JTextComponent component = (JTextComponent) editorComponent;
            component.addKeyListener(keyListener);
            component.getDocument().addDocumentListener(new InputFieldsMethodListener());
            component.addFocusListener(focusListener);
            Swinger.addKeyActions(component);
        }
        editorComponent = textFieldReplace.getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            final JTextComponent component = (JTextComponent) editorComponent;
            component.addKeyListener(keyListener);
            component.addFocusListener(focusListener);
            Swinger.addKeyActions(component);
        }
        final Font font = RiderStyles.getAreaFont().deriveFont(Font.PLAIN, 12);
        textFieldPhrase.setFont(font);
        textFieldReplace.setFont(font);
        jLabel3.setText(Lng.getLabel("dialog.findr.replacewith"));
        jLabel4.setText(Lng.getLabel("dialog.findr.textfind"));
        jLabel3.setLabelFor(textFieldPhrase);
        jLabel3.setDisplayedMnemonic(Lng.getMnemonic("dialog.findr.replacewith"));
        jLabel4.setDisplayedMnemonic(Lng.getMnemonic("dialog.findr.textfind"));
        jLabel4.setLabelFor(textFieldReplace);


        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                btnClose_actionPerformed();
            }
        });


        btnReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                btnReplace_actionPerformed();
            }
        });


        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                btnCancel_actionPerformed();
            }
        });
        //  jcbUnused.setText("jcbUnused");
        //jcbUnused.setVisible(false);
        final Dimension buttonSize = new Dimension(75, 25);
        btnFindNext.setMinimumSize(buttonSize);
        btnClose.setMinimumSize(buttonSize);
        btnReplace.setMinimumSize(buttonSize);
        btnCancel.setMinimumSize(buttonSize);

        panelBtn.add(btnFindNext);
        panelBtn.add(btnClose);
        panelBtn.add(btnReplace);
        panelBtn.add(btnCancel);
        this.getContentPane().add(panelMain, BorderLayout.NORTH);
        panelMain.add(panelFind, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
        panelMain.add(panelBtn, new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0
                , GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
        panelFind.add(textFieldPhrase, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 0), 0, 12));
        panelFind.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
        panelFind.add(textFieldReplace, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 0), 0, 12));
        panelFind.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
        panelMain.add(panelOptions, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));

        panelOptions.add(jcbWholeWords, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(-10, 0, 0, 0), 10, 0));
        panelOptions.add(jcbMatchCase, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(-6, 0, -5, 0), 10, 0));
        panelOptions.add(jcbStartOnTop, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(-3, 0, -5, 0), 10, 0));
        panelOptions.add(radioButtonUp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(-10, 0, 0, 0), 0, 0));
        panelOptions.add(radioButtonDown, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(-3, 0, 0, 0), 0, 0));
        //  panelOptions.add(jcbUnused, null);
        //   panelOptions.add(jcbProject, null);
        bgSearchDirection.add(radioButtonUp);
        bgSearchDirection.add(radioButtonDown);
    }

}