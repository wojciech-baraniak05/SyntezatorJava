package OkienkaIInne.Syntezator;

import OkienkaIInne.INNE.SynthColors;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;
import com.jsyn.util.WaveRecorder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class ControlPanel {
    private JPanel panel;
    private DefaultListModel<UnitOscillator> oscillatorListModel;
    private JList<UnitOscillator> oscillatorList;
    private UnitOscillator selectedOscillator;
    private JPanel oscControlPanel;
    private Synthesizer synthesizer;
    private LineOut lineOut;
    private double lastAmplitude = 0.25;
    public WaveRecorder recorder;
    private File probka;
    private File dir;
    private int sampleCount;


    public ControlPanel(Synthesizer synthesizer, LineOut lineOut) {
        this.synthesizer = synthesizer;
        this.lineOut = lineOut;
        dir = new File("samples");
        if (dir.exists()){
            dir.mkdir();
        }
        sampleCount = dir.list().length + 1;


        probka = new File(dir, "sample-" + sampleCount + ".wav");
        try {
            recorder = new WaveRecorder(synthesizer, probka);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WaveRecorder");
        }

        panel = new JPanel(new BorderLayout());
        oscillatorListModel = new DefaultListModel<>();
        oscillatorList = new JList<>(oscillatorListModel);

        oscillatorList.setBackground(SynthColors.BACKGROUND.getColor());
        oscillatorList.setForeground(SynthColors.SECONDARY.getColor());
        oscillatorList.addListSelectionListener(e -> updateControlPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        String[] waveTypes = {"Sine", "Square", "Sawtooth", "Triangle"};
        for (String waveType : waveTypes) {
            JButton addButton = new JButton("Add " + waveType);
            addButton.addActionListener(e -> addWave(waveType));
            buttonPanel.add(addButton);
        }

        JButton startStopButton = new JButton("Start/Stop");
        startStopButton.addActionListener(e -> toggleSynthesizer());
        buttonPanel.add(startStopButton);

        JButton recordButton = new JButton("Record");
        recordButton.addActionListener(e -> record());
        buttonPanel.add(recordButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        oscControlPanel = createOscillatorControlPanel();
        panel.add(oscControlPanel, BorderLayout.CENTER);

        panel.add(new JScrollPane(oscillatorList), BorderLayout.EAST);
    }

    public void record() {
        recorder.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        recorder.stop();

    }

    public JPanel getPanel() {
        return panel;
    }

    public DefaultListModel<UnitOscillator> getOscillatorListModel() {
        return oscillatorListModel;
    }


    private JPanel createOscillatorControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(SynthColors.CONTROL_PANEL.getColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel freqLabel = new JLabel("Frequency (Hz):");
        freqLabel.setForeground(SynthColors.SECONDARY.getColor());
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(freqLabel, gbc);

        JSlider freqSlider = new JSlider(20, 4000, 440);
        freqSlider.setBackground(SynthColors.CONTROL_PANEL.getColor());
        freqSlider.setPaintTicks(true);
        freqSlider.setPaintLabels(true);
        freqSlider.setMajorTickSpacing(500);
        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(freqSlider, gbc);

        JTextField freqField = new JTextField(6);
        freqField.setText(String.valueOf(freqSlider.getValue()));
        gbc.gridx = 2;
        gbc.gridy = 0;
        controlPanel.add(freqField, gbc);

        freqSlider.addChangeListener(e -> {
            int value = freqSlider.getValue();
            freqField.setText(String.valueOf(value));
            if (selectedOscillator != null) {
                selectedOscillator.frequency.set(value);
            }
        });

        freqField.addActionListener(e -> {
            try {
                int value = Integer.parseInt(freqField.getText());
                if (value >= 20 && value <= 4000) {
                    freqSlider.setValue(value);
                    if (selectedOscillator != null) {
                        selectedOscillator.frequency.set(value);
                    }
                } else {
                    freqField.setText(String.valueOf(freqSlider.getValue()));
                }
            } catch (NumberFormatException ex) {
                freqField.setText(String.valueOf(freqSlider.getValue()));
            }
        });

        JLabel ampLabel = new JLabel("Amplitude:");
        ampLabel.setForeground(SynthColors.SECONDARY.getColor());
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(ampLabel, gbc);

        JSlider ampSlider = new JSlider(0, 100, 25);
        ampSlider.setBackground(SynthColors.CONTROL_PANEL.getColor());
        ampSlider.setPaintTicks(true);
        ampSlider.setPaintLabels(true);
        Hashtable<Integer, JLabel> ampLabels = new Hashtable<>();
        ampLabels.put(0, new JLabel("0"));
        ampLabels.put(100, new JLabel("1"));
        ampSlider.setLabelTable(ampLabels);
        gbc.gridx = 1;
        gbc.gridy = 1;
        controlPanel.add(ampSlider, gbc);

        JTextField ampField = new JTextField(6);
        ampField.setText(String.valueOf(ampSlider.getValue() / 100.0));
        gbc.gridx = 2;
        gbc.gridy = 1;
        controlPanel.add(ampField, gbc);

        ampSlider.addChangeListener(e -> {
            double value = ampSlider.getValue() / 100.0;
            ampField.setText(String.valueOf(value));
            if (selectedOscillator != null) {
                selectedOscillator.amplitude.set(value);
            }
        });

        ampField.addActionListener(e -> {
            try {
                double value = Double.parseDouble(ampField.getText());
                if (value >= 0 && value <= 1) {
                    int sliderValue = (int) (value * 100);
                    ampSlider.setValue(sliderValue);
                    if (selectedOscillator != null) {
                        selectedOscillator.amplitude.set(value);
                    }
                } else {
                    ampField.setText(String.valueOf(ampSlider.getValue() / 100.0));
                }
            } catch (NumberFormatException ex) {
                ampField.setText(String.valueOf(ampSlider.getValue() / 100.0));
            }
        });

        JButton deleteButton = new JButton("Delete Wave");
        deleteButton.addActionListener(e -> deleteSelectedOscillator());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(deleteButton, gbc);

        JButton muteButton = new JButton("Mute");
        muteButton.addActionListener(e -> toggleMute(muteButton));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(muteButton, gbc);

        return controlPanel;
    }

    private void addWave(String waveType) {
        UnitOscillator newOscillator = switch (waveType) {
            case "Square" -> new SquareOscillator();
            case "Sawtooth" -> new SawtoothOscillator();
            case "Triangle" -> new TriangleOscillator();
            default -> new SineOscillator();
        };

        newOscillator.amplitude.set(0.25);
        synthesizer.add(newOscillator);
        newOscillator.output.connect(0, recorder.getInput(), 0);
        newOscillator.output.connect(0, recorder.getInput(), 1);
        newOscillator.output.connect(0, lineOut.input, 0);
        newOscillator.output.connect(0, lineOut.input, 1);

        oscillatorListModel.addElement(newOscillator);
    }

    private void toggleSynthesizer() {
        if (synthesizer.isRunning()) {
            synthesizer.stop();
            lineOut.stop();
        } else {
            synthesizer.start();
            lineOut.start();
        }
    }

    private void deleteSelectedOscillator() {
        if (selectedOscillator != null) {
            selectedOscillator.output.disconnectAll();
            synthesizer.remove(selectedOscillator);
            oscillatorListModel.removeElement(selectedOscillator);
            selectedOscillator = null;
            updateControlPanel();
        }
    }

    private void toggleMute(JButton muteButton) {
        if (selectedOscillator != null) {
            if (selectedOscillator.amplitude.get() > 0) {
                lastAmplitude = selectedOscillator.amplitude.get();
                selectedOscillator.amplitude.set(0);
                muteButton.setText("Unmute");
            } else {
                selectedOscillator.amplitude.set(lastAmplitude);
                muteButton.setText("Mute");
            }
        }
    }

    private void updateControlPanel() {
        selectedOscillator = oscillatorList.getSelectedValue();
        oscControlPanel.setVisible(selectedOscillator != null);
    }
}
