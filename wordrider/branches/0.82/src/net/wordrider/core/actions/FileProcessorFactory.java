package net.wordrider.core.actions;

import net.wordrider.utilities.Utils;

import java.io.File;

/**
 * @author Vity
 */
class FileProcessorFactory {
    private final static FileProcessorFactory instance = new FileProcessorFactory();


    private FileProcessorFactory() {
    }

    public static FileProcessorFactory getInstance() {
        return instance;
    }

    public FileProcessor getProcessorByFile(final File file) {
        final String extension = Utils.getExtension(file);
        if (extension.equals("89y") || extension.equals("9xy"))
            return new NotefolioFileProcessor(file);
        if (extension.equals("txt"))
            return new PureTextFileProcessor(file);
        else return new TextFileProcessor(file);
    }
}
