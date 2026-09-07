package kidstech;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
// import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import javax.swing.*;

import resources.Jama.Matrix;

public class DQImagePanelAbstract extends JPanel implements MouseListener, MouseMotionListener {
    private BufferedImage image;
    private int quiltSize;
    private int[][] corners = new int[2][4];
    private int index = 0;
    private int currentX;
    private int currentY;

    public DQImagePanelAbstract( BufferedImage image, int quiltSize) {
        super();
        this.image = image;
        this.quiltSize = quiltSize;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
   
    public int[][] getCorners() {
        if (index ==4) {
            return(corners);
        }
        else {
            return(null);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image,0,0,null);

        g.setColor(new Color(255,255,255));
        for(int i=1; i < index; i++) {
            g.drawLine( corners[0][i-1],corners[1][i-1],
                corners[0][i], corners[1][i]);
        }

        if (index > 0 && index < 4) {
            g.drawLine(corners[0][index-1], corners[1][index-1],
                currentX, currentY);
        }
        
        if (index == 4) {
            g.drawLine(corners[0][0],corners[1][0],
                corners[0][3], corners[1][3]);
            drawQuiltOverlay(g,corners);
        }
    }

    // this is the actual grid that's drawn
    private void drawQuiltOverlay(Graphics g, int corners[][]) {
        Homography h = new Homography();
        Matrix picPoints = new Matrix (4,2);
        Matrix quiltPoints = new Matrix (4,2);

        // Picture points use picture coordinates, so they shouldn't change
        // if the quilt block dimensions change

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


        // Quilt points creates a quilt that has it's lower left corner at the
        // origin (0,0) and its upper-right point that corresponds with its
        // dimensions. So generally, this will be (2(quiltSize), 2(quiltSize)).
        // For example (2(4), 2(4)) == (8, 8)

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

        // calculate homography
        h.findHomography(picPoints,quiltPoints);

        // Each block is divided into 4 board squares which are divided into 4
        // equilateral triangles. That means 16 triangles per block and therefore
        // 64 triangles in a 2x2 quilt.

        // First, we will draw the board lines
        
        // draw vertical lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(i,0);
            double [] p2 = h.reverseTranslatePoint(i,quiltSize);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw horizontal lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(quiltSize,i);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw 1/4 diagonal lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(i,0);
            double [] p2 = h.reverseTranslatePoint(quiltSize,quiltSize-i);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw 2/4 diagonal lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(quiltSize-i,quiltSize);
            g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
        }

        // draw 3/4 diagonal lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(i,0);
            g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
        }

        // draw 4/4 diagonal lines
        for (int i = 0; i < (quiltSize+1); i++) {
            double [] p1 = h.reverseTranslatePoint(i,quiltSize);
            double [] p2 = h.reverseTranslatePoint(quiltSize,i);
            g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
        }
    }


    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == e.BUTTON1) // left mouse click
        {
            if (index == 4) index = 0;

            corners[0][index] = e.getX();
            corners[1][index] = e.getY();
            index = index+1;
            System.out.println("Selected point:" + corners[0][index-1] + "," + corners[1][index-1]);
        
            if (index ==4) {
                System.out.println("GENERATING DIGIQUILT COMPATIBLE XML");

                DQTranslateXML translator = new DQTranslateXML(this.quiltSize, "QuiltGrabber-Translated-Quilt", "Translated-Quilt-1");

                Homography h = new Homography();

                String compiledXMLPayload = translator.buildQuiltXML(this.image, this.corners, h);

                String savePath = "./Translated-Quilt.xml.gz";
                String PNGSavePath = "./Translated-Quilt.png";

                System.out.println("\nSUCCESS DIGIQUILT XML COMPRESSED AND SAVED");
                try (FileOutputStream fileStream = new FileOutputStream(savePath);
                    GZIPOutputStream gzipStream = new GZIPOutputStream(fileStream)) {

                    byte[] xmlBytes = compiledXMLPayload.getBytes(StandardCharsets.UTF_8);
                    // this line converts the string to bytes and compresses them

                    gzipStream.write(xmlBytes);

                    gzipStream.finish();

                    DQGeneratePNG.saveAsPNG(this, PNGSavePath);

                // // Instantly writes the exact byte footprint profile directly down to the hard drive path
                // Files.write(Paths.get(savePath), compiledXMLPayload.getBytes(), 
                //             StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                
                // System.out.println(" SUCCESS: DIGIQUILT XML SAVED!");
                // System.out.println(" Location: " + savePath);

                }catch (IOException ioException) {
                System.err.println("ERROR: FAILED TO SAVE FILE");
                ioException.printStackTrace();
            }
            }
        }
        repaint();
    }
    public void mouseEntered(MouseEvent e) {};
    public void mouseExited(MouseEvent e) {};
    public void mousePressed(MouseEvent e) {};
    public void mouseReleased( MouseEvent e) {};

    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        repaint();
    }

    public void mouseDragged(MouseEvent e) {};
}