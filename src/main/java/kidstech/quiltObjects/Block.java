package kidstech.quiltObjects;

import java.util.Iterator;
import java.util.NoSuchElementException;

// referenced from DigiQuilt
public class Block implements Iterable<Patch>{

    /**
     * An array to carry the patches.
     */
    private Patch[] patchArray;

    /**
     * The size of this Block, in Patches
     */
    private int size = 0;

    /**
     * The height or width of this Block, in Patches
     */
    private int sideSize = 0;

    /**
     * Default constructor. The default size should be SIXTEEN. Sets all patches
     * to the default (gray) patch.
     */
    public Block() {
        this(16);
    }

    /**
     * Constructor that takes an int to determine the size of the Block (how
     * many patches will be in the block).
     * 
     * @param size
     *            The size to construct the Block with.
     */
    public Block(final int size) {
        // Check that size is a valid size. If not, throw exception;
        if ((size == 16) || (size == 9) || (size == 4) || (size == 1)) {
            patchArray = new Patch[size];
            sideSize = (int) Math.sqrt(size);
            for (int i = 0; i < patchArray.length; i++) {
                patchArray[i] = new Patch();
            }
            this.size = size;
        } else {
            System.out.println("Block Size: " + size);
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Method to get the size of the Block (in patches)
     * 
     * @return size The size of the Block.
     */
    public int getSize() {
        return size;
    }

    /**
     * Method to get the height/width of this block, in patches
     * @return height/width of block
     */
    public int getSideSize() {
        return sideSize;
    }

    /**
     * Method to put a patch on a specified location on the block, while
     * checking that the specified location is valid.
     * 
     * @param aPatch
     *            The patch to be placed.
     * @param location
     *            the desired location to place the patch.
     */
    public void setPatch(final Patch aPatch, final int location) {
        if (location >= 0 && location < patchArray.length) {
            patchArray[location] = aPatch;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Method to get a Patch at a specific location
     * 
     * @param location
     *            The location of the desired Patch.
     * @return patchArray[location] The desired Patch.
     */
    public Patch getPatch(final int location) {
        if (location >= 0 && location < patchArray.length) {
            return patchArray[location];
        }
        throw new IndexOutOfBoundsException();

    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Patch> iterator() {
        return new Iterator<Patch>() {

            int current = 0;
            
            public boolean hasNext() {
                return current < getSize();
            }

            public Patch next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                
                Patch thePatch = getPatch(current);
                current++;
                return thePatch;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }


}

