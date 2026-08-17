import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class QuiltGrabber {

 public static void main( String args[]) {
  BufferedImage bimg = null;
  int quiltSize = 2;
  try {
    bimg = ImageIO.read( new File("IMG_1932.JPG"));
      } catch( Exception e) {
	e.printStackTrace();
 	}
   System.out.println("Click the four corners of the DigiQuilt.");
   System.out.println("Start at the top left, then work your way clockwise!");
	
   DQImagePanelAbstract dqip = new DQImagePanelAbstract(bimg, quiltSize);
   JFrame myFrame = new JFrame("QuiltGrabber");
   myFrame.setBounds(0,0,bimg.getWidth(), bimg.getHeight());
   myFrame.add(dqip);
   myFrame.setVisible(true);
	
	
 }

}
