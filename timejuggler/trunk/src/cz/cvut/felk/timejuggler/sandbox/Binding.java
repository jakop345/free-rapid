package cz.cvut.felk.timejuggler.sandbox;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.factories.ButtonBarFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Binding {

    public static void main(String[] args) {

        MyBean bean = new MyBean();

        // create a new trigger for our presentation model, and store it in the presentation model.
        final Trigger trigger = new Trigger();
        PresentationModel adapter = new PresentationModel(bean, trigger);

        // Get buffered model objects.
        ValueModel booleanModel = adapter.getBufferedModel("booleanValue");
        ValueModel stringModel = adapter.getBufferedModel("stringValue");
        // creates a JCheckBox with the property adapter providing the underlying model.
        JCheckBox box = BasicComponentFactory.createCheckBox(booleanModel, "Boolean Value");
        JTextField field = BasicComponentFactory.createTextField(stringModel);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("OK Button Pressed");
                trigger.triggerCommit();
            }
        });

        // First, disable the OK button.
        okButton.setEnabled(false);
        // Note here that we wire the OK 'enabled' state to the presentation model 'buffering' state.
        PropertyConnector.connect(adapter, "buffering", okButton, "enabled");


        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                trigger.triggerFlush();
            }
        });

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new GridLayout(3, 1));
        frame.getContentPane().add(box);
        frame.getContentPane().add(field);
        frame.getContentPane().add(ButtonBarFactory.buildOKCancelBar(okButton, cancelButton));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
