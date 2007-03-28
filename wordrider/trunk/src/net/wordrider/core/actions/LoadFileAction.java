package net.wordrider.core.actions;

import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class LoadFileAction extends RiderSwingWorker {
    private final DocumentProducer documentProducer;
    private final static Logger logger = Logger.getLogger(LoadFileAction.class.getName());

    public LoadFileAction(final DocumentProducer batchTextRead) {
        super(true);    //call to super
        this.documentProducer = batchTextRead;
        //   dialogToClose.setTitle("message.loading");
    }


    public final Object construct() {
        final JProgressBar progress = dialogToClose.getProgressBar();
        progress.setIndeterminate(false);
        progress.setStringPainted(true);
        showInfoWhileLoading("message.loading");
        try {            
            return documentProducer.process(progress);
        } catch (BadLocationException e) {
            errorMessage = e.getMessage();
            LogUtils.processException(logger, e);
            return null;
        }
    }
}
