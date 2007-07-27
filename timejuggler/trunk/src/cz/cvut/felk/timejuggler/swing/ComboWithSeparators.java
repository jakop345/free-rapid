package cz.cvut.felk.timejuggler.swing;

public class ComboWithSeparators {
//    public ComboWithSeparators() {
//        setModel(createModel());
//        setRenderer(createRenderer());
//        setSelectedIndex(0);
//        setMaximumRowCount(20);
//    }
//
//    private ComboBoxModel createModel() {
//        return new NaiiveComboModel();
//    }

//    private static class NaiiveComboModel extends DefaultComboBoxModel {
//
//             public void setSelectedItem(Object o) {
//                 super.setSelectedItem();
//            if (o instanceof ComboItem) {
//                // If the user tries to select a separator...
//                if (((ComboItem) o).getDelegate() == null) {
//                    if (m_selectedItem != null) {
//                        int oldIndex = m_selectedItem.getIndex();
//                        int newIndex = ((ComboItem) o).getIndex();
//
//                        if (newIndex < oldIndex) {
//                            // Select the item before the separator.
//                            if (newIndex - 1 >= 0) {
//                                m_selectedItem = (ComboItem) getItems()[newIndex - 1];
//                            }
//                        } else if (newIndex > oldIndex) {
//                            // Select the item after the separator.
//                            if (newIndex + 1 < getItems().length) {
//                                m_selectedItem = (ComboItem) getItems()[newIndex + 1];
//                            }
//                        }
//
//                    }
//                } else {
//                    m_selectedItem = (ComboItem) o;
//                }
//
//                super.fireContentsChanged(this, -1, -1);
//            }
//        }
//
//    }


}

