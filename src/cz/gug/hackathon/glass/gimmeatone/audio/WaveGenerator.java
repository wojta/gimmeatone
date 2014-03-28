package cz.gug.hackathon.glass.gimmeatone.audio;

/**
 * Mutable tone generator.
 */
public class WaveGenerator implements AudioSource {

    private static final short[] MASTER_WAVE = new short[1000];
    private double position;
    private double step;

    static {
        for (int i = 0; i < MASTER_WAVE.length; i++) {
            MASTER_WAVE[i] = (short) (Math.sin(2.0 * Math.PI * i / MASTER_WAVE.length) * (Short.MAX_VALUE - 1));
        }
    }

    public WaveGenerator(double frequency) {
        changeFrequency(frequency);
    }

    public void changeFrequency(double frequency) {
        step = frequency * MASTER_WAVE.length / AudioPlayer.SAMPLE_RATE;
    }

    @Override
    public int fillBuffer(short[] buffer, long time) {
        for (int i = 0; i < buffer.length; i++) {
            // Get integer and fraction parts of the current position
            int integer = (int) position;
            double fraction = position - integer;
            // Get interpolated value
            short previous = MASTER_WAVE[integer];
            short following = MASTER_WAVE[(integer + 1) % MASTER_WAVE.length];
            buffer[i] = (short) (((1.0 - fraction) * previous + fraction * following) / 2);
            // Advance one step
            if ((position += step) >= MASTER_WAVE.length) {
                position -= MASTER_WAVE.length;
            }
        }
        return buffer.length;
    }

}

