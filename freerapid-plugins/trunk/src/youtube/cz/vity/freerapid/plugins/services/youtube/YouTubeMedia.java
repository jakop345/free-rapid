package cz.vity.freerapid.plugins.services.youtube;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;

class YouTubeMedia {
    private final int itag;
    private final Container container;
    private final int videoQuality; // deliberately not using VideoQuality, reason : flexibility, it's possible that YT introduces video quality which is not listed in VideoQuality data structure
    private final DashType dashType;
    private final int frameRate;
    private final AudioEncoding audioEncoding;
    private final int audioBitrate;
    private final String url;
    private final String signature;
    private final boolean cipherSignature;
    private final String fileExt;

    private static enum DashType {
        VIDEO, AUDIO, NONE
    }

    public YouTubeMedia(int itag, String url, String signature, boolean cipherSignature) throws ErrorDuringDownloadingException {
        this.itag = itag;
        this.container = getContainer(itag);
        this.dashType = getDashType(itag);
        this.fileExt = (dashType == DashType.NONE ? container.getFileExt() : (dashType == DashType.VIDEO ? ".m4v" : ".m4a"));
        this.videoQuality = (isDashAudio() ? -1 : getVideoResolution(itag));
        this.frameRate = (isDashAudio() ? -1 : getFrameRate(itag));
        this.audioEncoding = (isDashVideo() ? AudioEncoding.None : getAudioEncoding(itag));
        this.audioBitrate = (isDashVideo() ? -1 : getAudioBitrate(itag));
        this.url = url;
        this.signature = signature;
        this.cipherSignature = cipherSignature;
    }

    //source : https://en.wikipedia.org/wiki/YouTube#Quality_and_formats
    private Container getContainer(int itag) {
        switch (itag) {
            case 13:
            case 17:
            case 36:
                return Container._3gp;
            case 18:
            case 22:
            case 37:
            case 38:
            case 82:
            case 83:
            case 84:
            case 85:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 160:
            case 264:
            case 266:
            case 298:
            case 299:
            case 139:
            case 140:
            case 141:
                return Container.mp4;
            case 43:
            case 44:
            case 45:
            case 46:
            case 100:
            case 101:
            case 102:
            case 242:
            case 243:
            case 244:
            case 247:
            case 248:
            case 271:
            case 272:
            case 302:
            case 303:
            case 171:
            case 172:
                return Container.webm;
            default:
                return Container.flv;
        }
    }

    private DashType getDashType(int itag) {
        switch (itag) {
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 160:
            case 264:
            case 266:
            case 298:
            case 299:
            case 242:
            case 243:
            case 244:
            case 247:
            case 248:
            case 271:
            case 272:
            case 302:
            case 303:
                return DashType.VIDEO;
            case 139:
            case 140:
            case 141:
            case 171:
            case 172:
                return DashType.AUDIO;
            default:
                return DashType.NONE;
        }
    }

    private AudioEncoding getAudioEncoding(int itag) {
        switch (itag) {
            case 5:
            case 6:
                return AudioEncoding.MP3;
            case 43:
            case 44:
            case 45:
            case 46:
            case 171:
            case 172:
                return AudioEncoding.Vorbis;
            default:
                return AudioEncoding.AAC;
        }
    }

    private int getAudioBitrate(int itag) {
        switch (itag) {
            case 17:
                return 24;
            case 36:
                return 38;
            case 139:
                return 48;
            case 5:
            case 6:
                return 64;
            case 18:
            case 82:
            case 83:
                return 96;
            case 34:
            case 35:
            case 43:
            case 44:
            case 140:
            case 171:
                return 128;
            case 84:
            case 85:
                return 152;
            case 141:
                return 256;
            default:
                return 192;
        }
    }

    private int getVideoResolution(int itag) throws ErrorDuringDownloadingException {
        switch (itag) {
            case 17:
            case 160:
                return 144;
            case 5:
            case 36:
            case 83:
            case 133:
            case 242:
                return 240;
            case 6:
                return 270;
            case 18:
            case 34:
            case 43:
            case 82:
            case 100:
            case 101:
            case 134:
            case 243:
                return 360;
            case 35:
            case 44:
            case 135:
            case 244:
                return 480;
            case 85:
                return 520;
            case 22:
            case 45:
            case 84:
            case 102:
            case 120:
            case 136:
            case 247:
            case 298:
            case 302:
                return 720;
            case 37:
            case 46:
            case 137:
            case 248:
            case 299:
            case 303:
                return 1080;
            case 264:
                return 1440;
            case 266:
                return 2160;
            case 38:
                return 3072;
            case 138: //138=original
                return 4320;
            default:
                throw new PluginImplementationException("Unknown video resolution for itag=" + itag);
        }
    }

    private int getFrameRate(int itag) {
        switch (itag) {
            case 298:
            case 299:
            case 302:
            case 303:
                return FrameRate._60.getFrameRate();
            default:
                return FrameRate._30.getFrameRate();
        }
    }

    public boolean isVid2AudSupported() {
        return ((container == Container.mp4 || container == Container.flv)
                && (audioEncoding == AudioEncoding.MP3 || audioEncoding == AudioEncoding.AAC));
    }

    public boolean isAudioExtractSupported() {
        return isVid2AudSupported();
    }

    public boolean isDashVideo() {
        return dashType == DashType.VIDEO;
    }

    public boolean isDashAudio() {
        return dashType == DashType.AUDIO;
    }

    public boolean isDash() {
        return dashType != DashType.NONE;
    }

    public int getItag() {
        return itag;
    }

    public Container getContainer() {
        return container;
    }

    public String getFileExt() {
        return fileExt;
    }

    public int getVideoQuality() {
        return videoQuality;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public AudioEncoding getAudioEncoding() {
        return audioEncoding;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public String getUrl() {
        return url;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isCipherSignature() {
        return cipherSignature;
    }

    @Override
    public String toString() {
        return "YouTubeMedia{" +
                "itag=" + itag +
                ", container=" + container +
                ", videoQuality=" + videoQuality +
                ", dashType=" + dashType +
                ", frameRate=" + frameRate +
                ", audioEncoding=" + audioEncoding +
                ", audioBitrate=" + audioBitrate +
                '}';
    }
}
