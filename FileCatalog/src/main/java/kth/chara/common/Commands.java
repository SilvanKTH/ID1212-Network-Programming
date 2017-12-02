package kth.chara.common;

/**
 * All possible client's commands
 */

public enum Commands {
    //to create a new account
    REGISTER,
    //to unregister an account
    UNREGISTER,
    //to login to the system
    LOGIN,
    //to quit or logout of the system
    LOGOUT,
    //to upload or change a file
    UPLOAD,
    //to download a file
    DOWNLOAD,
    //to list files from catalog
    LIST,
    //to delete a file
    DELETE,
    //to retrieve specific info for a file
    STATUS,
    //to quit the catalog
    QUIT
}
