package net.wordrider.core.actions;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * @author Vity
 */
public interface DocumentProducer {
    Document process(JProgressBar progress) throws BadLocationException;
}
