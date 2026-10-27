package kidstech;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import kidstech.pngGeneration.DQGeneratePNG;
import kidstech.pngGeneration.LoadXML;
import kidstech.quiltObjects.Block;
public class QuiltGrabber {

 private static int quiltSize;

 public static void main( String args[]) {
  BufferedImage bimg = null;
  // quiltSize is number of patches per one side of the quilt (standard DigiQuilt sizes are 2x2, 3x3, 4x4)
  quiltSize = 4; // default
  
  // try {
  //   bimg = ImageIO.read( new File("src/test/resources/QuiltPhotos/IMG_1929.JPG"));
  //   bimg = ImageResize.resize(bimg, 1000);
  // } catch( Exception e) {
  //   e.printStackTrace();
 	// }

  System.out.println("Click the four corners of the DigiQuilt.");
  System.out.println("Start at the top left, then work your way clockwise!");
	
  DQImagePanelAbstract dqip = new DQImagePanelAbstract(bimg, quiltSize);
  JFrame myFrame = new JFrame("QuiltGrabber");
  myFrame.setBounds(0,0,1000, 1000 + 800);

  JFXPanel sizingPanel = new JFXPanel();

  JFXPanel instructionPanel = new JFXPanel();

  // set up for QuiltGrab panel and control panel
  myFrame.setLayout(new BorderLayout());
  myFrame.add(sizingPanel, BorderLayout.SOUTH); // javafx
  myFrame.add(instructionPanel, BorderLayout.NORTH); // javafx
  myFrame.add(dqip, BorderLayout.CENTER); // swing
  myFrame.setVisible(true);
  myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


  // south selection panel

  // grid icons from DigiQuilt
  ImageView icon2x2 = new ImageView(new Image("file:src/main/java/resources/icons/2x2.png"));
  ImageView icon3x3 = new ImageView(new Image("file:src/main/java/resources/icons/3x3.png"));
  ImageView icon4x4 = new ImageView(new Image("file:src/main/java/resources/icons/4x4.png"));

  // applying the grid icons to quilt sizing buttons
  Button button2x2 = new Button("", icon2x2);
  Button button3x3 = new Button ("", icon3x3);
  Button button4x4 = new Button ("", icon4x4);

  // giving each button the ability to update quiltSize and change the displayed grid
  button2x2.setOnAction(e -> dqip.updateQuiltSize(2));
  button3x3.setOnAction(e -> dqip.updateQuiltSize(3));
  button4x4.setOnAction(e -> dqip.updateQuiltSize(4));

  // this part may not actually be needed!
  // javafx.application.Platform.runLater(() -> {
  // });

  // close Quilt Grabber
  Button exitQG = new Button("Exit");
  exitQG.setOnAction(new EventHandler<ActionEvent>() {
    public void handle(ActionEvent e) {
      myFrame.dispose();
    }
  });

  // create a File chooser
  FileChooser fileChooser = new FileChooser();

  fileChooser.setTitle("Open Image File");

  // create a Button
  Button chooseFile = new Button("Choose File");

  // create an Event Handler
  chooseFile.setOnAction(new EventHandler<ActionEvent>() {

    public void handle(ActionEvent e) {
      Window stage = chooseFile.getScene().getWindow();

      // get the file selected
      File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
        try {
          // Load image
          BufferedImage newBimg = ImageIO.read(file);
          newBimg = ImageResize.resize(newBimg, 900);
          dqip.updateImage(newBimg); 
                            
          myFrame.setBounds(0, 0, newBimg.getWidth(), newBimg.getHeight() + 800);
                            
          myFrame.revalidate();
          myFrame.repaint();
        } catch (Exception e2) {
          e2.printStackTrace();
        }
      }
    }
  });

  Button saveQuilt = new Button("Save Quilt Block");
  saveQuilt.setOnAction(new EventHandler<ActionEvent>() {
    public void handle(ActionEvent e) {
      Stage saveWindow = new Stage();
      String tempBlockName = "tempBlockName";

      Block tempBlock = dqip.translateQuiltBlock(tempBlockName);

      if (tempBlock == null) {
          System.err.println("Quilt block could not be generated");
          return;
      }

      int selectedQuiltSize = dqip.getQuiltSize();

      java.awt.image.BufferedImage bufferedImg = DQGeneratePNG.generateQuiltPNG(tempBlock, selectedQuiltSize);

      if (bufferedImg == null) {
        System.err.println("Preview could not be generated");
        return;
      }

      Image quiltBlock = SwingFXUtils.toFXImage(bufferedImg, null);
      ImageView quiltBlockPreview = new ImageView(quiltBlock);

      quiltBlockPreview.setFitHeight(500);
      quiltBlockPreview.setFitWidth(500);

      Label blockNameLabel = new Label("Block Name:");
      TextField quiltBlockName = new TextField();

      quiltBlockName.setPromptText("Block Name...");

      Button confirmSave = new Button("Save");

        confirmSave.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent e) {
            String confirmedBlockName = quiltBlockName.getText();

            if (!confirmedBlockName.isEmpty()){
              dqip.saveQuiltBlock(confirmedBlockName, tempBlock);
              saveWindow.close();
            }
          }
        });
      VBox saveOptions = new VBox(10, quiltBlockPreview, blockNameLabel, quiltBlockName, confirmSave);
      saveOptions.setAlignment(Pos.CENTER);
      saveOptions.setPadding(new Insets(20));

      Scene saveScene = new Scene(saveOptions);
      saveWindow.setScene(saveScene);
      saveWindow.show();
    }
    
  });

  // grouping buttons together for placement
  HBox gridButtons = new HBox(15, button2x2, button3x3, button4x4);
  gridButtons.setAlignment(Pos.CENTER);

  // instructional label for buttons
  Label gridLabel = new Label("Select Your Quilt Block Sizing");
  gridLabel.setFont(new Font("Arial", 18));

  HBox fileOptions = new HBox(15, exitQG, chooseFile, saveQuilt);
  fileOptions.setAlignment(Pos.CENTER);
   
  // grouping buttons and label for placement
  VBox gridSizing = new VBox(8, gridLabel, gridButtons, fileOptions);
  gridSizing.setAlignment(Pos.CENTER);

  Scene selectionScene = new Scene(gridSizing);
  sizingPanel.setScene(selectionScene);


  // north instructions panel 
  Label instr1 = new Label("Click each corner of your Quilt Block");
  instr1.setFont(new Font("Arial", 18));

  Label instr2 = new Label("Start at the top left and work your way Clockwise");
  instr2.setFont(new Font("Arial", 18));

  VBox instructions = new VBox(instr1, instr2);
  instructions.setAlignment(Pos.CENTER);

  Scene instructionScene = new Scene(instructions);
  instructionPanel.setScene(instructionScene);
 }
}
