package kidstech.pngGeneration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;

import kidstech.quiltObjects.Block;
import kidstech.quiltObjects.Grid;

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

    public static void saveGrabAsPNG (Component myComponent, String outputFilePath) {
        try {
            BufferedImage image = generatePNG(myComponent);
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
        if (block == null) {
            System.err.println("Block is null");
            return;
        }
        
        try {
            int sideSize = block.getSideSize();
            int patchPixelSize = 250;
            int totalPixelDimension = sideSize * patchPixelSize;
            Dimension picSize = new Dimension(totalPixelDimension, totalPixelDimension);

            BlockViewer quiltViewer = new BlockViewer(block, patchPixelSize);
            GridViewPanel gridOverlay = new GridViewPanel(sideSize, patchPixelSize);
            
            int dynamicGridLines = quiltSize/2;
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
