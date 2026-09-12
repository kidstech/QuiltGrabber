import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ImageResize {
    public static BufferedImage resize (BufferedImage image, int maxDimension) {
        if (image == null) {
            return null;
        } // null handling

        // initializing original dimensions
        int oldWidth = image.getWidth();
        int oldHeight = image.getHeight();

        // checking if original dimensions fit constraint
        if (oldWidth <= maxDimension && oldHeight <= maxDimension) {
            return image;
        }

        // initializing new dimensions
        int newWidth = oldWidth;
        int newHeight = oldHeight;

        // picking larger image dimension to set as the maximum
        if (oldWidth > oldHeight) {
            newWidth = maxDimension;
            newHeight = (oldHeight * maxDimension) / oldWidth;
        } else {
            newHeight = maxDimension; 
            newWidth = (oldWidth * maxDimension) / oldHeight;
        }

        int imageType = image.getType();
        
        // getting image type to be able to draw new resized image
        if (imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }

        // this was found on stack overflow: https://stackoverflow.com/questions/9417356/bufferedimage-resize
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }
}
