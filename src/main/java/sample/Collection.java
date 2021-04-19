package sample;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private List<ProjectCollectionTabController.Group> groups = new ArrayList<>();
    private int groupProg;
    private int groupTotal;
    private int pageTotal;
    private int itemProg;
    private int itemTotal;
    private String name;

    public Collection(String name) {
        this.name = name;
    }

    public List<ProjectCollectionTabController.Group> getGroups() {
        return groups;
    }

    public void setGroups(List<ProjectCollectionTabController.Group> groups) {
        this.groups = groups;
    }

    public int getGroupProg() {
        return groupProg;
    }

    public void setGroupProg(int groupProg) {
        this.groupProg = groupProg;
    }

    public int getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(int groupTotal) {
        this.groupTotal = groupTotal;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getItemProg() {
        return itemProg;
    }

    public void setItemProg(int itemProg) {
        this.itemProg = itemProg;
    }

    public int getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
