package world_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * This class represents a named region for a geographic application where
 * all regions are hierarchically related. A region should have an id, name,
 * and type, and may optionally have a captial.
 * 
 * @author  Richard McKenna 
 *          Debugging Enterprises
 * @version 1.0
 */
public class Region<T extends Comparable<T>> implements Comparable<Region<T>>
{
    // UNIQUE IDENTIFIER FOR THIS REGION
    private String id;
    
    // NAME OF REGION
    private String name;
    
    //COLORS OF REGIONS
    
    private int red;
    private int green;
    private int blue;
    
    // CAPTIAL OF THIS REGION. NOTE THAT SOME REGIONS DO NOT HAVE A CAPITAL
    private String capital;
    
    //Leader
    private String leader;
    
    // TYPE OF REGION
    private RegionType type;
    
    // THE PARENT REGION, WITHIN WHICH THIS REGION IS CONTAINED
    private Region parentRegion;
    
    // LIST OF CHILD REGIONS FOR THIS ONE. FOR EXAMPLE, A NATION
    // WOULD LIST STATES HERE
    private ArrayList<Region> subRegions;
    
    /**
     * Constructor that initializes the three required fields for any
     * region: its id, name, and type
     * 
     * @param initId The unique identifier for this regions. No two
     * regions may have the same id.
     * 
     * @param initName The name of this region, which would typically
     * be used for display purposes.
     * 
     * @param initType The type of this region
     */
    
    public Region(String initId, String initName, RegionType initType){
        id = initId;
        name = initName;
        type = initType;
        
        parentRegion = null;
        capital = null;
        
        subRegions = new ArrayList();
    }
    public Region(String initName, String initRed, String initGreen, String initBlue)
    {
        // INIT THE PROVIDED FIELDS
       
        name = initName;
        
        red = Integer.parseInt(initRed);
        green = Integer.parseInt(initGreen);;
        blue = Integer.parseInt(initBlue);;
               
        
        // NULL THE MISSING FIELDS
        parentRegion = null;
        capital = null;
        
        // AND SETUP THE LIST SO WE CAN ADD CHILD REGIONS
        subRegions = new ArrayList();
    }
    
    public Region(String name){
        name = name;
        
        id = null;
        type = null;
        
        // NULL THE MISSING FIELDS
        parentRegion = null;
        capital = null;
        
        // AND SETUP THE LIST SO WE CAN ADD CHILD REGIONS
        subRegions = new ArrayList();
    }
    
    /**
     * Constructor that initializes the three required fields for any
     * region: its id, name, and type
     * 
     * @param initId The unique identifier for this regions. No two
     * regions may have the same id.
     * 
     * @param initName The name of this region, which would typically
     * be used for display purposes.
     * 
     * @param initType The type of this region.
     * 
     * @param initCapital The capital of this region.
     */
    public Region(String initName, String initRed, String initGreen, String initBlue, String initCapital)
    {
        // LET THE OTHER CONSTRUCTOR DO MOST OF THE SETUP WORK
        this(initName, initRed, initGreen, initBlue);
        
        // WE'LL KEEP THE CAPITAL
        capital = initCapital;
    }
    
    public Region(String initName, String initRed, String initGreen, String initBlue, String initCapital, String initLeader){
        
        this(initName, initRed, initGreen, initBlue);
        leader = initLeader;
    }

    // ACCESSOR METHODS

    /**
     * Accessor method for getting this region's id.
     * 
     * @return The id of this region.
     */
    public String       getId()             { return id;            }
    
    /**
     * Accessor method for getting this region's name.
     * 
     * @return The name of this region.
     */    
    public String       getName()           { return name;          }

    /**
     * Accessor method for getting this region's type.
     * 
     * @return The type of this region.
     */    
    public RegionType   getType()           { return type;          }

    /**
     * Accessor method for getting this region's parent region.
     * 
     * @return The parent region of this region.
     */    
    public Region       getParentRegion()   { return parentRegion;  }

    /**
     * Accessor method for getting this region's capital.
     * 
     * @return The name of the capital of this region.
     */    
    public String       getCapital()        { return capital;       }
    
    public int          getRed()            { return red;       }
    
    public int          getGreen()            { return green;       }
    
    public int          getBlue()            { return blue;       }
    
    public String       getLeader()         {   return leader; }
    /**
     * Accessor method for getting all of this regions subregions
     * in the form of an Iterator.
     * 
     * @return An Iterator that can traverse sequentially through
     * all of the child regions of this region.
     */
    public Iterator<Region> getSubRegions()
    {
        return subRegions.iterator();
    }

    /**
     * Accessor method for getting the child Region of this one that
     * has an id the same as the subRegionId argument.
     * 
     * @param subRegionId The region id of the Region we're looking for.
     * 
     * @return The found region with the same id as the subRegionId argument.
     */
    public Region getSubRegion(String subRegionId)
    {
        // GO THROUGH ALL THE CHILD REGIONS
        Iterator it = subRegions.iterator();
        while (it.hasNext())
        {
            Region subRegion = (Region)it.next();
            
            // DID WE FIND IT?
            if (subRegion.id.equals(subRegionId))
            {
                // YUP, RETURN IT
                return subRegion;
            }
        }
        // NOPE, RETURN NULL
        return null;
    }    
    
    /**
     * This method tests to see if this region is a leaf region (i.e.
     * has no child regions) or not.
     * 
     * @return true if this region has child regions, false otherwise.
     */    
    public boolean hasSubRegions()
    {
        return !subRegions.isEmpty();
    }    
    
    /**
     * This method tests to see if this region has a named capital at
     * the moment. 
     * 
     * @return true if this region has a capital, false otherwise.
     */
    public boolean hasCapital() 
    { 
        return capital != null; 
    }

    // MUTATOR METHODS
    
    /**
     * Mutator method for changing this region's unique identifier.
     * 
     * @param initId Unique id to be used for this region.
     */
    public void setId(String initId)
    {
        id = initId;
    }
    
    /**
     * Mutator method for changing this region's textual name.
     * 
     * @param initName Name to be used for this region's display.
     */
    public void setName(String initName)
    {
        name = initName;
    }

    /**
     * Mutator method for changing this region's named capital.
     * 
     * @param initCapital Capital to be used for this region.
     */
    public void setCapital(String initCapital)
    {
        capital = initCapital;
    }

    /**
     * Mutator method for setting the type of this region.
     * 
     * @param initType The region type to be used for this region.
     */
    public void setType(RegionType initType)
    {
        type = initType;
    }
    
    public void setLeader(String leader){
        this.leader = leader;
    }
    public void setRed(){
        
    }

    /**
     * Mutator method for setting the parent region for this region. Note
     * that this is a mutual relationship. The parent knows about the child
     * and vice versa, which makes walking up and down the tree easier.
     * 
     * @param initParentRegion The parent region in relation to this one.
     */
    public void setParentRegion(Region initParentRegion)
    {
        parentRegion = initParentRegion;
    }
    
    // ADDITIONAL SERVICE METHODS
    
    /**
     * Adds another region to be a child region of this one. 
     * 
     * @param subRegionToAdd Region to be added as a sub region. For 
     * example, a nation would be a subregion of a continent.
     */
    public void addSubRegion(Region subRegionToAdd)
    {
        // ADD IT TO OUR DATA STRUCTURE
        subRegions.add(subRegionToAdd);
        
        // AND LET'S KEEP IT SORTED BY NAME
        Collections.sort(subRegions);
    }

    /*
     * Removes the subRegionToRemove from the list of children
     * regions for this one.
     */
    public void removeSubRegion(Region subRegionToRemove)
    {
        // TAKE IT OUT OF OUR DATA STRUCTURE
        subRegions.remove(subRegionToRemove);
    }    

    /**
     * Used for comparing Regions for the purpose of sorting them.
     * 
     * @param region The Region to be compared to this one.
     * 
     * @return 0 if they have the same name, -1 if this Region's
     * name alphabetically precedes it, and 1 if it follows it.
     */
    @Override
    public int compareTo(Region<T> region)
    {
        return name.compareTo(region.name);
    }

    /**
     * Method for testing equivalence of this region with the 
     * regionAsObject argument.
     * 
     * @param regionAsObject The region to test for equivalence
     * with this one.
     * 
     * @return true if they have the same id, false otherwise.
     */
    public boolean equals(Object regionAsObject)
    {
        if (regionAsObject instanceof Region)
        {
            Region region = (Region)regionAsObject;
            return id.equals(region.id);
        }
        return false;
    }

    /**
     * Generates a textual representation of this region.
     * 
     * @return The textual representation of this region, which is
     * simply the name.
     */
   @Override
    public String toString()
    {
        return name;
    }
}