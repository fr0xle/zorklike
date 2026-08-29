package zorklike;

import zorklike.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public class Furniture {
    private String name;
    private String desc;
    private String extdesc;
    private boolean isContainer;
    private boolean isOpen;
    private List<String> requirements;
    private List<Item> iteml;
    public Furniture(String nm,String ds,String exds,boolean isc,boolean iso,Item... item) {
        name=nm;
        desc=ds;
        extdesc=exds;
        isContainer=isc;
        if (isc) {
            isOpen=iso;
        }
        else {
            isOpen=true;
        }
        iteml=new ArrayList<Item>(Arrays.asList(item));
        if (isc) {
            requirements=new ArrayList<String>();
        }
        else {
            requirements=null;
        }
    }
    public Furniture() {
        name=null;
        desc=null;
        extdesc=null;
        isContainer=false;
        isOpen=false;
        requirements=null;
    }
    public Furniture addRequirements(String... req) {
        if (req==null || req.length==0) {
            requirements=null;
        }
        else {
            for (String rq : req) {
                requirements.add(rq);
            }
        }
        return this;
    }
    public void removeItem(String item) {
        if (iteml.contains(item)) {
            iteml.remove(item);
        }
    }
    public boolean useItem(String item) {
        if (requirements.contains(item)) {
            requirements.remove(item);
            if (requirements.size()<=0) {
                requirements=null;
                isOpen=true;
            }
            return true;
        }
        if (requirements.size()<=0) {
            requirements=null;
            isOpen=true;
        }
        return false;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return desc;
    }
    public String getExtendedDescription() {
        return extdesc;
    }
    public boolean isContainer() {
        return isContainer;
    }
    public boolean isOpen() {
        return isOpen;
    }
    public List<Item> getItemL() {
        if (iteml != null) {
            if (iteml.size() == 0) {
                iteml = null;
            }
        }
        return iteml;
    }
    public List<String> getRequirements() {
        return requirements;
    }
}