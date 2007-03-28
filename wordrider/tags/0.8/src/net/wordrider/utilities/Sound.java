package net.wordrider.utilities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
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
        final URL url = ((URLClassLoader) Swinger.class.getClassLoader()).findResource(Consts.SOUNDSDIR + fileName);
        return Applet.newAudioClip(url);
    }

    public static void playSound(final String clip) {
        playSound(getCachedAudioClip(clip));
    }
}
