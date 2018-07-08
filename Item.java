import java.util.*;

public class Item {
    String description;

    Item(String description) {
        this.description = description;
    }

    public String getItem() {
        return description;
    }

    public Item clone() {
        Item c = new Item(this.description);
        return c;
    }
} 