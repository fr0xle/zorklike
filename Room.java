package zorklike;

import zorklike.Item;
import zorklike.Zorklike;
import zorklike.Furniture;
import zorklike.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Room {
    private String name;
    private String desc;
    private String extdesc;
    private Connection[] connections;
    private List<Furniture> furnl;
    public Room(String nm, String dc, String edc, Connection... connect) {
        name=nm;
        desc=dc;
        extdesc=edc;
        connections=connect;
        furnl=new ArrayList<Furniture>();
    }
    public Room() {
        name=null;
        desc=null;
        extdesc=null;
        connections=null;
        furnl=null;
    }
    public void addFurniture(Furniture... furn) {
        if (furn==null || furn.length==0) {
            furn=null;
        }
        else {
            for (Furniture fn : furn) {
                furnl.add(fn);
            }
        }
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return desc;
    }
    public String getExtDescription() {
        return extdesc;
    }
    public Connection[] getConnections() {
        return connections;
    }
    public List<Furniture> getFurnL() {
        return furnl;
    }
    public List<Item> getItemL() {
        List<Item> iteml = new ArrayList<Item>();
        for (Furniture furn : furnl) {
            List<Item> citeml = furn.getItemL();
            if (citeml!=null) {
                for (Item item : citeml) {
                    iteml.add(item);
                }
            }
        }
        return iteml;
    }
    public boolean getInfo(Connection[] connects) {
        List<String> iteml = new ArrayList<String>();
        for (Furniture furn : furnl) {
            List<Item> citeml = furn.getItemL();
            String name = furn.getName();
            String desc = furn.getDescription();
            if (furn.isOpen()) {
                if (citeml!=null) {
                    List<String> tempNameStorage = new ArrayList<String>();
                    for (Item it : citeml) {
                        String itnm = it.getName(); 
                        List<String> aOrAn = new ArrayList<String>(Arrays.asList(itnm.split("")));
                        if (aOrAn.get(0).toLowerCase().equals("a")||aOrAn.get(0).toLowerCase().equals("e")||aOrAn.get(0).toLowerCase().equals("i")||aOrAn.get(0).toLowerCase().equals("o")||aOrAn.get(0).toLowerCase().equals("u")) {
                            tempNameStorage.add("an " + itnm);
                        }
                        else {
                            tempNameStorage.add("a " + itnm);
                        }
                    }
                    if (tempNameStorage.size()>1) {
                        tempNameStorage.set(tempNameStorage.size()-1,"and " + (tempNameStorage.get(tempNameStorage.size()-1)));
                    }
                    if (tempNameStorage.size()>2) {
                        if (furn.isContainer()) {
                            iteml.add("in the " + furn.getName() + " there is " + String.join(", ",tempNameStorage) + ".");
                        }
                        else {
                            iteml.add("on the " + furn.getName() + " there is " + String.join(", ",tempNameStorage) + ".");
                        }
                    }
                    else {
                        if (furn.isContainer()) {
                            iteml.add("in the " + furn.getName() + " there is " + String.join(" ",tempNameStorage) + ".");
                        }
                        else {
                            iteml.add("on the " + furn.getName() + " there is " + String.join(" ",tempNameStorage) + ".");
                        }
                    }
                }
                else {
                    iteml.add(desc);
                }
            }
            else {
                iteml.add(desc);
            }
        }
        if (connects!=null) {
            boolean possible = false;
            for (Connection connect : connects) {
                if (connect.getName().equalsIgnoreCase(name)) {
                    if (connect.isOpen()) {
                        possible = true;
                    }
                    else {
                        System.out.println("Sorry, that door's closed.");
                        return false;
                    }
                }
            }
            if (!possible) {
                System.out.println("Can't do that, sorry bud.");
                return false;
            }
        }
        String[] namearr = name.split("");
        namearr[0]=namearr[0].toUpperCase();
        String nameCap = String.join("",namearr);
        if (connects==null) {
            System.out.println("You scan the " + nameCap + " for items:");
        }
        else {
            System.out.println("You peer into the " + nameCap + " for items:");
        }
        if (iteml==null||iteml.size()==0) {
            System.out.println("  Nothing...");
        }
        else {
            for (String item : iteml) {
                String[] upp = item.split("");
                upp[0] = upp[0].toUpperCase();
                System.out.println("   " + String.join("",upp));
            }
        }
        if (!(connections.length==0||connections==null)) {
            List<String> listOfConnections = new ArrayList<String>();
            for (Connection connect : connections) {
                String side = connect.getSide();
                String[] upp = connect.getName().split("");
                upp[0] = upp[0].toUpperCase();
                String nam = String.join("",upp);
                if (side.equals("front")) {
                    if (connects==null) {
                        listOfConnections.add("a door in front of you leads to the " + nam);
                    }
                    else {
                        listOfConnections.add("a door in the front");
                    }
                }
                else if (side.equals("back")) {
                    if (connects==null) {
                        listOfConnections.add("a door behind you leads to the " + nam);
                    }
                    else {
                        listOfConnections.add("a door in in the back");
                    }
                }
                else if (side.equals("left")) {
                    if (connects==null) {
                        listOfConnections.add("a door to your left leads to the " + nam);
                    }
                    else {
                        listOfConnections.add("a door on the left");
                    }
                }
                else if (side.equals("right")) {
                    if (connects==null) {
                        listOfConnections.add("a door to your right leads to the " + nam);
                    }
                    else {
                        listOfConnections.add("a door on the right");
                    }
                }
            }
            if (connects==null) {
                String[] upper = listOfConnections.get(0).split("");
                upper[0] = upper[0].toUpperCase();
                listOfConnections.set(0,String.join("",upper));
            }

            if (listOfConnections.size()!=1) {
                List<String> andFix = new ArrayList<String>(Arrays.asList(listOfConnections.get(listOfConnections.size()-1).split(" ")));
                andFix.add(0,"and");
                andFix.add(andFix.size(),"\b.");
                listOfConnections.set(listOfConnections.size()-1,String.join(" ",andFix));
            }
            String fin = null;
            if (listOfConnections.size()==2) {
                fin = String.join(" ",listOfConnections);
            }
            else if (listOfConnections.size()>2) {
                fin = String.join(", ",listOfConnections);
            }
            else if (listOfConnections.size()==1) {
                fin = listOfConnections.get(0) + ".";
            }
            if (connects!=null) {
                String[] a = fin.split(" ");
                List<String> thereIs = new ArrayList<String>(Arrays.asList(a));
                thereIs.add(0,"There is");
                fin = String.join(" ", thereIs);
            }
            if (fin!=null) {
                System.out.println(fin);
                listOfConnections.clear();
            }
        }
        else {
            System.out.println("Oddly, the room you are in is connected to " + Zorklike.italics + "no other room..." + Zorklike.resetFormatting + " Strange. How did you even get here??");
        }
        return true;
    }
    public boolean getFurnInfo(Furniture furniture) {
        List<String> iteml = new ArrayList<String>();
        for (Furniture furn : furnl) {
            List<Item> citeml = furn.getItemL();
            String name = furn.getName();
            String extdesc = furn.getExtendedDescription();

        }
        return true;
    }
}
