package kidstech.pngGeneration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;

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

    static {
        nu.pattern.OpenCV.loadLocally();
    }

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

            // setting up corner points array
            List<Point> cornerPoints = new ArrayList<>();

            // top left
            cornerPoints.add(new Point(corners[0][0], corners[1][0]));
            // top right
            cornerPoints.add(new Point(corners[0][1], corners[1][1]));
            // bottom right
            cornerPoints.add(new Point(corners[0][2], corners[1][2]));
            // bottom left
            cornerPoints.add(new Point(corners[0][3], corners[1][3]));

            // converting image to a mat
            BufferedImage byteImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            java.awt.Graphics2D g = byteImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            byte[] pixels = ((DataBufferByte) byteImage.getRaster().getDataBuffer()).getData();
            Mat imageMat = new Mat(byteImage.getHeight(), byteImage.getWidth(), CvType.CV_8UC3);
            imageMat.put(0, 0, pixels);

            // setting up frame points array
            List<Point> framePoints = new ArrayList<>();
            framePoints.add(new Point(0, 0));
            framePoints.add(new Point(500, 0));
            framePoints.add(new Point(500, 500));
            framePoints.add(new Point(0, 500));

            // converting array lists to mat
            Mat cornerPointsMat = Converters.vector_Point2f_to_Mat(cornerPoints);
            Mat framePointsMat = Converters.vector_Point2f_to_Mat(framePoints);

            // actual perspective warping action
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(cornerPointsMat, framePointsMat);
            Mat outputMat = new Mat();
            Size frameSize = new Size(500, 500);
            Imgproc.warpPerspective(imageMat, outputMat, perspectiveTransform, frameSize);

            // converting the mat back to an image
            int cols = outputMat.cols();
            int rows = outputMat.rows();
            int elemSize = (int)outputMat.elemSize();
            byte[] data = new byte[cols * rows * elemSize];
            outputMat.get(0, 0, data);
            int type = outputMat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
            BufferedImage finalImage = new BufferedImage(cols, rows, type);

            System.arraycopy(data, 0, ((DataBufferByte)finalImage.getRaster().getDataBuffer()).getData(), 0, data.length);
            return finalImage;

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
