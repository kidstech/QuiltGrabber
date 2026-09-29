package kidstech;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class PLGSelector extends JPanel implements MouseListener, MouseMotionListener {
 private BufferedImage image;
 private int[][] corners = new int[2][4];
 private int index = 0;
 private int currentX;
 private int currentY;


 public PLGSelector( BufferedImage image) {
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
   } // draw the last line connecting to the top!
 }


 // Mouse Listener methods:
 public  void 	mouseClicked(MouseEvent e) {
  if (e.getButton() == e.BUTTON1) // left mouse click
  {
    if (index == 4) {
      this.getCorners();
      index = 0;
    }
    // putting this conditionally executed code into brackets might help us do more stuff without being confused

    corners[0][index] = e.getX();
    corners[1][index] = e.getY();
    index = index+1;
    System.out.println("Selected point:" + corners[0][index-1] + "," + corners[1][index-1]);
  }// end if left mouse click.
   repaint();
   // System.out.print(this.getCorners()[0].toString() + this.getCorners()[1] + this.getCorners()[2] + this.getCorners()[3]);
   // We might want to print all of the corners, but this isn't quite how to do that.
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
