package OkienkaIInne.INNE;

import java.awt.*;

public enum SynthColors {
    PRIMARY(new Color(72, 77, 127)), // Dark blue for primary UI elements
    SECONDARY(new Color(240, 240, 240)), // Brighter white for text and accents
    BACKGROUND(Color.BLACK), // Background remains black
    CONTROL_PANEL(new Color(46, 46, 46)), // Dark gray for control panel background
    VISUALIZATION_LINE(Color.GREEN), // Green for visualization lines
    MUTED(Color.GRAY), // Gray for muted elements
    VISUALIZATION_BACKGROUND(Color.BLACK); // Black for visualization background

    private final Color color;

    SynthColors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
