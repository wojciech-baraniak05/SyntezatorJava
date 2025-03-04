package OkienkaIInne;

import OkienkaIInne.Mixer.MixerGUI;
import OkienkaIInne.Mixer.MixerLogic;

public class Main {
    public static void main(String[] args) {
        MixerLogic mixerLogic = new MixerLogic();
        new MixerGUI(mixerLogic);
    }
}
