import java.util.*;

import javax.lang.model.util.ElementScanner6;


public class Main {
    private Parser parser;
    private Room currentRoom, principalLocation; 
    private List<Item> itemList, timList;
    private Item keys = new Item("Keys");
    private Player player, timNPC;
    private int move = 2;
    private boolean mettim = false, gotStuff = false;

    public static void main(String[] args) {
        Main game = new Main();
        game.play();
    }

    public Main() {
        timNPC = new Player();
        createItems();
        createRooms();
        player = new Player();
        parser = new Parser();
    }

    private void createRooms() {
        Room classroom1, classroom2, bathroomm, bathroomf, hallway1, hallway2, hallway3, hallway4, gym, cafeteria, teacherslounge, office;
        List<Room> tempRoomList = new ArrayList<Room>();
        timList = new ArrayList<Item>();

        classroom1 = new Room("Classroom 102");
        classroom2 = new Room("Classroom 103");
        bathroomm = new Room("Boy's Bathroom");
        bathroomf = new Room("Girl's Bathroom");
        hallway1 = new Room("Hallway A");
        hallway2 = new Room("Hallway B");
        hallway3 = new Room("Hallway C");
        hallway4 = new Room("Hallway D");
        gym = new Room("Gym");
        cafeteria = new Room("Cafeteria");
        teacherslounge = new Room("Teacher's Lounge");
        office = new Room("Principal's Office");

        office.setExits("Hallway", hallway1);
        cafeteria.setExits("Hallway", hallway2);

        classroom1.setExits("Hallway", hallway2);
        classroom2.setExits("Hallway", hallway3);

        teacherslounge.setExits("Hallway", hallway3);
        gym.setExits("Hallway", hallway4);

        bathroomm.setExits("Hallway", hallway4);
        bathroomf.setExits("Hallway", hallway4);

        hallway1.setExits("Office", office);
        hallway1.setExits("Up", hallway2);

        hallway2.setExits("Classroom", classroom1);
        hallway2.setExits("Cafeteria", cafeteria);
        hallway2.setExits("Down", hallway1);
        hallway2.setExits("Up", hallway3);

        hallway3.setExits("TeachersLounge", teacherslounge);
        hallway3.setExits("Classroom", classroom2);
        hallway3.setExits("Down", hallway2);
        hallway3.setExits("Up", hallway4);

        hallway4.setExits("B-Bathroom", bathroomm);
        hallway4.setExits("G-Bathroom", bathroomf);
        hallway4.setExits("Gym", gym);
        hallway4.setExits("Down", hallway3);

        //Add rooms to tempRoomList
        tempRoomList.add(classroom1);
        tempRoomList.add(classroom2);
        tempRoomList.add(bathroomm);
        tempRoomList.add(bathroomf);
        tempRoomList.add(hallway1);
        tempRoomList.add(hallway2);
        tempRoomList.add(hallway3);
        tempRoomList.add(hallway4);
        tempRoomList.add(gym);
        tempRoomList.add(cafeteria);
        tempRoomList.add(teacherslounge);
        tempRoomList.add(office);

        
        // Placing items in room
        Random rand = new Random();
        for (Item x : itemList) {
            if ( x.getItem() == "Knife") {
                cafeteria.setItems(x);
            } else {
                Room temp;
                int randomIndex = rand.nextInt(tempRoomList.size());
                temp = tempRoomList.get(randomIndex);
                temp.setItems(x);
            }
        }

        // Picking tim's items
        for (int i = 0; i < 2; i++) {
            Item temp;
            int randomIndex = rand.nextInt(itemList.size());
            temp = itemList.get(randomIndex);
            if(!timList.contains(temp)){ 
                timList.add(temp); 
            } else {
                i--;
            }
        }
        
        currentRoom = hallway1;
        principalLocation = office;
    }

    private void createItems() {
        Item knife, hammer, mask, rope, bag, chloroform, coffee, laptop, tape, phone, pen, mop;
        itemList = new ArrayList<Item>();

        knife = new Item("Knife");
        hammer = new Item("Hammer");
        mask = new Item("Mask");
        rope = new Item("Rope");
        bag = new Item("Bag");
        chloroform = new Item("Chloroform");
        coffee = new Item("Coffee");
        laptop = new Item("Laptop");
        tape = new Item("Tape");
        phone = new Item("CellPhone");
        pen = new Item("Pen");
        mop = new Item("Mop");
        

        itemList.add(knife);
        itemList.add(hammer);
        itemList.add(mask);
        itemList.add(rope);
        itemList.add(bag);
        itemList.add(chloroform);
        itemList.add(coffee);
        itemList.add(laptop);
        itemList.add(tape);
        itemList.add(phone);
        itemList.add(pen);
        itemList.add(mop);

        Item keys2 = keys;
		timNPC.pickUpItem(keys2);

    }

    public void play() {
        System.out.println();
        System.out.println("Try to Escape the School before you get caught");
        System.out.println(currentRoom.getLongDescription());
        printMap();
        System.out.println();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
            if(principalLocation == currentRoom){
                System.out.println("You were caught by the principal.");
                System.out.println("Game Over");
                finished = true;
            }
            if(currentRoom.getShortDescription() == "Hallway A"){
                if(player.hasItem(keys)){
                    System.out.println("Congradulations! You Escaped!");
                    finished = true;
                } else {
                    System.out.println("The exit is locked!");
                }
            }
            if(currentRoom.getShortDescription() == "Gym"){
                interactWithTim();
            }
        }
        System.out.println("Thanks for playing.");
    }

    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't understand!");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("stay")){
            movePrincipal();
            System.out.println(currentRoom.getLongDescription());
            System.out.println(player.getBagString());
            if (currentRoom == principalLocation) {
                printSadFace();
            } else {
            printMap();
            }
        } else if (commandWord.equals("get")) {
            pickUpItem(command);
        } else if (commandWord.equals("drop")) {
            dropItem(command);
        } else if (commandWord.equals("help")) {
            System.out.println();
            parser.showCommands();
        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }

        return wantToQuit;
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            //If there is no second word
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        //Try to leave current room
        Room nextRoom = currentRoom.getExit(direction);

        if(nextRoom == null) {
            System.out.println("Can't go there!");
        } else {
            
            currentRoom = nextRoom;
            movePrincipal();
            System.out.println(currentRoom.getLongDescription());
            System.out.println(player.getBagString());
            if (currentRoom == principalLocation) {
                printSadFace();
            } else {
            printMap();
            }
        }
    }

    private void pickUpItem(Command command) {
        if (!command.hasSecondWord()) {
            //If there is no second word
            System.out.println("Get what?");
            return;
        }

        String itemName = command.getSecondWord();

        //Try to leave current room
        Item itemGrabbed = currentRoom.takeItem(itemName);

        if(itemGrabbed.getItem() == "ERROR") {
            System.out.println("Item Doesn't Exist!");
        } else {
            
            if(player.pickUpItem(itemGrabbed)){
                movePrincipal();
                System.out.println(currentRoom.getLongDescription());
                System.out.println(player.getBagString());
                if (currentRoom == principalLocation) {
                    printSadFace();
                } else {
                    printMap();
                }
            } else {
                System.out.println("Bag is full. Can't pick up item.");
                currentRoom.setItems(itemGrabbed);
            }
        }
    }

    private void dropItem(Command command) {
        if (!command.hasSecondWord()) {
            //If there is no second word
            System.out.println("Drop what?");
            return;
        }

        String itemName = command.getSecondWord();
        

        Item itemDropped = player.dropItem(itemName);

        if(itemDropped.getItem() == "ERROR") {
            System.out.println("You don't have this item!");
        } else {
            currentRoom.setItems(itemDropped);
            movePrincipal();
            System.out.println(currentRoom.getLongDescription());
            System.out.println(player.getBagString());
            if (currentRoom == principalLocation) {
                printSadFace();
            } else {
                printMap();
            }
            
        }
    }

    private boolean quit(Command command) {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;
        }
    }

    private void printMap() {
        /*
        "------------------------------------------------"
        "|        |        |        |        |           |"
        "|        |        |        |        |           |"
        "|        |        |        |        |           |"
        "------------------------------------|           |"
        "|                                   |           |"
        "------------------------------------|           |"
        "|        |        |        |        |           |"
        "|        |        |        |        |           |"
        "|        |        |        |        |           |"
        "------------------------------------------------"
        */

        
        /********************************************************************/
        // Print upper rooms
        /********************************************************************/
        System.out.println("------------------------------------------------");
        System.out.println("|        |        |        |        |           |");

        boolean principalupper = inUpperRoom(principalLocation);
        boolean youupper = inUpperRoom(currentRoom);
        if (!youupper && !principalupper){
            // Nobody Here
            System.out.println("|        |        |        |        |           |");
        } else if (youupper && !principalupper){
            // Just You
            if (currentRoom.getShortDescription() == "Classroom 102") {
                System.out.println("|        |   Y    |        |        |           |");
            } else if (currentRoom.getShortDescription() == "Teacher's Lounge") {
                System.out.println("|        |        |   Y    |        |           |");
            } else {
                System.out.println("|        |        |        |   Y    |           |");
            } 
        } else if (!youupper && principalupper){
            // Just Principal
            if (principalLocation.getShortDescription() == "Classroom 102") {
                System.out.println("|        |   P    |        |        |           |");
            } else if (principalLocation.getShortDescription() == "Teacher's Lounge") {
                System.out.println("|        |        |   P    |        |           |");
            } else {
                System.out.println("|        |        |        |   P    |           |");
            } 
        } else {
            //Both
            if (currentRoom.getShortDescription() == "Classroom 102" && principalLocation.getShortDescription() == "TeachersLounge") {
                System.out.println("|        |   Y    |   P    |        |           |");
            } else if (currentRoom.getShortDescription() == "Classroom 102" && principalLocation.getShortDescription() == "Boy's Bathroom") {
                System.out.println("|        |   Y    |        |   P    |           |");
            } else if (currentRoom.getShortDescription() == "Teacher's Lounge" && principalLocation.getShortDescription() == "Classroom 102") {
                System.out.println("|        |   P    |   Y    |        |           |");
            } else if (currentRoom.getShortDescription() == "Teacher's Lounge" && principalLocation.getShortDescription() == "Boy's Bathroom") {
                System.out.println("|        |        |   Y    |   P    |           |");
            } else if (currentRoom.getShortDescription() == "Boy's Bathroom" && principalLocation.getShortDescription() == "Classroom 102") {
                System.out.println("|        |    P   |        |   Y    |           |");
            } else if (currentRoom.getShortDescription() == "Boy's Bathroom" && principalLocation.getShortDescription() == "Teacher's Lounge") {
                System.out.println("|        |        |   P    |   Y    |           |");
            }
        }


        System.out.println("|        |        |        |        |           |");
        System.out.println("------------------------------------|           |");


        /********************************************************************/
        // Print Middle Rooms
        /********************************************************************/
        boolean principalmiddle = inMiddleRoom(principalLocation);
        boolean youmiddle = inMiddleRoom(currentRoom);

        if (!youmiddle && !principalmiddle) {
            System.out.println("|                                   |           |");
        } else if (youmiddle && !principalmiddle){
            // Just You
            if (currentRoom.getShortDescription() == "Hallway A") {
                System.out.println("|   Y                               |           |");
            } else if (currentRoom.getShortDescription() == "Hallway B") {
                System.out.println("|            Y                      |           |");
            } else if (currentRoom.getShortDescription() == "Hallway C") {
                System.out.println("|                     Y             |           |");
            } else if (currentRoom.getShortDescription() == "Hallway D") {
                System.out.println("|                              Y    |           |");
            } else {
                System.out.println("|                                   |     Y     |");
            }
        } else if (!youmiddle && principalmiddle){
            // Just Principal
            if (principalLocation.getShortDescription() == "Hallway A") {
                System.out.println("|   P                               |           |");
            } else if (principalLocation.getShortDescription() == "Hallway B") {
                System.out.println("|            P                      |           |");
            } else if (principalLocation.getShortDescription() == "Hallway C") {
                System.out.println("|                     P             |           |");
            } else if (principalLocation.getShortDescription() == "Hallway D") {
                System.out.println("|                              P    |           |");
            } else {
                System.out.println("|                                   |     P     |");
            }
        } else {
            if (currentRoom.getShortDescription() == "Hallway A") { 
                if (principalLocation.getShortDescription() == "Hallway B") {
                    System.out.println("|   Y        P                      |           |");
                } else if (principalLocation.getShortDescription() == "Hallway C") {
                    System.out.println("|   Y                 P             |           |");
                } else if (principalLocation.getShortDescription() == "Hallway D") {
                    System.out.println("|   Y                          P    |           |");
                } else {
                    System.out.println("|   Y                               |     P     |");
                } 
            } else if (currentRoom.getShortDescription() == "Hallway B"){
                if(principalLocation.getShortDescription() == "Hallway A"){
                    System.out.println("|   P        Y                      |           |");
                } else if(principalLocation.getShortDescription() == "Hallway C"){
                    System.out.println("|            Y        P             |           |");
                } else if(principalLocation.getShortDescription() == "Hallway D"){
                    System.out.println("|            Y                 P    |           |");
                } else {
                    System.out.println("|            Y                      |     P     |");
                }
            } 
            
            
            else if (currentRoom.getShortDescription() == "Hallway C") {
                if(principalLocation.getShortDescription() == "Hallway A"){
                    System.out.println("|   P                 Y             |           |");
                } else if(principalLocation.getShortDescription() == "Hallway B"){
                    System.out.println("|            P        Y             |           |");
                } else if(principalLocation.getShortDescription() == "Hallway D"){
                    System.out.println("|                     Y        P    |           |");
                } else {
                    System.out.println("|                     Y             |     P     |");
                }
            } 
            else if (currentRoom.getShortDescription() == "Hallway D") {
                if(principalLocation.getShortDescription() == "Hallway A"){
                    System.out.println("|   P                          Y    |           |");
                } else if(principalLocation.getShortDescription() == "Hallway B"){
                    System.out.println("|            P                 Y    |           |");
                } else if(principalLocation.getShortDescription() == "Hallway C"){
                    System.out.println("|                     P        Y    |           |");
                } else {
                    System.out.println("|                              Y    |     P     |");
                }
            } 
            else {
                if(principalLocation.getShortDescription() == "Hallway A"){
                    System.out.println("|   P                               |     Y     |");
                } else if(principalLocation.getShortDescription() == "Hallway B"){
                    System.out.println("|            P                      |     Y     |");
                } else if(principalLocation.getShortDescription() == "Hallway C"){
                    System.out.println("|                     P             |     Y     |");
                } else {
                    System.out.println("|                              P    |     Y     |");
                }
            }
        }


        /********************************************************************/
        // Print lower rooms
        /********************************************************************/
        System.out.println("------------------------------------|           |");
        System.out.println("|        |        |        |        |           |");

        boolean principallower = inLowerRoom(principalLocation);
        boolean youlower = inLowerRoom(currentRoom);

        if(!youlower && !principallower){
            System.out.println("|        |        |        |        |           |");
        } else if (youlower && !principallower) {
            if (currentRoom.getShortDescription() == "Principal's Office") {
                System.out.println("|   Y    |        |        |        |           |");
            } else if (currentRoom.getShortDescription() == "Cafeteria") {
                System.out.println("|        |   Y    |        |        |           |");
            } else if (currentRoom.getShortDescription() == "Classroom 103") {
                System.out.println("|        |        |   Y    |        |           |");
            } else {
                System.out.println("|        |        |        |   Y    |           |");
            } 
        } else if (!youlower && principallower) {
            if (principalLocation.getShortDescription() == "Principal's Office") {
                System.out.println("|   P    |        |        |        |           |");
            } else if (principalLocation.getShortDescription() == "Cafeteria") {
                System.out.println("|        |   P    |        |        |           |");
            } else if (principalLocation.getShortDescription() == "Classroom 103") {
                System.out.println("|        |        |   P    |        |           |");
            } else  {
                System.out.println("|        |        |        |   P    |           |");
            } 
        } else {
            if (currentRoom.getShortDescription() == "Principal's Office") {
                if (principalLocation.getShortDescription() == "Cafeteria") {
                    System.out.println("|   Y    |   P    |        |        |           |");
                } else if (principalLocation.getShortDescription() == "Classroom 103") {
                    System.out.println("|   Y    |        |   P    |        |           |");
                } else {
                    System.out.println("|   Y    |        |        |   P    |           |");
                } 
            } else if (currentRoom.getShortDescription() == "Cafeteria") {
                if (principalLocation.getShortDescription() == "Principal's Office") {
                    System.out.println("|   P    |   Y    |        |        |           |");
                } else if (principalLocation.getShortDescription() == "Classroom 103") {
                    System.out.println("|        |   Y    |   P    |        |           |");
                } else {
                    System.out.println("|        |   Y    |        |   P    |           |");
                } 
            } else if (currentRoom.getShortDescription() == "Classroom 103") {
                if (principalLocation.getShortDescription() == "Principal's Office") {
                    System.out.println("|   P    |        |   Y    |        |           |");
                } else if (principalLocation.getShortDescription() == "Cafeteria") {
                    System.out.println("|        |   P    |   Y    |        |           |");
                } else {
                    System.out.println("|        |        |   Y    |   P    |           |");
                } 
            } else {
                if (principalLocation.getShortDescription() == "Principal's Office") {
                    System.out.println("|   P    |        |        |   Y    |           |");
                } else if (principalLocation.getShortDescription() == "Cafeteria") {
                    System.out.println("|        |   P    |        |   Y    |           |");
                } else {
                    System.out.println("|        |        |   P    |   Y    |           |");
                }
            }

        }

        System.out.println("|        |        |        |        |           |");
        System.out.println("------------------------------------------------");
    }

    private void movePrincipal() {
        if (move == 0) {
            Room nextRoom = principalLocation;
            Random rand = new Random();
            int x;
            switch (principalLocation.getShortDescription()) {
                case "Principal's Office":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Cafeteria":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Classroom 103":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Boy's Bathroom":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Girl's Bathroom":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Gym":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Teacher's Lounge":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Classroom 102":
                    nextRoom = principalLocation.getExit("Hallway");
                    break;
                case "Hallway A":
                    rand = new Random();
                    x = rand.nextInt(100);
                    if(x <= 50){ 
                        nextRoom = principalLocation.getExit("Up");
                    } else { 
                        nextRoom = principalLocation.getExit("Office"); 
                    }
                    break;
                case "Hallway B":
                    rand = new Random();
                    x = rand.nextInt(100);
                    if(x <= 25){ 
                        nextRoom = principalLocation.getExit("Classroom");
                    } else if(x <= 50) { 
                        nextRoom = principalLocation.getExit("Cafeteria"); 
                    } else if(x <= 75) { 
                        nextRoom = principalLocation.getExit("Up"); 
                    } else { 
                        nextRoom = principalLocation.getExit("Down"); 
                    }
                    break;
                case "Hallway C":
                    rand = new Random();
                    x = rand.nextInt(100);
                    if(x <= 25){ 
                        nextRoom = principalLocation.getExit("Classroom");
                    } else if(x <= 50) { 
                        nextRoom = principalLocation.getExit("TeachersLounge"); 
                    } else if(x <= 75) { 
                        nextRoom = principalLocation.getExit("Up"); 
                    } else { 
                        nextRoom = principalLocation.getExit("Down"); 
                    }
                    break;
                case "Hallway D":
                    rand = new Random();
                    x = rand.nextInt(100);
                    if(x <= 35){ 
                        nextRoom = principalLocation.getExit("B-Bathroom");
                    } else if(x <= 60) { 
                        nextRoom = principalLocation.getExit("Gym"); 
                    } else { 
                        nextRoom = principalLocation.getExit("Down"); 
                    }
                    break;

                    
            }
            principalLocation = nextRoom;
            move = rand.nextInt(3);
        } else {
            move--;
        }
    }

    private boolean inUpperRoom(Room passedRoom){
        String temp = passedRoom.getShortDescription();
        if (temp == "Classroom 102" || temp == "Teacher's Lounge" || temp == "Boy's Bathroom") {
            return true;
        }
        return false;
    }

    private boolean inMiddleRoom(Room passedRoom){
        String temp = passedRoom.getShortDescription();
        if (temp == "Hallway A" || temp == "Hallway B" || temp == "Hallway C" || temp == "Hallway D"|| temp == "Gym") {
            return true;
        }
        return false;
    }

    private boolean inLowerRoom(Room passedRoom){
        String temp = passedRoom.getShortDescription();
        if (temp == "Principal's Office" || temp == "Cafeteria" || temp == "Classroom 103" || temp == "Girl's Bathroom") {
            return true;
        }
        return false;
    }

    private void printSadFace(){
        System.out.println();
        System.out.println();
        System.out.println("   ---        ---");
        System.out.println("    |          | ");
        System.out.println();
        System.out.println("          |      ");
        System.out.println();
        System.out.println("     ---------- ");
        System.out.println("    |          |");
        System.out.println("    |          |");
        System.out.println();
    }

    private void interactWithTim(){
        String timString = "Items Needed: [ ";
        for(Item x : timList) {
            timString += x.getItem() + ", ";
        }
        timString += " ]";

        if (!mettim) {
            mettim = true;
            System.out.println("You walk into the Gym and see the school janitor.");
            System.out.println("'Hey, I see you want out of here. I'll cut you a deal.''");
            System.out.println("'It seems I have misplaced a few items. If you find them,'");
            System.out.println("'I'll give you the keys to get out of here.'");
            System.out.println(timString);

        } else {
            System.out.println("'Hey, found my stuff?''");
            System.out.println(timString);
        }

        if (gotTimsStuff()) {
            System.out.println("'Thank You.''");
            Item temp = timNPC.dropItem(keys.getItem());
            
            currentRoom.setItems(temp);
            for(Item x : timList) {
                player.dropItem(x.getItem());
            }
           
            gotStuff = true;

        } else {
            if (!gotStuff) {
                System.out.println("'Looks like you're missing some stuff.''");
                System.out.println(timString);
            }
        }
    }

    private boolean gotTimsStuff() {
        for(Item x : timList){
            if(!player.hasItem(x)){
                return false;
            }
        }
        return true;
    }
}