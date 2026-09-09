package kidstech.quiltObjects;

// referenced from DigiQuilt
public class Patch {
    /**
     * The maximum number of tiles possible in any Patch.
     */
    public static final int MAXTILES = 16;
    /**
     * Array of tiles, the actual backend implementation of a Patch.
     */
    private Fabric[] tileArray;

    /**
     * Default constructor for a patch.
     */
    public Patch() {
        tileArray = new Fabric[MAXTILES];
        for (int i = 0; i < MAXTILES; i++) {
            tileArray[i] = Fabric.TRANSPARENT;
        }
    }

    /**
     * (Re)Create a patch with certain Fabrics.
     * 
     * @param fabrics an array of fabric names
     */
    public Patch(Fabric[] fabrics){
        if (fabrics.length != MAXTILES){
            throw new IllegalArgumentException();
        }
        tileArray = new Fabric[MAXTILES];
        for (int i = 0; i < MAXTILES; i++){
            tileArray[i] = fabrics[i];
        }
    }

    /**
     * Getter for a specific tile of the patch.
     * 
     * @param index the tile number to get
     * @return the FabricTile at that location
     */
    public Fabric getTile(final int index) {
        if (index < MAXTILES && index >= 0) {
            return tileArray[index];
        } 
        throw new IndexOutOfBoundsException();
    }

    /**
     * Method to set the tiles in a Patch to a FabricTile. This should only
     * be used internally or for testing purposes, the "right" thing to do is
     * to use the constructor. 
     * 
     * @param index numbered location of FabricTile in Patch.
     * @param tile input tile which will be set to the index location in Patch.
     */
    protected void setTile(final int index, final Fabric tile) {
        if (index < MAXTILES && index >= 0) {
            tileArray[index] = tile;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

}