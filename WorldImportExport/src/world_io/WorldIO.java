package world_io;

import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import world_data.Region;
import world_data.RegionType;
import world_data.WorldDataManager;
import world_data.WorldImporterExporter;
import static world_io.WorldIOSettings.*;
import xml_utilities.XMLUtilities;
import java.lang.Iterable;

/**
 * This class serves as a plugin for reading and writing worlds
 * to and from XML files using WorldSchema.xsd.
 * 
 * @author  Richard McKenna
 *          Debugging Enterprises
 * @version 1.0
 */
public class WorldIO implements WorldImporterExporter
{
    // THIS WILL HELP US PARSE THE XML FILES
    private XMLUtilities xmlUtil;
    
    // THIS IS THE SCHEMA WE'LL USE
    private File worldSchema;

    /**
     * Constructor for making our importer/exporter. Note that it
     * initializes the XML utility for processing XML files and it
     * sets up the schema for use.
     */
    public WorldIO(File initWorldSchema)
    {
        xmlUtil = new XMLUtilities();
        
        // WE'LL USE THE SCHEMA FILE TO VALIDATE THE XML FILES
        worldSchema = initWorldSchema;
    }
    
    /**
     * Reads the geographic data found in regionsFile into worldToLoad.
     *
     * @param regionsFile The XML file to load the data from.
     * 
     * @param worldToLoad The world to fill with the data from the
     * XML file.
     * 
     * @return true if the world loads successfully, false otherwise.
     */
    @Override
    public boolean loadWorld(File regionsFile, WorldDataManager worldToLoad)
    {
        try
        {
            // FIRST LOAD ALL THE XML INTO A TREE
            Document doc = xmlUtil.loadXMLDocument( regionsFile.getAbsolutePath(), 
                                                    worldSchema.getAbsolutePath());
            
            // THEN LET'S LOAD THE LIST OF ALL THE REGIONS
            loadRegionsList(doc, worldToLoad);
            
            // AND NOW CONNECT ALL THE REGIONS TO EACH OTHER
            loadRegionsMappings(doc, worldToLoad);
        }
        catch(Exception e)
        {
            // WORLD DIDN'T LOAD PROPERLY
            return false;
        }
        // WORLD LOADED PROPERLY
        return true;
    }
    
    /**
     * Private helper method for loading our world. This method loads
     * the complete list of regions into the world.
     * 
     * @param doc The hierarchical Regions data loaded from an XML
     * file into a Document, we'll extract the data from this doc
     * and load it into the world.
     * 
     * @param world The data manager for all the regions. We'll load
     * the data in the doc into this object.
     */
    private void loadRegionsList(   Document doc,
                                    WorldDataManager world)
    {
        // EMPTY THE REGIONS LIST
        world.clearRegions();
        
        // FIRST GET THE REGIONS LIST
        Node regionsListNode = xmlUtil.getNodeWithName(doc, REGION_NODE);
        
        // AND THEN GO THROUGH AND ADD ALL THE LISTED REGIONS
        ArrayList<Node> regionsList = xmlUtil.getChildNodesWithName(regionsListNode, SUB_REGION_NODE);
        for (int i = 0; i < regionsList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node regionNode = regionsList.get(i);
            NamedNodeMap regionAttributes = regionNode.getAttributes();
           // String id = regionAttributes.getNamedItem(ID_ATTRIBUTE).getNodeValue();
            String name = regionAttributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
           // String type = regionAttributes.getNamedItem(TYPE_ATTRIBUTE).getNodeValue();
            String red = regionAttributes.getNamedItem(RED_ATTRIBUTE).getNodeValue();
            String green = regionAttributes.getNamedItem(GREEN_ATTRIBUTE).getNodeValue();
            String blue = regionAttributes.getNamedItem(BLUE_ATTRIBUTE).getNodeValue();
            //RegionType regionType = RegionType.valueOf(type);
            Node leaderNode = regionAttributes.getNamedItem("leader");
            
            Region regionToAdd;
            Node capitalNode = regionAttributes.getNamedItem(CAPITAL_ATTRIBUTE);
            if (capitalNode != null)
            {
                // MAKE A REGION WITH A CAPITAL
                String capital = capitalNode.getNodeValue();
                regionToAdd = new Region(name, red, green, blue, capital);
            }
            else
            {
                // MAKE A REGION WITHOUT A CAPITAL
                regionToAdd = new Region(name, red, green, blue);
            }
            
            if(leaderNode != null){
                String leader = leaderNode.getNodeValue();
                regionToAdd = new Region(name, red, green, blue, leader);
            }
            else{
                regionToAdd = new Region(name, red, green, blue);
            }
            // PUT THE REGION IN THE WORLD
            world.addRegion(regionToAdd);
//            if (regionType == RegionType.WORLD)
//            {
//                world.setRoot(regionToAdd);
//            }
        }
    }

    /**
     * Private helper method for loading all the region mappings.
     * 
     * @param doc The hierarchical Regions data loaded from an XML
     * file into a Document, we'll extract the data from this doc
     * and load it into the world.
     * 
     * @param world The data manager for all the regions. We'll load
     * the data in the doc into this object.
     */
    private void loadRegionsMappings(Document doc, WorldDataManager world)
    {      
        // NOW GET THE REGIONS MAPPINGS
        Node regionsMappingsNode = doc.getElementsByTagName(REGIONS_MAPPINGS_NODE).item(0);
        
        // AND THEN GET THE MAPPINGS
        ArrayList<Node> regionsMapped = xmlUtil.getChildNodesWithName(regionsMappingsNode, REGION_NODE);
        for (int i = 0; i < regionsMapped.size(); i++)
        {
            // AND THEN GET THEIR SUB REGIONS
            Node regionNode = regionsMapped.get(i);
            String regionID = regionNode.getAttributes().getNamedItem(ID_ATTRIBUTE).getNodeValue();
            Region region = world.getRegion(regionID);
            
            // MAP ALL THE CHILD NODES TO PARENTS
            // AND PARENTS TO CHILDREN
            ArrayList<Node> subRegionNodes = xmlUtil.getChildNodesWithName(regionNode, SUB_REGION_NODE);
            int numSubRegions = subRegionNodes.size();
            for (int j = 0; j < numSubRegions; j++)
            {
                Node subRegionNode = subRegionNodes.get(j);
                NamedNodeMap subRegionAttributes = subRegionNode.getAttributes();
                Node subRegionIdNode = subRegionAttributes.getNamedItem(ID_ATTRIBUTE);
                String subRegionID = subRegionIdNode.getNodeValue();
                Region subRegion = world.getRegion(subRegionID);
                region.addSubRegion(subRegion);
                subRegion.setParentRegion(region);
            }
        }        
    }   
    
    /**
     * This method saves the world currently being edited to the worldFile. Note
     * that it will be saved as an .xml file, which is an XML-format that will
     * conform to the WorldRegions.xsd schema.
     * 
     * @param worldFile The file to write the world to.
     * 
     * @return true if the file is successfully saved, false otherwise. It's
     * possible that another program could lock out ours from writing to it,
     * so we need to let the caller know when this happens.
     */
    public boolean saveWorld(File worldFile, WorldDataManager worldToSave)
    {
        try 
        {
            // THESE WILL US BUILD A DOC
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // FIRST MAKE THE DOCUMENT
            Document doc = docBuilder.newDocument();
            
            // THEN THE ROOT ELEMENT
            Element rootElement = doc.createElement(WORLD_REGIONS_NODE);
            doc.appendChild(rootElement);
 
            // THEN THE REGIONS LIST
            Element regionsListElement = makeElement(doc, rootElement, REGIONS_LIST_NODE, "");
            
            // AND LET'S ADD ALL THE REGIONS
            for (Region region : worldToSave.getAllRegions().values())
            {
                // MAKE A REGION NODE AND ADD IT TO THE LIST
                Element regionNodeElement = makeElement(doc, regionsListElement,
                        REGION_NODE, "");
                
                // NOW LET'S FILL IN THE REGION'S DATA. FIRST MAKE THE ATTRIBUTES
                doc.createAttribute(ID_ATTRIBUTE); 
                doc.createAttribute(NAME_ATTRIBUTE);
                doc.createAttribute(TYPE_ATTRIBUTE);
                doc.createAttribute(CAPITAL_ATTRIBUTE);
                regionNodeElement.setAttribute(ID_ATTRIBUTE, region.getId());
                regionNodeElement.setAttribute(NAME_ATTRIBUTE, region.getName());
                regionNodeElement.setAttribute(TYPE_ATTRIBUTE, region.getType().toString());
                if (region.hasCapital())
                {
                    regionNodeElement.setAttribute(CAPITAL_ATTRIBUTE, region.getCapital());
                }   
             }

            // AND NOW ADD ALL THE CONNECTIONS
            Element regionsMappingsElement = makeElement(doc, rootElement, REGIONS_MAPPINGS_NODE, "");
            
            
            for (Region region: worldToSave.getAllRegions().values())
            {
                // MAKE A REGION NODE AND ADD IT TO THE LIST
                Element regionNodeElement = makeElement(doc, regionsMappingsElement, REGION_NODE, "");
                regionNodeElement.setAttribute(ID_ATTRIBUTE, region.getId());
                
                Iterator<Region> subRegionsIt = region.getSubRegions();
                while (subRegionsIt.hasNext())
                {
                    Region subRegion = subRegionsIt.next();
                    Element subRegionNodeElement = makeElement(doc, regionNodeElement, SUB_REGION_NODE, "");
                    subRegionNodeElement.setAttribute(ID_ATTRIBUTE, subRegion.getId());
                }
            }

            // THE TRANSFORMER KNOWS HOW TO WRITE A DOC TO
            // An XML FORMATTED FILE, SO LET'S MAKE ONE
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, YES_VALUE);
            transformer.setOutputProperty(XML_INDENT_PROPERTY, XML_INDENT_VALUE);
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(worldFile);
            
            // SAVE THE POSE TO AN XML FILE
            transformer.transform(source, result);    
            
            return true;
        }
        catch(TransformerException | ParserConfigurationException | DOMException | HeadlessException ex)
        {
            return false;
        }    
    }   
    
    /**
     * This helper method builds elements (nodes) for us to help with building
     * a Doc which we would then save to a file.
     * 
     * @param doc The document we're building.
     * 
     * @param parent The node we'll add our new node to.
     * 
     * @param elementName The name of the node we're making.
     * 
     * @param textContent The data associated with the node we're making.
     * 
     * @return A node of name elementName, with textComponent as data, in the doc
     * document, with parent as its parent node.
     */
    private Element makeElement(Document doc, Element parent, String elementName, String textContent)
    {
        Element element = doc.createElement(elementName);
        element.setTextContent(textContent);
        parent.appendChild(element);
        return element;
    }
}