package net.wordrider.plugintools;

import net.wordrider.area.RiderDocument;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class BreakpointList extends PluginTool implements CaretListener, PropertyChangeListener, DocumentListener, ListSelectionListener {
    private static JTextComponent editor;
    private final static Logger logger = Logger.getLogger(BreakpointList.class.getName());

    private final JList list;

    /**
     * Model for the List.
     */
    private ListViewModel listModel;

    public BreakpointList() {
        super();

        //treeModel = new ListViewModel(document);
        list = new JList();
        try {
            list.addKeyListener(new UnsetBreakpointListener());
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(final MouseEvent e) {
                    if (e.getClickCount() == 2 && list.getSelectedIndex() != -1) {
                        getEditor().requestFocus();
                        e.consume();
                    }
                }
            });

            list.setFont(RiderStyles.getAreaFont());
            list.addListSelectionListener(this);

            // Since the display value of every node after the insertion point
            // changes every time the text changes and we don't generate a change
            // event for all those nodes the display value can become off.
            // This can be seen as '...' instead of the complete string value.
            // This is a temporary workaround, increase the needed size by 15,
            // hoping that will be enough.

            list.setCellRenderer(new MyCellRenderer());
            // become a listener on the document to update the List.
            final Container contentPane = getContentPane();
            // configure the panel and frame containing it.
            contentPane.setLayout(new BorderLayout());
            list.setBorder(new EmptyBorder(2, 2, 2, 4));

            contentPane.add(new JScrollPane(list), BorderLayout.CENTER);

            // Add a label above List to describe what is being shown

            getComponent().setPreferredSize(new Dimension(250, 300));
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    public final String getName() {
        return Lng.getLabel("breakpoints.title");
    }

    public String getTabName() {
        return getName();
    }

    public final Icon getIcon() {
        return Swinger.getIcon("view_bookm.gif");
    }

    public final String getTip() {
        return Lng.getLabel("breakpoints.tooltip");
    }

    public void activate() {
        setFileInstance(AreaManager.getInstance().getActiveInstance());
        logger.info("BreakpointList activated");
    }

    public void deactivate() {
        setFileInstance(null);
        logger.info("BreakpointList deactivated");
    }

    public void updateData() {
        if (editor != null && listModel != null)
            listModel.updateDataList();
    }

    public void setFileInstance(final IFileInstance instance) {
        if (instance != null)
            setEditor(instance.getRiderArea());
        else
            setEditor(null);
        getContentPane().setEnabled(editor != null);
    }


    private final class UnsetBreakpointListener extends KeyAdapter {
        /**
         * Invoked when a LASTOPENFOLDER_KEY has been pressed. See the class description for {@link
         * java.awt.event.KeyEvent} for a definition of a LASTOPENFOLDER_KEY pressed event.
         */
        public void keyPressed(final KeyEvent e) {
            //implement - call to super class
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                final Object[] selObjects = list.getSelectedValues();
                final int count = selObjects.length;
                final RiderDocument doc = (RiderDocument) getEditor().getDocument();
                for (int i = 0; i < count; ++i)
                    doc.toggleBookmark((Element) selObjects[i]);
                e.consume();
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                getEditor().requestFocus();
                e.consume();
            }

        }

    }

    private final class MyCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, listModel.getStringValue(value), index, isSelected, cellHasFocus);    //call to super
        }


        public final Dimension getPreferredSize() {
            final Dimension retValue = super.getPreferredSize();
            if (retValue != null)
                retValue.width += 5;
            return retValue;
        }
    }


    /**
     * Resets the JTextComponent to <code>newEditor</code>. This will update the List accordingly.
     */
    private void setEditor(final JTextComponent newEditor) {
        if (editor != null && editor.equals(newEditor))
            return;

        if (editor != null) {
            editor.getDocument().removeDocumentListener(this);
            editor.removePropertyChangeListener(this);
            editor.removeCaretListener(this);
        }
        editor = newEditor;
        if (newEditor == null) {
            listModel = null;
            list.setModel(new DefaultListModel());
        } else {
            final Document newDoc = newEditor.getDocument();
            newDoc.addDocumentListener(this);
            newEditor.addPropertyChangeListener(this);
            newEditor.addCaretListener(this);
            list.setModel(listModel = new ListViewModel(newDoc));
        }
    }


    /**
     * @return JTextComponent showing elements for.
     */
    private static JTextComponent getEditor() {
        return editor;
    }


    /**
     * Called when the caretPosition position is updated.
     * @param e the caretPosition event
     */
    public final void caretUpdate(final CaretEvent e) {
        if (!list.hasFocus()) {
            Element el = editor.getDocument().getDefaultRootElement();
            el = el.getElement(el.getElementIndex(e.getDot()));
            if (listModel.contains(el))
                list.setSelectedValue(el, true);
        }
    }

    /**
     * This method gets called when a bound property is changed.
     * @param event A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public final void propertyChange(final PropertyChangeEvent event) {
        if (event.getSource().equals(getEditor()) &&
                event.getPropertyName().equals("document")) {

            // Reset the DocumentListener
            ((Document) event.getOldValue()).removeDocumentListener(this);
            final Document newDoc = (Document) event.getNewValue();
            newDoc.addDocumentListener(this);
            // Recreate the ListModel.
            listModel = new ListViewModel(newDoc);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    list.setModel(listModel);
                }
            });
            //
            //updateList(event);
        }
    }

    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public final void valueChanged(final ListSelectionEvent e) {
        if (list.hasFocus() && list.getSelectedIndex() >= 0) {
            final Element selElement = (Element) list.getSelectedValue();
            final Rectangle r;
            try {
                r = editor.modelToView(selElement.getStartOffset());
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
                return;
            }
            if (r == null)
                return;
            r.y += -10;
            r.height = editor.getVisibleRect().height;
            //  editor.select(selElement.getStartOffset(), selElement.getEndOffset());

            editor.setSelectionStart(selElement.getStartOffset());
            editor.setSelectionEnd(selElement.getStartOffset());
            //            editor.setCaretPosition();
            editor.scrollRectToVisible(r);
            //  getEditor().requestFocus();
            //editor.setCaretPosition(selElement.getStartOffset());
        }
    }


    /**
     * Gives notification that an attribute or set of attributes changed.
     * @param e the document event
     */
    public final void changedUpdate(final DocumentEvent e) {
        updateList(e);
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     * @param e the document event
     */
    public final void insertUpdate(final DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateList(e);
            }
        });
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     * @param e the document event
     */
    public final void removeUpdate(final DocumentEvent e) {
        insertUpdate(e);
        //updateList(e);
    }


    /**
     * Updates the List based on the event type. This will invoke either updateList with the root element, or
     * handleChange.
     */
    private void updateList(final DocumentEvent event) {
        updateList(event, getEditor().getDocument().getDefaultRootElement());
    }

    /**
     * Creates ListModelEvents based on the DocumentEvent and messages the Listmodel. This recursively invokes this
     * method with children elements.
     * @param event   indicates what elements in the List hierarchy have changed.
     * @param element Current element to check for changes against.
     */
    private void updateList(final DocumentEvent event, final Element element) {
        final DocumentEvent.ElementChange ec = event.getChange(element);

        if (ec != null) {
            final Element[] removed = ec.getChildrenRemoved();
            final Element[] added = ec.getChildrenAdded();
            //     int startIndex = ec.getIndex();

            // Check for removed.
            if (removed != null && removed.length > 0) {
                for (Element aRemoved : removed) listModel.removeElement(aRemoved);
            }
            // check for added
            if (added != null && added.length > 0) {
                for (Element anAdded : added) listModel.addIfCan(anAdded);
            }
        }
        //        if (!element.isLeaf()) {
        listModel.checkValue(element.getElement(element.getElementIndex(event.getOffset())));
        listModel.updateData();
    }

    private static final class ListViewModel extends AbstractListModel {
        private final Vector<Element> delegate = new Vector<Element>();
        private final Map<Element, String> stringsMap = new Hashtable<Element, String>();
        private final Document document;
        boolean wasChanged = false;

        public ListViewModel(final Document document) {
            this.document = document;
            final Element root = document.getDefaultRootElement();
            final int elCount = root.getElementCount();
            Element el;
            for (int i = 0; i < elCount; ++i) {
                el = root.getElement(i);
                if (RiderStyles.isBookmark(el)) {
                    delegate.addElement(el);
                    stringsMap.put(el, convertValueToText(el));
                }
            }
            if (!delegate.isEmpty())
                fireContentsChanged(this, 0, delegate.size());
        }

        public final synchronized void updateDataList() {
            for (Object o : new ArrayList(stringsMap.keySet())) {
                final Element el = (Element) o;
                stringsMap.put(el, convertValueToText(el));
            }
            fireContentsChanged(this, 0, delegate.size());
        }

        private static final class MyComparator implements Comparator {
            public final int compare(final Object o1, final Object o2) {
                return ((Element) o1).getStartOffset() - ((Element) o2).getStartOffset();
            }
        }

        public final void addIfCan(final Element obj) {
            if (RiderStyles.isBookmark(obj)) {
                wasChanged = true;
                delegate.addElement(obj);
                stringsMap.put(obj, convertValueToText(obj));
            }
        }

        /**
         * Returns the length of the list.
         * @return the length of the list
         */
        public final int getSize() {
            return delegate.size();
        }

        /**
         * Returns the value at the specified index.
         * @param index the requested index
         * @return the value at <code>index</code>
         */
        public final Object getElementAt(final int index) {
            return (index < delegate.size()) ? delegate.elementAt(index) : null;
        }

        public final void updateData() {
            if (wasChanged && !delegate.isEmpty()) {
                logger.info("update list refresh");
                final SortedSet<Element> set = new TreeSet<Element>(new MyComparator());
                set.addAll(delegate);
                delegate.removeAllElements();
                delegate.addAll(set);
                wasChanged = false;
                fireContentsChanged(this, 0, delegate.size());
            }
        }


        public final void checkValue(final Element element) {
            final boolean isBookmark = RiderStyles.isBookmark(element);
            if (contains(element)) {
                if (!isBookmark)
                    removeElement(element);
                else {
                    final int index = delegate.indexOf(element); //redraw this element, text was changed
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            final String textValue;
                            if (!(textValue = convertValueToText(element)).equals(stringsMap.get(element))) {
                                stringsMap.put(element, textValue);
                                fireContentsChanged(this, index, index);
                            }
                        }
                    });
                }
            } else if (isBookmark) {
                delegate.addElement(element);
                stringsMap.put(element, convertValueToText(element));
                wasChanged = true;
            }

        }

        public final boolean contains(final Element elem) {
            return delegate.contains(elem);
        }

        public final void removeElement(final Element obj) {
            final int index = delegate.indexOf(obj);
            delegate.removeElement(obj);
            stringsMap.remove(obj);
            wasChanged = true;
            //fireContentsChanged(this, index, index);
            if (index >= 0)
                fireContentsChanged(this, index, index);
            //                fireIntervalRemoved(this, 0, index);

        }

        private String convertValueToText(final Element paraEl) {
            final int startOffset = paraEl.getStartOffset();

            try {
                final int rowEnd = Utilities.getRowEnd(getEditor(), startOffset);
                final String text = document.getText(startOffset, (rowEnd != -1) ? rowEnd - startOffset + 1 : paraEl.getEndOffset() - startOffset);
                return (text == null || text.equals("\n")) ? "-" : text;
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
                return "";
            }
        }

        public String getStringValue(final Object value) {
            return stringsMap.get(value);
        }

    }

    public boolean closeSoft() {
        return true;  //implement - call to super class
    }

    public void closeHard() {
        //implement - call to super class
    }
}
