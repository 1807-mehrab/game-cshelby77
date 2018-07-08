import java.util.*;

public class Room {
    private String description;
    private HashMap<String, Room> exits;
    private List<Item> itemList;

    public Room (String description) {
        this.description = description;
        exits = new HashMap<>();
        itemList = new ArrayList<>();
    }

    // Items
    /***************************************/
    public void setItems(Item item) {
        itemList.add(item);
    }

    private String getItemString() {
        String returnString = "Items in Room: [ ";
        for(Item x : itemList) {
            returnString += x.getItem() + ", ";
        }
        returnString += " ]";
        return returnString;
    }
    
    public boolean doesItemExist(Item item) {
       return itemList.contains(item);
    }

    public Item takeItem(String itemname) {
        for(Item x : itemList){
            String itemname2 = itemname;
            String itemname3 = x.getItem();
			if (itemname2.equals(itemname3)) {
                itemList.remove(x);
                return x;
            }
            
        }
        Item somethingwentwrong = new Item("ERROR");
        return somethingwentwrong;
        
    }

    // Descriptions
    /***************************************/
    public String getShortDescription() {
        return description;
    }

    public String getLongDescription() {
        return "\nYou are in the " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    // Exits
    /***************************************/
    public void setExits(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    private String getExitString() {
        String returnString = "Exits: ";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += exit + ", ";
        }
        return returnString;
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }
}