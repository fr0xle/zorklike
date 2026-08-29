package zorklike;

import zorklike.Zorklike;
import zorklike.Room;
import zorklike.Item;
import java.util.List;
import java.util.ArrayList;

public class Dictionary {
    private String[] roomNames;
    private String[] itemNames;
    private String[] furnNames;
    public static String[] actions = {"peer","list","go","move","find","search","look","examine","take","grab","get","unlock","open","close","drop","foreward","front","forewards","right","left","back","backward","backwards","inventory","backpack","around","use","xyzzy"};
    public static String[] useless = {"into","to","an","a","me","my","i","your","you","the","mine","at","the"};
    public static String[] splitters = {"with","in","inside","into","on","onto","off"};
    public static String[] flags = {"and",",","all"};
	public static String[] movement = {"go","move","foreward","front","forewards","right","left","back","backward","backwards"};
    public static String[] directions = {"foreward","forewards","front","right","left","backward","backwards","back"};
	public static String[] searching = {"find","search","look","examine","peer"};
	public static String[] obtaining = {"take","grab","get"};
	public static String[] objectInteraction = {"open","close","search"};
	public static String[] inventoryActions = {"drop","inventory","backpack"};
    public Dictionary() {
        roomNames = new String[Zorklike.rooms.size()];
        for (int i=0;i<Zorklike.rooms.size();i++) {
            roomNames[i] = Zorklike.rooms.get(i).getName();
        }
        int max = 0;
        for (Room room : Zorklike.rooms) {
            List<Item> iteml = room.getItemL();
            if (iteml!=null) {
                max += iteml.size();
            }
        }
        itemNames = new String[max];
        int x = 0;
        for (Room room : Zorklike.rooms) {
            List<Item> iteml = room.getItemL();
            if (iteml!=null) {
                for (int i=0;i<iteml.size();i++) {
                    itemNames[x] = iteml.get(i).getName();
                    x++;
                }
            }
        }
        int fmax = 0;
        for (Room room : Zorklike.rooms) {
            List<Furniture> furnl = room.getFurnL();
            if (furnl!=null) {
                fmax += furnl.size();
            }
        }
        furnNames = new String[fmax];
        int y = 0;
        for (Room room : Zorklike.rooms) {
            List<Furniture> furnl = room.getFurnL();
            if (furnl!=null) {
                for (int i=0;i<furnl.size();i++) {
                    furnNames[y] = furnl.get(i).getName();
                    y++;
                }
            }
        }
	} 
	public boolean searchRooms(String roomName) { 
		for (int i=0;i<roomNames.length;i++) { 
			if (Zorklike.containsExactWord(roomName,roomNames[i])) { 
				return true; 
			}
        }
        return false;
    }
    public boolean searchItems(String itemName) {
        for (int i=0;i<itemNames.length;i++) {
            if (Zorklike.containsExactWord(itemName,itemNames[i])) {
                return true;
            }
        }
        return false;
    }
    public boolean searchFurniture(String furnName) {
        for (int i=0;i<furnNames.length;i++) {
            if (Zorklike.containsExactWord(furnName,furnNames[i])) {
                return true;
            }
        }
        return false;
    }
    public String[] getItemNames() {
        return itemNames;
    }
    public String[] getRoomNames() {
        return roomNames;
    }
}
