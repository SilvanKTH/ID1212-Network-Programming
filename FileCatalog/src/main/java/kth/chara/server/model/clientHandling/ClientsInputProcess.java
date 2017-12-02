package kth.chara.server.model.clientHandling;

import kth.chara.common.Commands;
import kth.chara.common.ProcessInput;
import kth.chara.server.model.clientHandling.ClientException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handle all the commands of the client input in console.
 */

public class ClientsInputProcess {
    private Commands commandType;
    private String username;
    private String password;
    private String filename;
    private String path;
    private String access;
    private String permissions;
    private float size;
    private boolean check;
    private boolean uploadReady;

    public ClientsInputProcess(){}

    public ProcessInput processInput(String command) throws ClientException {
        String[] words = command.split("\\s");//splits the string based on whitespace
        switch (words[0].toUpperCase()){
            case "REGISTER":
                commandType = Commands.REGISTER;
                setCredentials(words);
                break;
            case "UNREGISTER":
                commandType = Commands.UNREGISTER;
                check = words.length == 1;
                break;
            case "LOGIN":
                commandType = Commands.LOGIN;
                setCredentials(words);
                break;
            case "LOGOUT":
                commandType = Commands.LOGOUT;
                check = words.length == 1;
                break;
            case "UPLOAD":
                commandType = Commands.UPLOAD;
                extractFileInfo(command);
                break;
            case "DOWNLOAD":
                commandType = Commands.DOWNLOAD;
                setFileName(words);
                break;
            case "LIST":
                commandType = Commands.LIST;
                check = words.length == 1;
                break;
            case "DELETE":
                commandType = Commands.DELETE;
                setFileName(words);
                break;
            case "STATUS":
                commandType = Commands.STATUS;
                setFileName(words);
                break;
            case "QUIT":
                commandType = Commands.QUIT;
                check = words.length == 1;
                break;
            default:
                check = false;
                break;
        }
        return new ProcessInput(commandType, username, password, filename, path, access, permissions, check, uploadReady);
    }

    /**
     *Set the username and password of the client
     * @param creds The input of the user.
     */

    private void setCredentials(String[] creds){
        if (creds.length != 3){
            check = false;
        } else {
            username = creds[1];
            password = creds[2];
            check = true;
        }
    }

    /**
     * Set the name of the file to download, modify, and get status
     * @param info The input of the user.
     */

    private void setFileName(String[] info){
        if (info.length != 2){
            check = false;
        } else {
            filename = info[1];
            check = true;
        }
    }

    /**
     * Check the correct format of uploading a file
     * @param info The input of the user.
     */

    private void extractFileInfo(String info) throws ClientException {
        try {
            String[] splitStringInfo = info.split("\"");
            path = splitStringInfo[1].replace("\\", "/");
            Path p = Paths.get(path);
            filename = p.getFileName().toString();
            String[] temp = splitStringInfo[2].split("\\s");
            if (temp.length == 2){
                if (temp[1].equals("private")){
                    access = "private";
                    permissions = "write";
                    check = true;
                    uploadReady = true;
                } else {
                    uploadReady = false;
                    check = false;
                }
            } else if (temp.length == 3){
                if (temp[1].equals("public") && (temp[2].equals("write")
                        || temp[2].equals("read"))){
                    access = "public";
                    permissions = temp[2];
                    check = true;
                    uploadReady = true;
                } else {
                    check = false;
                    uploadReady = false;
                }
            } else {
                uploadReady = false;
                check = false;
            }
        }
        //giving empty values to avoid null exceptions.
        catch (Exception e){
            uploadReady = false;
            check = true;
            access = "";
            permissions = "";
            path = "";
            filename = "";
        }
    }

}
