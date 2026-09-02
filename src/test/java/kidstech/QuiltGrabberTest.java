package kidstech;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class QuiltGrabberTest {

    @Test
    public void testSomething() {
        BufferedImage bimg = null;
        // quiltSize is number of blocks per one side of the quilt (NOT PATCHES)
        int quiltSize = 4;
        try {
            bimg = ImageIO.read( new File("src/test/resources/QuiltPhotos/IMG_1932.JPG"));
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

    private static boolean isImageExtension(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".bmp");
    }

    static Stream<Arguments> quiltPhotoProvider() throws IOException {
        File folder = new File("src/test/resources/QuiltPhotos/");
        
        // Filter and map all files in the directory to JUnit Arguments
        return Files.walk(folder.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> isImageExtension(path.toString()))
                .map(Path::toFile)
                .map(Arguments::of); // Feeds File objects into the test
    }

    @ParameterizedTest(name = "Testing image file: {0}")
    @MethodSource("quiltPhotoProvider")
    void testUseOfPhotos(File imageFile) throws IOException {
        // In the parameterized test, you can run through each image in QuiltPhotos
        // Perhaps try using different color selection strategies (one pixel, patch, vote)
        // A passing test would mean that your strategy works for all of the images

        // compare a CORRECT digiquilt xml to the GENERATED digiquilt xml to determine if it worked
        // Maybe the process can generate the xml and also count the number of correct matches... include that in the name of the generated thing?
        // Save the xml files so that you can look at them in DigiQuilt... 
        // You might also track what the mismatch was (red when should have been brown, for example)

        // Assert file exists and can be read
        assertTrue(imageFile.exists(), "Image file should exist");

        // Load the image into memory
        BufferedImage image = ImageIO.read(imageFile);
        assertEquals("IMG_1932.JPG", imageFile.getName());

        // EXECUTE YOUR LOGIC HERE
        // Example: int width = image.getWidth();
        // Example: YourComponent.process(image);
        
        // For testing visual generation or matching:
        assertTrue(image.getWidth() > 0, "Image width must be valid");
    }



    
}
