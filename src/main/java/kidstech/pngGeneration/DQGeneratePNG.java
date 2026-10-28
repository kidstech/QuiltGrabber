package kidstech.pngGeneration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;

import kidstech.DQImagePanelAbstract;
import kidstech.quiltObjects.Block;
import kidstech.quiltObjects.Grid;

/**
 * Generate PNGs of both the QuiltGrab and the generated quilt from the image panel
 * 
 * @author Harley Hannahs, user: imbroglio-0
 *
 */

public class DQGeneratePNG {
    public static BufferedImage generatePNG (Component myComponent) {
        // referenced from DigiQuilt blockSave code
        Dimension size = myComponent.getSize();
        BufferedImage myImage = 
            new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();

        g2.setColor(Color.WHITE); 
        g2.fillRect(0, 0, size.width, size.height);

        myComponent.paint(g2);
        return myImage;
    }

    public static BufferedImage generateGrabPNG (DQImagePanelAbstract dqip) {
        BufferedImage image = generatePNG(dqip);
        // coords for cropping quilt grab
            int[][] corners = dqip.getCorners();

            // top left
            int topLeftX = corners[0][0];
            int topLeftY = corners[1][0];

            // top right
            int topRightX = corners[0][1];
            int topRightY = corners[1][1];

            // bottom right
            int botRightX = corners[0][2];
            int botRightY = corners[1][2];

            // bottom left
            int botLeftX = corners[0][3];
            int botLeftY = corners[1][3];

            // min and max values
            int minX = Math.min(topLeftX, botLeftX);
            int minY = Math.min(topLeftY, topRightY);
            int maxX = Math.max(topRightX, botRightX);
            int maxY = Math.max(botLeftY, botRightY);

            // width and height values
            int width = maxX - minX;
            int height = maxY - minY;

            int minDimension = Math.min(width, height);

            // crop image
            return image.getSubimage(minX, minY, minDimension, minDimension);
    }

    public static void saveGrabAsPNG (DQImagePanelAbstract dqip, String outputFilePath) {
        try {
            BufferedImage image = generateGrabPNG(dqip);
            File outputFile = new File(outputFilePath);

            if (ImageIO.write(image, "png", outputFile)) {
                System.out.println("Quilt Grab PNG successfully saved as: " + outputFilePath);
            } else {
                System.out.println("Failure to save Quilt Grab PNG");
            }
        } catch (IOException e) {
            System.err.println("Failed to generate Quilt Grab PNG file");
            e.printStackTrace();
        }
    }

    public static void saveQuiltAsPNG (Block block, int quiltSize, String outputFilePath) {
        try { 
            BufferedImage image = generateQuiltPNG(block, quiltSize);

            File outputFile = new File(outputFilePath);

            if (ImageIO.write(image, "png", outputFile)) {
                System.out.println("Quilt PNG successfully saved as: " + outputFilePath);
            } else {
                System.out.println("Failure to save Quilt PNG");
            }
        } catch (IOException e) {
            System.err.println("Failed to generate Quilt PNG file");
            e.printStackTrace();
        }
    }

    public static BufferedImage generateQuiltPNG (Block block, int quiltSize) {
        if (block == null) {
            System.err.println("Block is null");
        } 
        int sideSize = block.getSideSize();
        int patchPixelSize = 200;
        int totalPixelDimension = sideSize * patchPixelSize;
        Dimension picSize = new Dimension(totalPixelDimension, totalPixelDimension);

        BlockViewer quiltViewer = new BlockViewer(block, patchPixelSize);
        GridViewPanel gridOverlay = new GridViewPanel(sideSize, patchPixelSize);
        
        int dynamicGridLines = quiltSize;
        Grid dynamicGrid = new Grid(dynamicGridLines, dynamicGridLines, 0, 0);
        gridOverlay.setGrid(dynamicGrid);

        quiltViewer.setBounds(0, 0, totalPixelDimension, totalPixelDimension);
        gridOverlay.setBounds(0, 0, totalPixelDimension, totalPixelDimension);

        if (dynamicGrid != null) {
            gridOverlay.setGrid(dynamicGrid);
        }

        JLayeredPane pic = new JLayeredPane();
        pic.setPreferredSize(picSize);

        pic.add(quiltViewer, JLayeredPane.DEFAULT_LAYER);
        pic.add(gridOverlay, JLayeredPane.PALETTE_LAYER);

        pic.setSize(picSize);
        pic.doLayout();
        subComponentLayout(pic);

        BufferedImage image = generatePNG(pic);

        return image;
    }

    // forces layout to be larger than 0 pixels even though image generating isn't being done on screen
    public static void subComponentLayout (Component component) {
        if (component instanceof Container) {
            Container container = (Container) component;
            container.doLayout();
            for (Component child : container.getComponents()) {
                subComponentLayout(child);
            }
        }
    }
}
