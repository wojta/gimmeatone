package cz.gug.hackathon.glass.gimmeatone.audio;

/**
 * Sound source.
 */
public interface AudioSource {

    /**
     * Fill the provided sample buffer.
     * @return Length of sample data filled (if lower than the buffer length, playback is considered finished).
     */
    int fillBuffer(short[] buffer, long time);

}
