package kidstech.pngGeneration;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import kidstech.quiltObjects.Patch;
import kidstech.quiltObjects.Tile;

// Code by biatekjt, in DigiQuilt

// referenced from DigiQuilt
@SuppressWarnings("serial")
public class PatchViewer extends JPanel {

    /**
     * Patch backend. 
     */
    private Patch thePatch;
    /**
     * Image of the Patch to be displayed.
     */
    private Image image;

    /**
     * The length of a side of a full sized Patch. In other words, a full
     * square will be fullSize by fullSize, whereas a rectangle could be
     * fullSize/2 by fullSize.
     */
    private int fullSize;

    /**
     * Create a viewer with the given patch.
     * 
     * @param patch
     * @param size The size of a full sized patch. A full sized square will
     * be size by size.
     */
    public PatchViewer(Patch patch, int size){
        thePatch = patch;
        fullSize = size;
        setVisible(true);
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        refreshImage();
    }

    /**
     * Regenerate the image of the patch being held, and repaint.
     */
    public final void refreshImage() {
        image = generateImage(thePatch, fullSize);
        this.repaint();
    }

    @Override
    public void paintComponent(final Graphics graphic) {
        super.paintComponent(graphic);
        final Graphics2D drawSpace = (Graphics2D) graphic.create();
        if (image != null) {
            drawSpace.drawImage(image, 0, 0, null);
        }
        drawSpace.dispose();
    }

    /**
     * Method to create an image representation of a Patch.
     * 
     * @param patch The patch to create an image of
     * @param patchSize The desired full size of the square. If cropping is
     * enabled, the returned image may be cropped to half or a quarter of the
     * size, depending on the shape of the Patch. Otherwise, the returned 
     * image should be patchSize by patchSize.
     * @param cropped Whether or not to crop the image, shrinking the image
     * just like Patch's getSmallPatch() method does. This is meant for 
     * use with display in the Hand.
     * 
     * @return An Image that represents the Patch
     */
    public static BufferedImage generateImage(
        Patch patch, int patchSize){
        int width = patchSize;
        int height = patchSize;
        Patch myPatch = patch;

        final BufferedImage display = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D canvas = display.createGraphics();

        for (int i=0; i<Patch.MAXTILES; i++){
            Tile theTile = new Tile(i, myPatch.getTile(i), 1);
            theTile.paintTile(canvas, patchSize);
        }
        return display;
    }
}
