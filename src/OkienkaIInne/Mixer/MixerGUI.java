package OkienkaIInne.Mixer;

import OkienkaIInne.Syntezator.WaveSynthesizer;

import javax.swing.*;
import java.awt.*;

public class MixerGUI {
    private JFrame frame;
    private JList<String> availableFilesList;
    private JList<String> loadedSamplesList;
    private DefaultListModel<String> availableFilesModel;
    private DefaultListModel<String> loadedSamplesModel;
    private MixerLogic mixerLogic;

    public MixerGUI(MixerLogic mixerLogic) {
        this.mixerLogic = mixerLogic;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Okno Mixer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        availableFilesModel = new DefaultListModel<>();
        loadedSamplesModel = new DefaultListModel<>();
        availableFilesList = new JList<>(availableFilesModel);
        loadedSamplesList = new JList<>(loadedSamplesModel);

        availableFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadedSamplesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane availableScrollPane = new JScrollPane(availableFilesList);
        availableScrollPane.setPreferredSize(new Dimension(300, 200));
        JScrollPane loadedScrollPane = new JScrollPane(loadedSamplesList);
        loadedScrollPane.setPreferredSize(new Dimension(300, 200));

        JButton loadFilesButton = new JButton("Load Files");
        loadFilesButton.addActionListener(e -> loadFiles());

        JButton loadSampleButton = new JButton("Load Sample");
        loadSampleButton.addActionListener(e -> loadSample());

        JButton playSampleButton = new JButton("Play Sample");
        playSampleButton.addActionListener(e -> playSample());

        JButton openSynthButton = new JButton("Open Synthesizer");
        openSynthButton.addActionListener(e -> openSynthesizer());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loadFilesButton);
        buttonPanel.add(loadSampleButton);
        buttonPanel.add(playSampleButton);
        buttonPanel.add(openSynthButton);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(availableScrollPane);
        mainPanel.add(loadedScrollPane);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mainPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadFiles() {
        availableFilesModel.clear();
        for (String fileName : mixerLogic.loadFilesFromDirectory()) {
            availableFilesModel.addElement(fileName);
        }
    }

    private void loadSample() {
        int selectedIndex = availableFilesList.getSelectedIndex();
        if (selectedIndex != -1) {
            String fileName = availableFilesModel.get(selectedIndex);
            if (mixerLogic.loadSample(fileName)) {
                loadedSamplesModel.addElement(fileName);
            }
        }
    }

    private void playSample() {
        int selectedIndex = loadedSamplesList.getSelectedIndex();
        if (selectedIndex != -1) {
            mixerLogic.playSample(selectedIndex);
        }
    }

    private void openSynthesizer() {
        SwingUtilities.invokeLater(() -> new WaveSynthesizer());
    }
}
