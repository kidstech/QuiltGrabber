package kidstech;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;

import resources.Jama.Matrix;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class DQImagePanel2x2 extends JPanel implements MouseListener, MouseMotionListener {
    private BufferedImage image;
    private int[][] corners = new int[2][4];
    private int index = 0;
    private int currentX;
    private int currentY;

    public DQImagePanel2x2( BufferedImage image) {
        super();
        this.image = image;
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
        // dimensions. For a 2x2 quilt, this should be (4,4) because it was (8,8)
        // for the 4x4 quilt.

        // top left
        quiltPoints.set(0,0,0);
        quiltPoints.set(0,1,4);

        // top right
        quiltPoints.set(1,0,4);
        quiltPoints.set(1,1,4);

        // bottom right
        quiltPoints.set(2,0,4);
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
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(i,0);
            double [] p2 = h.reverseTranslatePoint(i,4);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw horizontal lines
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(4,i);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw 1/4 diagonal lines
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(i,0);
            double [] p2 = h.reverseTranslatePoint(4,4-i);
            g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
        }

        // draw 2/4 diagonal lines
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(4-i,4);
            g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
        }

        // draw 3/4 diagonal lines
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(0,i);
            double [] p2 = h.reverseTranslatePoint(i,0);
            g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
        }

        // draw 4/4 diagonal lines
        for (int i = 0; i < 5; i++) {
            double [] p1 = h.reverseTranslatePoint(i,4);
            double [] p2 = h.reverseTranslatePoint(4,i);
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
