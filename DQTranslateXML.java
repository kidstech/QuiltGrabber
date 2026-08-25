import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class DQTranslateXML {
    private final int quiltSize;
    private final String studentName;
    private final String blockName;
    private final Map<String, Color> fabricPalette;

    public DQTranslateXML (int quiltSize, String studentName, String blockName) {
        this.quiltSize = quiltSize;
        this.studentName = studentName;
        this.blockName = blockName;

        this.fabricPalette = new HashMap<>();
        fabricPalette.put("PINK", new Color(220, 150, 160));
        fabricPalette.put("REDVIOLET", new Color(205, 80, 160));
        fabricPalette.put("RED", new Color(180, 65, 80));
        fabricPalette.put("ORANGE", new Color(230, 150, 100));
        fabricPalette.put("YELLOW", new Color(240, 215, 50));
        fabricPalette.put("GREEN", new Color(160, 187, 130));
        fabricPalette.put("DARKGREEN", new Color(79, 140, 105));
        fabricPalette.put("INDIGO", new Color(131, 175, 208));
        fabricPalette.put("BLUE", new Color(60, 100, 190));
        fabricPalette.put("VIOLET", new Color(117, 88, 154));
        fabricPalette.put("WHITE", new Color(246, 236, 235));
        fabricPalette.put("BLACK", new Color(65, 63, 68));
        fabricPalette.put("BROWN", new Color(120, 90, 70));
    }

    private String findClosestColor (Color imageColor) {

        String closestColor = "TRANSPARENT";
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, Color> entry: fabricPalette.entrySet()) {
            Color paletteColor = entry.getValue();
            double distance = Math.sqrt(
                Math.pow((imageColor.getRed() - paletteColor.getRed()),2) +
                Math.pow((imageColor.getGreen() - paletteColor.getGreen()),2) +
                Math.pow((imageColor.getBlue() - paletteColor.getBlue()),2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = entry.getKey();
            }
        }
        return closestColor;
    }

    private String centerOfTriangle (BufferedImage image, Point p1, Point p2, Point p3) {
        int centerX = (int) ((p1.x + p2.x + p3.x) / 3);
        int centerY = (int) ((p1.y + p2.y + p3.y) / 3);

        if (centerX < 0 || centerX < image.getWidth() || centerY < 0 || centerY < image.getHeight()) {
            return "TRANSPARENT";
        }

        Color triangleColor = new Color(image.getRGB(centerX,centerY));
        return findClosestColor(triangleColor);
    }
}
