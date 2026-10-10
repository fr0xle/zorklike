package zorklike;

import java.util.List;
import java.util.ArrayList;

public class Dictionary {
    private String[] roomNames;
    private String[] itemNames;
    private String[] furnNames;
    public static String[] actions = {"peer","list","go","move","find","search","look","examine","take","grab","get","unlock","open","close","drop","foreward","front","forewards","right","left","back","backward","backwards","inventory","backpack","around","use","xyzzy"};
    public static String[] useless = {"into","to","an","a","me","my","i","your","you","the","mine","at","the"};
    public static String[] splitters = {"with","in","inside","into","on","onto","off","open"};
    public static String[] flags = {"and",","};
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
    public int searchItems(ArrayList<String> itemName) {
			if (itemName==null||itemName.isEmpty()) {
				return 0;
			}

			if (itemName.size()>1) {
				for (String item : itemName) {
					boolean itemFound = false;
					for (int i=0;i<itemNames.length;i++) {
						if (itemNames[i]!=null && Zorklike.containsExactWord(item, itemNames[i])) {
							itemFound = true;
							break;
						}
						else {
							itemFound = false;
							break;
						}
					}
					if (!itemFound) {
						return 2;
					}
				}
				return 1;
			}
			else {
				String phrase = itemName.get(0);
				for (int i=0;i<itemNames.length;i++) {
					if (itemNames[i]!=null && Zorklike.containsExactWord(phrase,itemNames[i])) {
						return 1;
					}
				}
			}
			return 0;
		//2 for trying to open more than one things
		//3 for trying to open something with something else that can be opened
    //    for (int i=0;i<itemNames.length;i++) {
		//			if (itemName.size() > 1) {
		//				for (int y=0;y<itemName.size();y++) {
		//					if (!Zorklike.containsExactWord(itemName.get(y),itemNames[i])) {
		//						return 2;
		//					}
		//				}
		//				return 1;
		//			}
		//			else {
    //        if (Zorklike.containsExactWord(itemName.get(0),itemNames[i])) {
    //            return 1;
    //        }
		//			}
    //    }
    //    return 0;
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
