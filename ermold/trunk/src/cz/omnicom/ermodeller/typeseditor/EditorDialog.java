package cz.omnicom.ermodeller.typeseditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorDialog extends JDialog implements ActionListener {

    private JButton OKButton = null;

    public EditorDialog(UserTypesEditor owner) {
        super(owner);
        initialize();
        initConnections();
    }

    protected void initialize() {
        getContentPane().setLayout(null);
        getContentPane().add(getOKButton());
    }

    protected void initConnections() {
        getOKButton().addActionListener(this);
    }

    protected JButton getOKButton() {
        if (OKButton == null) {
            OKButton = new JButton("OK");
            OKButton.setBounds(65, 205, 70, 25);
            OKButton.setEnabled(true);
            OKButton.setVisible(true);
        }
        return OKButton;
    }

    public void setOKButtonVisible(boolean choice) {
        if (choice)
            getOKButton().setVisible(true);
        else
            getOKButton().setVisible(false);
    }

    /**
     * removes all components except OKButton
     */
    public void removeAll() {
        for (int i = 0; i < getContentPane().getComponentCount(); i++) {
            if (!(getContentPane().getComponent(i) instanceof JButton))
                remove(getContentPane().getComponent(i));
        }
        getOKButton().setVisible(false);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 260);
    }

    public void actionPerformed(ActionEvent e) {
        //System.out.println("OK pressed!!");
        for (int i = 0; i < getContentPane().getComponentCount(); i++) {
            if (getContentPane().getComponent(i) instanceof ObjectTypeEditor)
                if (((ObjectTypeEditor) getContentPane().getComponent(i)).confirmName())
                    hide();
        }
	}
}