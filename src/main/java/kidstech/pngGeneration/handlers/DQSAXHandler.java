package kidstech.pngGeneration.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import kidstech.quiltObjects.Block;
import kidstech.quiltObjects.Grid;

// Code by Jason Biatek, biatekjt 
// on Date: 2009-06-10 16:58:41

// referenced from DigiQuilt
public class DQSAXHandler extends DelegatingHandler {

    /**
     * The current block loaded from XML. 
     */
    private Block currentBlock;
    
    /**
     * The current grid loaded from XML.
     */
    private Grid currentGrid;
    
    
    /**
     * The tag we left off on, for example "Notes" or "grid". We delegate
     * the actual loading of these to their respective handlers, and then when
     * they send us an object with childFinished() we can use this to know 
     * what to expect.
     */
    String currentDelegation;

    /**
     * @param xmlReader
     */
    public DQSAXHandler(XMLReader xmlReader) {
        super(xmlReader);
    }

    /**
     * @return the block marked "current" in the save file.
     */
    public Block getCurrentBlock(){
        return currentBlock;
    }
    
    /**
     * @return the grid from the save file.
     */
    public Grid getCurrentGrid(){
        return currentGrid;
    }
    
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        currentDelegation = name;
        if (name.equals("Block")){
            BlockHandler blockH = new BlockHandler(this, attributes);
            blockH.startHandlingEvents();
        } else if (name.equals("Grid")){
            GridHandler gridH = new GridHandler(this);
            gridH.startHandlingEvents();
        } else if (name.equals("Notes")
                || name.equals("Student")
                || name.equals("BlockName")
                || name.equals("Timestamp")){
            // Simple text contents
            TextHandler textH = new TextHandler(this, name);
            textH.startHandlingEvents();
        }
    }

    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.xmlsaveload.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        if (currentDelegation.equals("Block")){
            // This was listed as the "current" block
            currentBlock = (Block) o;
        } else if (currentDelegation.equals("Grid")){
            currentGrid = (Grid) o;
        }
    }
}
