package cz.gug.hackathon.glass.gimmeatone;


public class Tone {

    private int frequency;
    private short[] waveBuffer;

    public Tone(int frequency) {
        this.frequency = frequency;
        buildWaveBuffer();
    }

    private void buildWaveBuffer() {
        waveBuffer = new short[AudioPlayer.SAMPLE_RATE / frequency];
        for (int i = 0; i < waveBuffer.length; i++) {
            double phase = 2.0D * Math.PI * i * (double) frequency / AudioPlayer.SAMPLE_RATE;
            waveBuffer[i] = (short) (Math.sin(phase) * (Short.MAX_VALUE - 1));
        }
    }

    public Playback createPlayback() {
        return new TonePlayback();
    }

    private class TonePlayback implements Playback {
        private int position;
        @Override
        public void fillBuffer(short[] buffer) {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = waveBuffer[(i + position) % waveBuffer.length];
            }
            position += (position + buffer.length) % waveBuffer.length;
        }
    }

}
