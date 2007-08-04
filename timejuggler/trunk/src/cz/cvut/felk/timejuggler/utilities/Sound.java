package cz.cvut.felk.timejuggler.utilities;

import application.ResourceManager;
import application.ResourceMap;
import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.MainApp;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Podpora prehravani zvuku. Zvuky s cachuji.
 * @author Vity
 */
public class Sound {

    private Sound() {
    }

    private static final Map<String, AudioClip> clipsMap = new HashMap<String, AudioClip>(1);

    private static void playSound(AudioClip clip) {
        if (clip != null)
            clip.play();
    }

    private static AudioClip getCachedAudioClip(final String fileName) {
        if (!clipsMap.containsKey(fileName)) {
            final AudioClip audioClip = getAudioClip(fileName);
            if (audioClip != null)
                clipsMap.put(fileName, audioClip);
            return audioClip;
        } else return clipsMap.get(fileName);
    }

    private static AudioClip getAudioClip(final String fileName) {
        final ResourceManager rm = MainApp.getAContext().getResourceManager();
        final ResourceMap resourceMap = rm.getResourceMap();
        String dir = resourceMap.getResourcesDir() + Consts.SOUNDS_DIR + "/" + fileName;
        final URL url = resourceMap.getClassLoader().getResource(dir);
        return Applet.newAudioClip(url);
    }

    public static AudioClip playSound(final String clip) {
        final AudioClip audioClip = getCachedAudioClip(clip);
        playSound(audioClip);
        return audioClip;
    }
}