package cz.gug.hackathon.glass.gimmeatone;

public class Tone {

    private int frequency;

    public Tone(int frequency) {
        this.frequency = frequency;
    }

    public void fillBuffer(byte[] buffer, int position) {
        for (int i = 0; i < buffer.length; i++) {
            double phase = 2 * Math.PI * (i + position) / (AudioPlayer.SAMPLE_RATE / frequency);
            short sample = (short) (Math.sin(phase) * Short.MAX_VALUE);
            buffer[2 * i] = (byte) (sample & 0x00ff);
            buffer[2 * i + 1] = (byte) ((sample & 0xff00) >>> 8);
        }
    }

}
