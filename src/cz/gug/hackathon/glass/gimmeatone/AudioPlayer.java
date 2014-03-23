package cz.gug.hackathon.glass.gimmeatone;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayer {

    public static final int SAMPLE_RATE = 44100;
    public static final int OUTPUT_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // XXX Tohle je jen docasne... bude to chtit predelat na STREAMING a snizit na 10ms
    public static final int BUFFER_SIZE = SAMPLE_RATE;

    public AudioTrack createTrack() {
        return new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                OUTPUT_CHANNELS, AUDIO_FORMAT,
                44100, AudioTrack.MODE_STATIC);
    }

}
