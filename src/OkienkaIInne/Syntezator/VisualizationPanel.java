package OkienkaIInne.Syntezator;

import OkienkaIInne.INNE.SynthColors;
import com.jsyn.unitgen.*;

import javax.swing.*;
import java.awt.*;

public class VisualizationPanel extends JPanel {
    private final DefaultListModel<UnitOscillator> oscillatorListModel;

    public VisualizationPanel(DefaultListModel<UnitOscillator> oscillatorListModel) {
        this.oscillatorListModel = oscillatorListModel;
        setPreferredSize(new Dimension(900, 200));
        setBackground(SynthColors.VISUALIZATION_BACKGROUND.getColor());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(SynthColors.VISUALIZATION_BACKGROUND.getColor());
        g.setColor(SynthColors.VISUALIZATION_LINE.getColor());

        int width = getWidth();
        int height = getHeight();
        int midY = height / 2;

        int prevX = 0, prevY = midY;

        for (int x = 0; x < width; x++) {
            double combinedWaveValue = 0;

            for (int i = 0; i < oscillatorListModel.size(); i++) {
                UnitOscillator osc = oscillatorListModel.get(i);
                double frequency = osc.frequency.get();
                double amplitude = osc.amplitude.get();
                double time = (double) x / (width * 10);

                if (osc instanceof SineOscillator) {
                    combinedWaveValue += amplitude * Math.sin(2 * Math.PI * frequency * time);
                } else if (osc instanceof SquareOscillator) {
                    combinedWaveValue += amplitude * (Math.sin(2 * Math.PI * frequency * time) >= 0 ? 1 : -1);
                } else if (osc instanceof SawtoothOscillator) {
                    combinedWaveValue += amplitude * (2 * (time * frequency - Math.floor(0.5 + time * frequency)));
                } else if (osc instanceof TriangleOscillator) {
                    combinedWaveValue += amplitude * (2 * Math.abs(2 * (time * frequency - Math.floor(0.5 + time * frequency))) - 1);
                }
            }

            int y = (int) (midY - combinedWaveValue * midY);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }
}
