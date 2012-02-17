package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.DownloadFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Preusporadani seznamu
 *
 * @author Ludek Zika
 */

public class NiceOrder {


    List<DownloadFile> files;
    ArrayList<DownloadFile> notpauzedfiles;

        public NiceOrder() {
        notpauzedfiles = new ArrayList<DownloadFile>();
    }

    public List<DownloadFile> getNotPauzed() {
        return notpauzedfiles;

    }

    public void reorder(List<DownloadFile> kfiles) {
        this.files = kfiles;

        ArrayList<HostWithFiles> services = new ArrayList<HostWithFiles>();
        for (DownloadFile f : files) {

            String sName = f.getFileUrl().getHost();
            int position = services.indexOf(new HostWithFiles(sName));
            if (position > -1) {
                services.get(position).addFile(f);
            } else {
                HostWithFiles sf = new HostWithFiles(sName);
                sf.addFile(f);
                services.add(sf);
            }
        }

        int i = 0;
        int shift = 0;
        int olddiv = 0;

        int sizeOfServices = services.size();
        int countOfFiles = services.get(0).count();
        boolean even = (sizeOfServices%countOfFiles==0)||(countOfFiles%sizeOfServices ==0);
        int endOfCycle;
        if(sizeOfServices >countOfFiles) endOfCycle = sizeOfServices;
         else endOfCycle= countOfFiles;
        while (i < sizeOfServices * countOfFiles) {
            HostWithFiles sf = services.get((i+shift) % sizeOfServices);
                //   System.out.println("i " + i + "   " + ((i+shift) % sizeOfServices) + "     " + (i % countOfFiles)+"   i/sizeOfServices" + (i / sizeOfServices));
            if ((i % countOfFiles) < sf.count()) {
                DownloadFile fls = sf.getFile((i % countOfFiles));
                files.remove(fls);
                files.add(fls);
                if(i<countOfFiles) notpauzedfiles.add(fls);
            }
            i++;
            if( even && i/endOfCycle>olddiv){
             olddiv=i/ sizeOfServices;
             shift++;
            }
        }

    }

}

class HostWithFiles {
    private final String service;
    ArrayList<DownloadFile> lfiles;

    public String getService() {
        return service;
    }

    HostWithFiles(String service) {
        this.service = service;
        lfiles = new ArrayList<DownloadFile>();
    }

    void addFile(DownloadFile df) {
        lfiles.add(df);

    }

    DownloadFile getFile(int index) {
        return lfiles.get(index);
    }

    public int count() {
        return lfiles.size();
    }

    public boolean equals(Object obj) {
        if (obj instanceof HostWithFiles) {
            HostWithFiles sr = (HostWithFiles) obj;
            return service.equals(sr.getService());
        }
        if (obj instanceof String) {
            String st = (String) obj;
            return service.equals(st);
        }

        return false;
    }

}
