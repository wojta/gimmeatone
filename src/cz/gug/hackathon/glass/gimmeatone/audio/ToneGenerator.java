package cz.gug.hackathon.glass.gimmeatone.audio;


/**
 * Simple SIN tone generator.
 */
public class ToneGenerator implements AudioSource {

    private short[] waveBuffer;

    public ToneGenerator(int frequency) {
        waveBuffer = new short[findOptimalBufferSize(frequency)];
        for (int i = 0; i < waveBuffer.length; i++) {
            double phase = 2.0 * Math.PI * i * frequency / (double) AudioPlayer.SAMPLE_RATE;
            waveBuffer[i] = (short) (Math.sin(phase) * (Short.MAX_VALUE - 1));
        }
    }

    /**
     * Find optimal buffer size in {@link AudioPlayer.SAMPLE_RATE} for the wave of the given frequency.
     */
    private int findOptimalBufferSize(int frequency) {
        double waveOverflow = 1.0 * AudioPlayer.SAMPLE_RATE / frequency;
        double minimumOverflow = waveOverflow;
        int minimumRepeat = 1;
        for (int i = 1; i < 30; i++) {
            double overflow = i * waveOverflow - (int) (i * waveOverflow);
            if (overflow < minimumOverflow) {
                minimumOverflow = overflow;
                minimumRepeat = i;
            }
        }
        System.out.println("FREQUENCY: " + frequency + ", REPEAT: " + minimumRepeat + ", OVERFLOW: " + minimumOverflow);
        return minimumRepeat * AudioPlayer.SAMPLE_RATE / frequency;
    }

    @Override
    public int fillBuffer(short[] buffer, long time) {
        int position = (int) (time % waveBuffer.length);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = waveBuffer[(i + position) % waveBuffer.length];
        }
        return buffer.length;
    }

}
