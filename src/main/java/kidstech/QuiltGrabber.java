package kidstech;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
public class QuiltGrabber {

 private static int quiltSize;

 public static void main( String args[]) {
  BufferedImage bimg = null;
  // quiltSize is number of patches per one side of the quilt (standard DigiQuilt sizes are 2x2, 3x3, 4x4)
  quiltSize = 4;
  try {
    bimg = ImageIO.read( new File("src/test/resources/QuiltPhotos/IMG_1929.JPG"));
    bimg = ImageResize.resize(bimg, 1000);
  } catch( Exception e) {
    e.printStackTrace();
 	}

  System.out.println("Click the four corners of the DigiQuilt.");
  System.out.println("Start at the top left, then work your way clockwise!");
	
  DQImagePanelAbstract dqip = new DQImagePanelAbstract(bimg, quiltSize);
  JFrame myFrame = new JFrame("QuiltGrabber");
  myFrame.setBounds(0,0,bimg.getWidth(), bimg.getHeight() + 500);

  JFXPanel dqPanel = new JFXPanel();

  // set up for QuiltGrab panel and control panel
  myFrame.setLayout(new BorderLayout());
  myFrame.add(dqPanel, BorderLayout.SOUTH); // javafx
  myFrame.add(dqip, BorderLayout.CENTER); // swing
  myFrame.setVisible(true);
  myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  // grid icons from DigiQuilt
  ImageView icon2x2 = new ImageView(new Image("file:src/main/java/resources/icons/2x2.png"));
  ImageView icon3x3 = new ImageView(new Image("file:src/main/java/resources/icons/3x3.png"));
  ImageView icon4x4 = new ImageView(new Image("file:src/main/java/resources/icons/4x4.png"));

  // applying the grid icons to quilt sizing buttons
  Button button2x2 = new Button("", icon2x2);
  Button button3x3 = new Button ("", icon3x3);
  Button button4x4 = new Button ("", icon4x4);

  // grouping buttons together for placement
  HBox gridButtons = new HBox(15, button2x2, button3x3, button4x4);
  gridButtons.setAlignment(Pos.CENTER);

  // instructional label for buttons
  Label gridLabel = new Label("Select Your Quilt Block Sizing");
  gridLabel.setFont(new Font("Arial", 18));
   
  // grouping buttons and label for placement
  VBox gridSizing = new VBox(8, gridLabel, gridButtons);
  gridSizing.setAlignment(Pos.CENTER);

  Scene scene = new Scene(gridSizing);
  dqPanel.setScene(scene);

  // giving each button the ability to update quiltSize and change the displayed grid
  button2x2.setOnAction(e -> dqip.updateQuiltSize(2));
  button3x3.setOnAction(e -> dqip.updateQuiltSize(3));
  button4x4.setOnAction(e -> dqip.updateQuiltSize(4));

  // this part may not actually be needed!
  // javafx.application.Platform.runLater(() -> {
  // });

 }
}
