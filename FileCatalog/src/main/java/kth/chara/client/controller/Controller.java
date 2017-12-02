package kth.chara.client.controller;

import kth.chara.client.net.ServerConnection;
import kth.chara.common.FilesCatalog;

import java.util.concurrent.CompletableFuture;

/**
 * Connect the UI with net package.
 * Introduce separate threads for calling the remote processes in order to
 * prepare server for listening sockets, as well as to avoid client system stacking.
 */

public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    /**
     * Call upload remote method to check for unique filename and if so
     * start the listening server socket
     * @param filesCatalog A reference to the file catalog class in common package.
     * @param username The corresponding username that wants to upload.
     * @param filename The name of the file to upload.
     * @param size The size of the file to upload.
     * @param access The access of the file to upload.
     * @param permissions The permissions of the file to upload.
     */

    public void prepareUpload(FilesCatalog filesCatalog, String username, String filename,
                              long size, String access, String permissions){
        CompletableFuture.runAsync(() -> {
            try {
                filesCatalog.upload(username, filename, size, access, permissions);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Call the ServerConnection class to open a socket and send the file.
     * @param path The path of the file to upload.
     * @param filename The name of the file to upload.
     */

    public void finishUpload(String path, String filename) {
        try {
            serverConnection.sendFile(path, filename);
            System.out.println("Upload success!");
        } catch (Exception e) {
            System.out.println("Upload failed!");
        }
    }

    /**
     * Call download remote method to check if download is possible and
     * if so ask the server to send the file with a TCP socket.
     * @param filesCatalog A reference to the file catalog class in common package.
     * @param username The name of the corresponding user.
     * @param filename The name of the file to download.
     */

    public void prepareDownload(FilesCatalog filesCatalog, String username, String filename) {
        CompletableFuture.runAsync(() -> {
            try {
                filesCatalog.download(username, filename);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Finish downloading of the file by calling the ServerConnection class to open a
     * listening socket.
     */

    public void finishDownload(){
        try {
            serverConnection.receiveFile();
            System.out.println("Download success!");
        } catch (Exception e) {
            System.out.println("Download failed!");
        }
    }

}
