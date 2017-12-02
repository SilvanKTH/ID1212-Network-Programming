package kth.chara.client.view;


import kth.chara.client.controller.Controller;
import kth.chara.common.FilesCatalog;
import kth.chara.common.ProcessInput;

import java.io.*;
import java.io.File;

/**
 * Implements Runnable for responsive UI.
 */

public class ClientUI implements Runnable{
    private FilesCatalog filesCatalog;
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean newInput = false;
    private boolean newLogin;
    private Controller controller;

    public void start(FilesCatalog filesCatalog){
        this.filesCatalog = filesCatalog;
        if (newInput) return;
        newInput = true;
        newLogin = false;
        controller = new Controller();
        new Thread(this).start();
    }

    @Override
    public void run() {
        welcomeMsg();
        while (newInput){
            try {
                System.out.print("> ");
                String Br = br.readLine();
                ProcessInput processInput = filesCatalog.processInput(Br);
                if (!processInput.checkCommand()){
                    printWrongCommand();
                } else {
                    switch (processInput.getCommand()){
                        case REGISTER:
                            try {
                                filesCatalog.register(processInput.getUsername(), processInput.getPassword());
                                System.out.println("Registration success! Login to continue!\n");
                            } catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            break;
                        case LOGIN:
                            try {
                                filesCatalog.login(processInput.getUsername(), processInput.getPassword());
                                loginProcess(processInput.getUsername());
                            } catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            break;
                        case QUIT:
                            System.out.println("Quit success!");
                            newInput = false;
                            break;
                        default:
                            System.out.println("The " + processInput.getCommand() +
                                    " command cannot be performed now!");
                            break;
                    }
                }
            } catch (Exception e){
                if (newInput) {
                    System.out.println("Failure with command processing");
                }
            }
        }
    }

    /**
     * Handles the state after logging in to the system.
     * @param name The name of the current client.
     */

    private void loginProcess(String name){
        printLoginMsg(name);
        newLogin = true;
        while (newLogin){
            System.out.print("> ");
            try {
                String Br = br.readLine();
                ProcessInput loginCmd = filesCatalog.processInput(Br);
                if (!loginCmd.checkCommand()){
                    printWrongCommandLogin();
                }else {
                    switch (loginCmd.getCommand()){
                        case UPLOAD: upload(loginCmd); break;
                        case DOWNLOAD: download(name, loginCmd.getFileName()); break;
                        case LIST: listFiles(name); break;
                        case DELETE: delete(name, loginCmd.getFileName()); break;
                        case STATUS: statusInfo(name, loginCmd.getFileName()); break;
                        case UNREGISTER: unregister(name); break;
                        case LOGOUT: logout(name); break;
                        case QUIT: quit(name); break;
                        default:
                            System.out.println("The " + loginCmd.getCommand() +
                                    " command cannot be performed now!");
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unregister(String username){
        try {
            filesCatalog.unregister(username);
            System.out.println("Unregister success!");
            newLogin = false;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void logout (String username){
        try {
            filesCatalog.logout(username);
            System.out.println("Logout success!");
            welcomeMsg();
            newLogin = false;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void quit(String username){
        try {
            filesCatalog.logout(username);
            System.out.println("Quit success!");
            newLogin = false;
            newInput = false;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Upload a file or change an existing file with TCP connection to server.
     * The controller will handle the process of preparing and finally opening a communication.
     * @param loginCmd The client's input after processing from server.
     */

    private void upload(ProcessInput loginCmd){
        if (new File(loginCmd.getPath()).isFile() && loginCmd.readyToUpload()){
            long size = new File(loginCmd.getPath()).length();
            controller.prepareUpload(filesCatalog, loginCmd.getUsername(),
                    loginCmd.getFileName(), size, loginCmd.getAccess(), loginCmd.getPermissions());
            controller.finishUpload(loginCmd.getPath(), loginCmd.getFileName());
        } else {
            System.out.println("Failed! An example of correct command is: " +
                    "\nupload \"C:\\Users\\chara\\Documents\\text.txt\" public write");
        }
    }

    /**
     * Download a file from server with TCP connection.
     * The controller will handle the process of preparing and finally opening a communication.
     * @param username The current username.
     * @param filename The filename to download.
     */

    private void download(String username, String filename){
        controller.prepareDownload(filesCatalog, username, filename);
        controller.finishDownload();
    }

    private void delete(String username, String filename){
        try {
            filesCatalog.delete(username,filename);
            System.out.println("Delete success!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * List all available files for the corresponding user.
     * @param username The username of the current client.
     */

    private void listFiles(String username){
        try {
            System.out.println(filesCatalog.listFiles(username));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Give info for a specific public file
     * @param username The username of the current client.
     * @param filename The filename to display info for.
     */

    private void statusInfo(String username, String filename){
        try {
            System.out.println(filesCatalog.fileInfo(username, filename));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //All the possible menus displayed to the current client.

    private void welcomeMsg(){
        System.out.println("\nWelcome to the File Catalog!\n" +
                "The commands of the system are:\n\n" +
                "register <username> <password>                    //create a new account//\n" +
                "login <username> <password>                       //login to the system//\n" +
                "quit                                              //to quit the system//\n");
    }

    private void printWrongCommand(){
        System.out.println("\nWrong command!\nThe correct commands are: " +
                "\n\nregister <username> <password>\n" +
                "login <username> <password>\nquit\n");
    }

    private void printLoginMsg(String name){
        System.out.println("\nHi " + name +"!\n" +
                "Available Commands:\n\n" +
                "upload <path> <private>                            //to upload a private file//\n" +
                "upload <path> <public> <write or read>             //to upload a public file//\n" +
                "download <filename>                                //to download a file//\n" +
                "list                                               //to view all the available files//\n" +
                "delete <filename>                                  //to delete a specific file//\n" +
                "status <filename>                                  //to check the status of a specific public file//\n" +
                "unregister                                         //to unregister your account//\n" +
                "logout                                             //to logout from catalog//\n" +
                "quit                                               //to exit the system//\n");
    }

    private void printWrongCommandLogin(){
        System.out.println("\nWrong command!\nThe correct commands are: " +
                "\n\nupload <path> <private>\nupload <path> <public> <write or read>" +
                "\ndownload <filename>\n" +
                "list\ndelete <filename>\nstatus <filename>\nunregister\nlogout\nquit\n");
    }
}
