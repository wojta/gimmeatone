package cz.gug.hackathon.glass.gimmeatone.audio;

/**
 * Simple enveloped source with the support for ATTACK and RELEASE phases.
 */
public class EnvelopedSource<T extends AudioSource> implements AudioSource {

    private static final double ATTACK_STEP = 1.0 / AudioPlayer.SAMPLE_RATE / 0.005;
    private static final double RELEASE_STEP = 1.0 / AudioPlayer.SAMPLE_RATE / 0.01;

    private final T source;

    private volatile int phase = 1;
    private double factor = 0;

    public EnvelopedSource(T source) {
        this.source = source;
    }

    public T getSource() {
        return source;
    }

    @Override
    public int fillBuffer(short[] buffer, long time) {
        int sampleCount = source.fillBuffer(buffer, time);
        if (phase > 0) {
            for (int i = 0; i < sampleCount; i++) {
                if ((factor += ATTACK_STEP) >= 1) {
                    phase--;
                    break;
                }
                buffer[i] = (short) (buffer[i] * factor);
            }
        }
        if (phase < 0) {
            for (int i = 0; i < sampleCount; i++) {
                if ((factor -= RELEASE_STEP) <= 0) {
                    return i;
                }
                buffer[i] = (short) (buffer[i] * factor);
            }
        }
        return sampleCount;
    }

    public void stop() {
        phase = -1;
    }

}
