package kidstech;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class QuiltGrabber {

 public static void main( String args[]) {
  BufferedImage bimg = null;
  // quiltSize is number of patches per one side of the quilt (standard DigiQuilt sizes are 2x2, 3x3, 4x4)
  int quiltSize = 3;
  try {
    bimg = ImageIO.read( new File("src/test/resources/QuiltPhotos/IMG_1928.JPG"));
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
