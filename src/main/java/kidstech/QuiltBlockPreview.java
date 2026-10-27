package kidstech;

import java.awt.image.BufferedImage;
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
import javafx.stage.Stage;
import kidstech.pngGeneration.DQGeneratePNG;
import kidstech.quiltObjects.Block;


// Preview Quilt Block Save Window
public class QuiltBlockPreview {

    private final DQImagePanelAbstract dqip;
    private final Stage saveWindow;

    public QuiltBlockPreview(DQImagePanelAbstract dqip) {
        this.dqip = dqip;
        this.saveWindow = new Stage();
        this.saveWindow.setTitle("Save Quilt Block");
    }

    public void display() {
        String tempBlockName = "tempBlockName";
        Block tempBlock = dqip.translateQuiltBlock(tempBlockName);
        int selectedQuiltSize = dqip.getQuiltSize();


        if (tempBlock == null) {
          System.err.println("Quilt block could not be generated");
          return;
        } 

        // generate DigiQuilt Preview Quilt Block
        BufferedImage previewBufferedImg = DQGeneratePNG.generateQuiltPNG(tempBlock, selectedQuiltSize);

        if (previewBufferedImg == null) {
            System.err.println("Preview could not be generated");
            return;
        }

        Image quiltBlock = SwingFXUtils.toFXImage(previewBufferedImg, null);
        ImageView quiltBlockPreview = new ImageView(quiltBlock);

        quiltBlockPreview.setFitHeight(500);
        quiltBlockPreview.setFitWidth(500);

        // generate QuiltGrab
        BufferedImage quiltGrabBufferedImage = DQGeneratePNG.generatePNG(dqip);

        if (quiltGrabBufferedImage == null) {
            System.err.println("Quilt Grab could not be generated");
            return;
        }

        Image quiltGrab = SwingFXUtils.toFXImage(quiltGrabBufferedImage, null);
        ImageView quiltGrabImageView = new ImageView(quiltGrab);

        // logic from ImageResize but for ImageView
        quiltGrabImageView.setFitHeight(500);
        quiltGrabImageView.setFitWidth((quiltGrab.getWidth() * 500) / quiltGrab.getHeight());

        Label blockNameLabel = new Label("Block Name:");
        TextField quiltBlockName = new TextField();

        quiltBlockName.setPromptText("Block Name...");

        // set up exit and save buttons
        Button confirmSave = new Button("Save");
        Button exit = new Button("Exit");

        confirmSave.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                String confirmedBlockName = quiltBlockName.getText();

                if (!confirmedBlockName.isEmpty()) {
                    dqip.saveQuiltBlock(confirmedBlockName, tempBlock);
                    saveWindow.close();
                }
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                saveWindow.close();
            }
        });

        HBox saveSelectOptions = new HBox(8, exit, confirmSave);
        saveSelectOptions.setAlignment(Pos.CENTER);

        // set up save options
        
        HBox quiltBlockImages = new HBox(quiltGrabImageView, quiltBlockPreview);
        quiltBlockImages.setAlignment(Pos.CENTER);

        VBox saveOptions = new VBox(10, quiltBlockImages, blockNameLabel, quiltBlockName, saveSelectOptions);
        saveOptions.setAlignment(Pos.CENTER);
        saveOptions.setPadding(new Insets(20));

        Scene saveScene = new Scene(saveOptions);
        saveWindow.setScene(saveScene);
        saveWindow.show();
    }
}
