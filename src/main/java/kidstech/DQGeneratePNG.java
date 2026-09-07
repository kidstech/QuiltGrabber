package kidstech;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DQGeneratePNG {
    public static BufferedImage generatePNG (Component myComponent) {
        // referenced from DigiQuilt blockSave code
        Dimension size = myComponent.getSize();
        BufferedImage myImage = 
            new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        myComponent.paint(g2);
        return myImage;
    }

    public static void saveAsPNG (Component myComponent, String outputFilePath) {
        try {
            BufferedImage image = generatePNG(myComponent);
            File outputFile = new File(outputFilePath);

            if (ImageIO.write(image, "png", outputFile)) {
                System.out.println("Quilt PNG successfully saved as: " + outputFilePath);
            } else {
                System.out.println("Failure to save PNG");
            }
        } catch (IOException e) {
            System.err.println("Failed to generate PNG file");
            e.printStackTrace();
        }
    }
}
