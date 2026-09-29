package kidstech;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import resources.Jama.Matrix;

public class DQTranslateXML {
    private final int quiltSize;
    private final String studentName;
    private final String blockName;
    private final Map<String, Color> fabricPalette;

    public DQTranslateXML (int quiltSize, String studentName, String blockName) {
        this.quiltSize = quiltSize;
        this.studentName = studentName;
        this.blockName = blockName;

        // fabric palette that corresponds to DigiQuilt Palette
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

        // Color distance calculation similar to dynamic closest color warping technique
        // Check distance for each color and see which DigiQuilt color is closest to the observed color
        for (Map.Entry<String, Color> entry: fabricPalette.entrySet()) {
            Color paletteColor = entry.getValue();
            double distance = Math.sqrt(
                Math.pow((imageColor.getRed() - 10 - paletteColor.getRed()),2) +
                Math.pow((imageColor.getGreen() - 10 - paletteColor.getGreen()),2) +
                Math.pow((imageColor.getBlue() - 10 - paletteColor.getBlue()),2)
            );
            // reassigning closest color if a new smallest distance is found
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = entry.getKey();
            }
        }
        return closestColor;
    }

    // finding the center of a triangle in the grid to sample it's color
    // currently only one pixel
    private String centerOfTriangle (BufferedImage image, Point p1, Point p2, Point p3) {
        int centerX = (int) ((p1.x + p2.x + p3.x) / 3);
        int centerY = (int) ((p1.y + p2.y + p3.y) / 3);

        int patchWidth = 3;
        int patchHeight = 3;

        if (centerX < 0 || centerX + patchWidth >= image.getWidth() || centerY < 0 || centerY + patchHeight >= image.getHeight()) {
            return "TRANSPARENT";
        }

        int[] pixels = new int[patchWidth * patchHeight];

        image.getRGB(centerX,centerY, patchWidth, patchHeight, pixels, 0, patchWidth);

        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;

        for (int pixel : pixels) {
            Color color = new Color(pixel);
            sumRed += color.getRed();
            sumGreen += color.getGreen();
            sumBlue += color.getBlue();
        }

        int totalPixels = pixels.length;
        Color averageColor = new Color(
            sumRed/totalPixels,
            sumGreen/totalPixels,
            sumBlue/totalPixels
        );

        return findClosestColor(averageColor);
    }

    //building the actual xml file for the quilt
    public String buildQuiltXML (BufferedImage image, int[][] corners, Homography h) {
        Matrix picPoints = new Matrix(4,2);
        Matrix quiltPoints = new Matrix (4,2);
        // initializing picture points as done in DQImagePanel

        // top left
        picPoints.set(0,0,corners[0][0]);
        picPoints.set(0,1,corners[1][0]);

        // top right
        picPoints.set(1,0,corners[0][1]);
        picPoints.set(1,1,corners[1][1]);

        // bottom right
        picPoints.set(2,0,corners[0][2]);
        picPoints.set(2,1,corners[1][2]);

        // bottom left
        picPoints.set(3,0,corners[0][3]);
        picPoints.set(3,1,corners[1][3]);

        // initializing quilt points as done in DQImagePanel

         // top left
        quiltPoints.set(0,0,0);
        quiltPoints.set(0,1,quiltSize);

        // top right
        quiltPoints.set(1,0,quiltSize);
        quiltPoints.set(1,1,quiltSize);

        // bottom right
        quiltPoints.set(2,0,quiltSize);
        quiltPoints.set(2,1,0);

        // bottom left (origin)
        quiltPoints.set(3,0,0);
        quiltPoints.set(3,1,0);

        h.findHomography(picPoints, quiltPoints);

        StringBuilder buildPatches = new StringBuilder();

        // these loops make steps by two, assuring that we are assigning a patch as
        // a block of 16 triangles, like the xml is generated in DigiQuilt terms.
        // For a 2x2 patch quilt, there are 4 blocks in each patch, so we must take 
        // steps of 2 to properly represent the quilt.

        for (int i = quiltSize - 2; i >= 0; i-=2) {
            for (int j = 0; j < quiltSize; j+=2) {
                buildPatches.append("        <Patch>\n");
                // one patch per 4 blocks

                // rows and columns WITHIN the patch itself
                for (int row = 1; row >= 0; row--) {
                    for (int col = 0; col < 2; col++) {
                
                        // internal patch rows & columns
                        int targetRow = i + row;
                        int targetCol = j + col;

                        // initializing where the points are (similar to what is done in DQImagePanel)
                        double[] topLeftCoords = h.reverseTranslatePoint(targetCol, targetRow + 1);
                        double[] topRightCoords = h.reverseTranslatePoint(targetCol + 1, targetRow + 1);
                        double[] bottomRightCoords = h.reverseTranslatePoint(targetCol + 1, targetRow);
                        double[] bottomLeftCoords = h.reverseTranslatePoint(targetCol, targetRow);
                        // added a central point to make identifying triangles easier
                        double[] centerCoords = h.reverseTranslatePoint(targetCol + 0.5, targetRow + 0.5);

                        Point topLeft = new Point(topLeftCoords[0], topLeftCoords[1]);
                        Point topRight = new Point(topRightCoords[0], topRightCoords[1]);
                        Point bottomRight = new Point(bottomRightCoords[0], bottomRightCoords[1]);
                        Point bottomLeft = new Point(bottomLeftCoords[0], bottomLeftCoords[1]);
                        Point center = new Point(centerCoords[0], centerCoords[1]);

                        // locating the triangles within each block at their proper numbering position
                        String triangle1 = centerOfTriangle(image, topLeft, topRight, center);
                        String triangle2 = centerOfTriangle(image, topLeft, bottomLeft, center);
                        String triangle3 = centerOfTriangle(image, topRight, bottomRight, center);
                        String triangle4 = centerOfTriangle(image, bottomLeft, bottomRight, center);

                        buildPatches.append(String.format("            <Fabric>%s</Fabric>\n", triangle1));
                        buildPatches.append(String.format("            <Fabric>%s</Fabric>\n", triangle2));
                        buildPatches.append(String.format("            <Fabric>%s</Fabric>\n", triangle3));
                        buildPatches.append(String.format("            <Fabric>%s</Fabric>\n", triangle4));
                    }
                }
            buildPatches.append("        </Patch>\n");
        }
    }
        return writeXML(buildPatches.toString());
    }
       private String writeXML(String patchesContent) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<DigiQuiltSave>\n" +
                "    <Student>" + studentName + "</Student>\n" +
                "    <BlockName>" + blockName + "</BlockName>\n" +
                "    <Timestamp>" + System.currentTimeMillis() + "</Timestamp>\n" +
                "    <Notes>Translated programmatically via Java Translation Engine</Notes>\n" +
                "    <Challenge>(no challenge)</Challenge>\n" +
                "    <Grid>\n" +
                "        <Line><x1>0.0</x1><y1>0.0</y1><x2>0.0</x2><y2>1.0</y2></Line>\n" +
                "        <Line><x1>0.0</x1><y1>0.0</y1><x2>1.0</x2><y2>0.0</y2></Line>\n" +
                "        <Line><x1>0.0</x1><y1>1.0</y1><x2>1.0</x2><y2>1.0</y2></Line>\n" +
                "        <Line><x1>1.0</x1><y1>0.0</y1><x2>1.0</x2><y2>1.0</y2></Line>\n" +
                "    </Grid>\n" +
                "    <Block size=\"" + quiltSize + "\">\n" +
                patchesContent +
                "    </Block>\n" +
                "    <History>\n" +
                "        <Undos/>\n" +
                "    </History>\n" +
                "</DigiQuiltSave>";
    }
    private static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }
}
