import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

//Matrix class to store correspondance points.
import Jama.Matrix;

public class DQImagePanel extends JPanel implements MouseListener, MouseMotionListener {
 private BufferedImage image;
 private int[][] corners = new int[2][4];
 private int index = 0;
 private int currentX;
 private int currentY;


 public DQImagePanel( BufferedImage image) {
  super();
  this.image = image;
  addMouseListener(this);
  addMouseMotionListener(this);
 }
  

 public int[][] getCorners() {

   if (index == 4) {
      return(corners);
    }
    else {
	 return(null);
     }
 }
   
 protected void paintComponent(Graphics g) {
  super.paintComponent(g);
  g.drawImage(image,0,0,null);

  // Draw the mouse lines if needed.
  g.setColor( new Color(255,255,255));
  for(int i = 1; i < index; i++) {
    g.drawLine( corners[0][i-1],corners[1][i-1], 
		corners[0][i],corners[1][i]);
  }
  if ( index > 0 && index < 4 ) {
    // Draw the current mouse move line:
    g.drawLine(corners[0][index-1],corners[1][index-1],
		currentX,currentY);
  }

  if (index == 4) {
	g.drawLine( corners[0][0],corners[1][0],
		     corners[0][3], corners[1][3]);
        // Draw the quilt overlay!
        drawQuiltOverlay(g, corners);

   } // draw the last line connecting to the top!
 }

//Draws a Quilt outline on top of the image at the specified corners.
private void  drawQuiltOverlay(Graphics g, int corners[][]) {

  Homography h = new Homography();
  Matrix picPoints = new Matrix(4,2);
  Matrix quiltPoints = new Matrix(4,2);

  //We assume the first point clicked on is the top-left point of the
  // quilt, and then proceeded clockwise...
  // picture points use the picture coordinates.
  picPoints.set(0,0, corners[0][0]);
  picPoints.set(0,1, corners[1][0]);

  picPoints.set(1,0, corners[0][1]);
  picPoints.set(1,1, corners[1][1]);
 
  picPoints.set(2,0, corners[0][2]);
  picPoints.set(2,1, corners[1][2]);
 
  picPoints.set(3,0, corners[0][3]);
  picPoints.set(3,1, corners[1][3]);


  //Quilt points use an imaginary quilt that has it's lower left corner at the
  // origin (0,0) and it's uper-right point at (8,8).

  // Top left:
  quiltPoints.set(0,0, 0);
  quiltPoints.set(0,1, 8);
  
  //top right:
  quiltPoints.set(1,0,8);
  quiltPoints.set(1,1,8);

  //bottom right:
  quiltPoints.set(2,0,8);
  quiltPoints.set(2,1,0);

  //bottom left (origin)
  quiltPoints.set(3,0,0);
  quiltPoints.set(3,1,0);

  // Calculate the homography.
  h.findHomography(picPoints,quiltPoints); 

 
  //A Digiquilt consists of an 8x8 board, where each board square
  // is then divided into 4 equladeral triangles, for a total of 256
  // triangles.
 
  // This is drawn easiest by drawing the board and then diagonal lines.
  // draw the vertical lines...
  for(int i = 0; i < 9; i++)  {
   double [] p1 = h.reverseTranslatePoint( i,0);
   double [] p2 = h.reverseTranslatePoint(i,8);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end for vertical lines.

 // draw the horizontal lines...
  for(int i = 0; i < 9; i++)  {
   double [] p1 = h.reverseTranslatePoint( 0,i);
   double [] p2 = h.reverseTranslatePoint(8,i);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end for horizontal lines.

 // Draw 1/4 diagonal lines...
 for(int i = 0; i < 9; i++) {
   double [] p1 = h.reverseTranslatePoint(i,0);
   double [] p2 = h.reverseTranslatePoint(8,8-i);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end diagonal lines..


 // Draw 1/4 diagonal lines...
 for(int i = 0; i < 9; i++) {
   double [] p1 = h.reverseTranslatePoint(0,i);
   double [] p2 = h.reverseTranslatePoint(8-i,8);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end diagonal lines..


 // Draw 1/4 diagonal lines...
 for(int i = 0; i < 9; i++) {
   double [] p1 = h.reverseTranslatePoint(0,i);
   double [] p2 = h.reverseTranslatePoint(i,0);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end diagonal lines..

 // Draw 1/4 diagonal lines...
 for(int i = 0; i < 9; i++) {
   double [] p1 = h.reverseTranslatePoint(i,8);
   double [] p2 = h.reverseTranslatePoint(8,i);
   g.drawLine( (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1] );
  } // end diagonal lines..
}

 // Mouse Listener methods:
 public  void 	mouseClicked(MouseEvent e) {
  if (e.getButton() == e.BUTTON1) // left mouse click
  {
    if (index == 4) index = 0;

    corners[0][index] = e.getX();
    corners[1][index] = e.getY();
    index = index+1;
    System.out.println("Selected point:" + corners[0][index-1] + "," + corners[1][index-1]);
  }// end if left mouse click.
   repaint();
 };

 public void mouseEntered(MouseEvent e) {};
 public void mouseExited(MouseEvent e) {};
 public void mousePressed(MouseEvent e) {};
 public void mouseReleased( MouseEvent e) {};

 // Mouse Motion listener events:
 public void mouseMoved(MouseEvent e) {
   currentX = e.getX();
   currentY = e.getY();  
   repaint();

 } // end mouse moved.
 public void mouseDragged(MouseEvent e) {};
}
