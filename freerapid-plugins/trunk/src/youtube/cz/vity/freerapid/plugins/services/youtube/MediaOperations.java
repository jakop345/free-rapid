package cz.vity.freerapid.plugins.services.youtube;

import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.builder.ItunesBuilder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.FlvAacTrackImpl;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.utilities.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * @author tong2shot
 * @author ntoskrnl
 */
class MediaOperations {

    private static final Logger logger = Logger.getLogger(MediaOperations.class.getName());

    private MediaOperations() {
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void multiplexDash(final HttpFileDownloadTask downloadTask, final boolean isAudio) throws IOException {
        if (downloadTask.isTerminated()) {
            logger.info("Download task was terminated");
            return;
        }
        logger.info("Multiplexing DASH streams");
        final HttpFile downloadFile = downloadTask.getDownloadFile();
        final File inputFile = downloadFile.getStoreFile();
        if (!inputFile.exists()) {
            logger.warning("Input file not found, multiplexing aborted");
            return;
        }

        File videoFile;
        File audioFile;
        String fnameNoExt = downloadFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", "");
        String fnameOutput = fnameNoExt + Container.mp4.getFileExt();
        if (isAudio) {
            videoFile = new File(downloadFile.getSaveToDirectory(), fnameNoExt + ".m4v");
            audioFile = inputFile;
        } else {
            videoFile = inputFile;
            audioFile = new File(downloadFile.getSaveToDirectory(), fnameNoExt + ".m4a");
        }
        if (!videoFile.exists()) {
            logger.info("DASH video file not found");
            return;
        }
        if (!audioFile.exists()) {
            logger.info("DASH audio file not found");
            return;
        }

        logger.info("DASH video file size: " + videoFile.length());
        logger.info("DASH audio file size: " + audioFile.length());

        FileOutputStream fos = null;
        FileDataSourceImpl videoFds = null;
        FileDataSourceImpl audioFds = null;
        File outputFile = new File(downloadFile.getSaveToDirectory(), fnameOutput);
        int outputFileCounter = 2;
        boolean finished = false;
        try {
            while (outputFile.exists()) {
                fnameOutput = fnameNoExt + "-" + outputFileCounter++ + Container.mp4.getFileExt();
                outputFile = new File(downloadFile.getSaveToDirectory(), fnameOutput);
            }
            fos = new FileOutputStream(outputFile);
            logger.info("Output file name: " + fnameOutput);
            downloadFile.setState(DownloadState.COMPLETED);

            videoFds = new FileDataSourceImpl(videoFile);
            audioFds = new FileDataSourceImpl(audioFile);
            Movie videoMovie = MovieCreator.build(videoFds);
            Movie audioMovie = MovieCreator.build(audioFds);
            Track audioTrack = audioMovie.getTracks().get(0);
            audioTrack.getTrackMetaData().setLanguage("eng");
            videoMovie.addTrack(audioTrack);
            com.coremedia.iso.boxes.Container out = new DefaultMp4Builder().build(videoMovie);
            out.writeContainer(fos.getChannel());
            logger.info("Output file size: " + fos.getChannel().position());
            finished = true;
        } finally {
            closeSilently(videoFds);
            closeSilently(audioFds);
            closeSilently(fos);
        }
        if (finished) {
            logger.info("Deleting DASH files");
            audioFile.delete();
            videoFile.delete();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void extractAudio(final HttpFileDownloadTask downloadTask, final Container container) throws IOException {
        if (downloadTask.isTerminated()) {
            logger.info("Download task was terminated");
            return;
        }
        logger.info("Extracting audio");
        final HttpFile downloadFile = downloadTask.getDownloadFile();
        final File inputFile = downloadFile.getStoreFile();
        if (!inputFile.exists()) {
            logger.warning("Input file not found, extraction aborted");
            return;
        }

        String tempSuffix = ".extract.temp";
        String fnameNoExt = downloadFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", "");
        String fnameOutput = fnameNoExt + ".m4a";
        String fnameTempOutput = fnameOutput + tempSuffix;
        FileOutputStream fos = null;
        File outputFile = new File(downloadFile.getSaveToDirectory(), fnameOutput);
        File tempOutputFile = new File(downloadFile.getSaveToDirectory(), fnameTempOutput);
        FileDataSourceImpl inputFds = null;
        int outputFileCounter = 2;
        boolean finished = false;
        try {
            while (outputFile.exists() || tempOutputFile.exists()) {
                fnameOutput = fnameNoExt + "-" + outputFileCounter++ + ".m4a";
                fnameTempOutput = fnameOutput + tempSuffix;
                outputFile = new File(downloadFile.getSaveToDirectory(), fnameOutput);
                tempOutputFile = new File(downloadFile.getSaveToDirectory(), fnameTempOutput);
            }
            fos = new FileOutputStream(tempOutputFile);
            logger.info("Temp output file name: " + fnameTempOutput);
            logger.info("Output file name: " + fnameOutput);
            downloadFile.setState(DownloadState.COMPLETED);

            Movie movie;
            inputFds = new FileDataSourceImpl(inputFile);
            if (container == Container.flv) {
                FlvAacTrackImpl flvAACTrack = new FlvAacTrackImpl(inputFds);
                movie = new Movie();
                movie.addTrack(flvAACTrack);
            } else { //mp4 or m4a
                movie = MovieCreator.build(inputFds);
            }
            com.coremedia.iso.boxes.Container out = new ItunesBuilder().build(movie);
            out.writeContainer(fos.getChannel());
            logger.info("Output file size: " + fos.getChannel().position());
            finished = true;
        } finally {
            closeSilently(inputFds);
            closeSilently(fos);
        }
        if (finished) {
            logger.info("Renaming temp file to output file");
            tempOutputFile.renameTo(outputFile);
            logger.info("Deleting input file");
            inputFile.delete();
        }
    }

    private static void closeSilently(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    private static void closeSilently(DataSource ds) {
        if (ds != null) {
            try {
                ds.close();
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

}
