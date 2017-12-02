package kth.chara.server.model.fileHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the lists for file information purposes.
 */

public class StatusInfo {
    private String filename;
    private String owner;
    private List<String> updateList;
    private List<String> deleteList;
    private List<String> retrieveList;

    StatusInfo(String filename, String owner){
        this.filename = filename;
        this.owner = owner;
        this.updateList = new ArrayList<>();
        this.deleteList = new ArrayList<>();
        this.retrieveList = new ArrayList<>();
    }

    public String getFilename() {return filename;}

    String getOwner() {return owner;}

    void setUpdateList(String username){
        updateList.add(username);
    }

    void setDeleteList(String username){
        deleteList.add(username);
    }

    void setRetrieveList(String username){
        retrieveList.add(username);
    }

    List<String> getUpdateList() {
        return updateList;
    }

    List<String> getDeleteList() {
        return deleteList;
    }

    List<String> getRetrieveList() {
        return retrieveList;
    }
}
