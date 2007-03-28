package net.wordrider.core.actions;

import net.wordrider.files.ti68kformat.FastTextFileReader;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vity
 */
public class GroupFileProcess {
    private Collection<Event> accidentsLog;

    public GroupFileProcess(File outputFile, GroupFileOptions options) {
        File outputFile1 = outputFile;
        GroupFileOptions options1 = options;
    }

    public void processFiles(Collection<File> files) {
        accidentsLog = new ArrayList<Event>(files.size() * 2);
        for (final File f : files) {
            if (!f.exists()) {
                generateEvent(Event.LOADFILE_NOTFOUND, f);
                continue;
            }
            FastTextFileReader reader = new FastTextFileReader();

            try {
                reader.openFromFile(f);
                generateEvent(Event.LOADFILE_OK, f);
            } catch (InvalidDataTypeException e) {
                generateEvent(Event.LOADFILE_BADFILETYPE, f);
            } catch (NotSupportedFileException e) {
                generateEvent(Event.LOADFILE_BADFILETYPE, f);
            } catch (IOException e) {
                generateEvent(Event.LOADFILE_ERROR, f);
            }

        }
    }

    private void generateEvent(final int type, File detailInfo) {
        accidentsLog.add(Event.generate(type, detailInfo));
    }

    private static class Event {
        public final static int LOADFILE_OK = 0;
        public final static int LOADFILE_NOTFOUND = 1;
        public final static int LOADFILE_BADFILETYPE = 2;
        public final static int LOADFILE_PIC_NOTFOUND = 3;
        public final static int LOADFILE_PIC_BADFILETYPE = 4;
        public final static int LOADFILE_ERROR = 5;

        private int type;
        private File detailInfo;

        public Event(final int type, final File detail) {
            this.type = type;
            this.detailInfo = detail;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public File getDetailInfo() {
            return detailInfo;
        }

        public void setDetailInfo(File detailInfo) {
            this.detailInfo = detailInfo;
        }

        public static Event generate(final int type, final File detail) {
            return new Event(type, detail);
        }
    }

}
