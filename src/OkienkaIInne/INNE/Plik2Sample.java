package OkienkaIInne.INNE;

import javax.sound.sampled.*;
import com.jsyn.data.FloatSample;
import java.io.File;

public class Plik2Sample {
    public static FloatSample loadFloatSample(File wavFile) throws Exception {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format = audioStream.getFormat();
        if (format.getChannels() != 2) throw new UnsupportedAudioFileException("Stereo audio required.");
        byte[] buffer = audioStream.readAllBytes();
        int numFrames = buffer.length / 4;
        float[] interleavedData = new float[numFrames * 2];
        for (int i = 0; i < numFrames; i++) {
            int idx = i * 4;
            int left = (buffer[idx + 1] << 8) | (buffer[idx] & 0xFF);
            int right = (buffer[idx + 3] << 8) | (buffer[idx + 2] & 0xFF);
            interleavedData[i * 2] = left / 32768.0f;
            interleavedData[i * 2 + 1] = right / 32768.0f;
        }
        return new FloatSample(interleavedData, 2);
    }
}
