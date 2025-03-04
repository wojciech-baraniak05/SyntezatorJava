package OkienkaIInne.Syntezator;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;

import javax.swing.*;
import java.awt.*;

public class WaveSynthesizer {
    private Synthesizer synthesizer;
    private LineOut lineOut;
    private ControlPanel controlPanel;
    private VisualizationPanel visualizationPanel;

    public WaveSynthesizer() {
        setupSynth();
        createAndShowGUI();
    }

    private void setupSynth() {
        synthesizer = JSyn.createSynthesizer();
        lineOut = new LineOut();
        synthesizer.add(lineOut);
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Wave Synthesizer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only closes this window
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        controlPanel = new ControlPanel(synthesizer, lineOut);
        visualizationPanel = new VisualizationPanel(controlPanel.getOscillatorListModel());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, controlPanel.getPanel(), visualizationPanel
        );
        splitPane.setResizeWeight(0.67);
        splitPane.setDividerLocation(400);

        frame.add(splitPane, BorderLayout.CENTER);

        // Start the synthesizer and line out
        synthesizer.start();
        lineOut.start();

        // Timer for periodic UI updates
        Timer timer = new Timer(16, e -> visualizationPanel.repaint());
        timer.start();

        // Add a listener to clean up resources when the window is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cleanup();
            }
        });

        frame.setVisible(true);
    }

    private void cleanup() {
        // Stop the synthesizer and free resources
        if (synthesizer != null) {
            synthesizer.stop();
        }
    }
}
