package OkienkaIInne.Mixer;

import OkienkaIInne.INNE.Plik2Sample;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateStereoReader;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MixerLogic {
    private List<FloatSample> samples;
    private List<Float> sampleRates;
    private Synthesizer synth;
    private LineOut lineOut;
    private VariableRateStereoReader samplePlayer;

    public MixerLogic() {
        samples = new ArrayList<>();
        sampleRates = new ArrayList<>();
    }

    public List<String> loadFilesFromDirectory() {
        List<String> fileNames = new ArrayList<>();
        File samplesDir = new File("samples");
        if (samplesDir.exists() && samplesDir.isDirectory()) {
            File[] files = samplesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
            if (files != null) {
                for (File file : files) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    public boolean loadSample(String fileName) {
        File file = new File("samples/" + fileName);
        try {
            FloatSample sample = Plik2Sample.loadFloatSample(file);
            samples.add(sample);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            sampleRates.add(audioStream.getFormat().getSampleRate());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean playSample(int index) {
        if (index < 0 || index >= samples.size()) {
            return false;
        }

        FloatSample sample = samples.get(index);
        float sampleRate = sampleRates.get(index);
        try {
            synth = JSyn.createSynthesizer();
            lineOut = new LineOut();
            samplePlayer = new VariableRateStereoReader();

            synth.add(samplePlayer);
            synth.add(lineOut);

            samplePlayer.rate.set(sampleRate);
            samplePlayer.dataQueue.queue(sample, 0, sample.getNumFrames());

            samplePlayer.output.connect(0, lineOut.input, 0);
            samplePlayer.output.connect(1, lineOut.input, 1);

            synth.start();
            lineOut.start();

            double durationInSeconds = (double) sample.getNumFrames() / sampleRate;
            Thread.sleep((long) (durationInSeconds * 1000));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (synth != null) {
                synth.stop();
            }
        }
    }
}
