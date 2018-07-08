import java.util.*;

public class Player {
    private List<Item> bag;

    Player(){
        this.bag = new ArrayList<>();
    }

    public boolean pickUpItem(Item item){
        if(bag.size() <= 2){
            System.out.println(bag.size());
            bag.add(item);
            return true;
        }
        return false;
    }

    public Item dropItem(String itemname) {
        for(Item x : bag){
            String itemname2 = itemname;
            String itemname3 = x.getItem();
			if (itemname2.equals(itemname3)) {
                bag.remove(x);
                return x;
            }
            
        }
        Item somethingwentwrong = new Item("ERROR");
        return somethingwentwrong;
        
    }

    public boolean hasItem(Item item){
        return bag.contains(item);
    }

    public String getBagString() {
        String returnString = "Items in Bag: [ ";
        for(Item x : bag) {
            returnString += x.getItem() + ", ";
        }
        returnString += " ]";
        return returnString;
    }
}