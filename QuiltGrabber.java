import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class QuiltGrabber {

 public static void main( String args[]) {
  BufferedImage bimg = null;
  int quiltSize = 4;
  try {
    bimg = ImageIO.read( new File("Harley-Test-Quilts - Paper-Palette-23.png"));
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
