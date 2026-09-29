package kidstech.pngGeneration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import kidstech.pngGeneration.handlers.DQSAXHandler;
import kidstech.quiltObjects.Block;
import kidstech.quiltObjects.Grid;

// Code by Daniel Selifonov, in DigiQuilt

// referenced from DigiQuilt
public class LoadXML
{

    /**
     * The SAX ContentHandler which knows how to load from XML.
     */
    DQSAXHandler dqHandler;
    
    private String filename;
    
    /**
     * Loads all of the save data from the specified file name into this LoadXML.
     * 
     * @param filename The file to be loaded.
     * @throws SAXException 
     * @throws IOException 
     */
    public LoadXML(String filename) throws IOException, SAXException {
	this.filename = filename;
        FileInputStream fileIn = new FileInputStream(filename);
        loadFromStream(fileIn);
    }

    /**
     * Load all of the saved XML data from the specified InputStream.
     * 
     * @param in
     * @throws IOException
     * @throws SAXException
     */
    public LoadXML(InputStream in) throws IOException, SAXException {
        loadFromStream(in);
    }

    /**
     * Loads and parses a GZIPped input stream of XML into this instance of LoadXML.
     * The results can be accessed using the get methods.
     * 
     * @param in
     * @throws IOException
     * @throws SAXException
     */
    private final void loadFromStream(InputStream in) throws SAXException, IOException{
        XMLReader xmlRead = XMLReaderFactory.createXMLReader();
        dqHandler = new DQSAXHandler(xmlRead);
        xmlRead.setContentHandler(dqHandler);
        xmlRead.setErrorHandler(dqHandler);
        
        if(filename == null || filename.endsWith(".gz")){
            xmlRead.parse(new InputSource(new GZIPInputStream(in)));
        }
        else{
            xmlRead.parse(new InputSource(in));
        }
    }

    /**
     * @return The block that was marked as the current block from the XML file. If
     * there wasn't one, or nothing has been loaded yet, it will be null.
     */
    public Block getCurrentBlock(){
        return dqHandler.getCurrentBlock();
    }

    /**
     * @return The grid that was loaded from XML, if any. If there wasn't one,
     * this may be null.
     */
    public Grid getGrid(){
        return dqHandler.getCurrentGrid();
    }

    // /**
    //  * @return the name of the quilt block.
    //  */
    // public String getBlockName() {
    //     return dqHandler.getBlockName();
    // }
}

