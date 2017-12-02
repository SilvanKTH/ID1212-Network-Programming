package kth.chara.common;


import java.io.Serializable;

/**
 * Handle all the input in client's console.
 * Implements Serializable method to convert its
 * state to a byte stream so that the byte stream can be
 * reverted back into a copy of the object.
 */

public class ProcessInput implements Serializable{
    private Commands commandType;
    private String username;
    private String password;
    private String filename;
    private String path;
    private String access;
    private String permissions;
    private boolean check;
    private boolean uploadReady;

    public ProcessInput(Commands commandType, String username, String password, String filename, String path, String access, String permissions, boolean check, boolean uploadReady){
        this.commandType = commandType;
        this.username= username;
        this.password =password;
        this.filename = filename;
        this.path = path;
        this.access = access;
        this.permissions = permissions;
        this.check = check;
        this.uploadReady = uploadReady;
    }

    public Commands getCommand(){return commandType;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public String getFileName() {return filename;}

    public String getPath() {return path;}

    public String getAccess() {return access;}

    public String getPermissions() {return permissions;}

    public Boolean checkCommand(){return check;}

    public Boolean readyToUpload(){return uploadReady;}
}
