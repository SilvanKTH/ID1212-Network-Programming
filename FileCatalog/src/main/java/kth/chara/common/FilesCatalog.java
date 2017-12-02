package kth.chara.common;

import kth.chara.server.model.clientHandling.ClientException;
import kth.chara.server.model.fileHandling.FileException;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Specifies the file's system remote methods.
 */

public interface FilesCatalog extends Remote {

    /**
     * The default URI of the file system server in the RMI registry.
     */

    String FILESYSTEM_NAME_IN_REGISTRY = "filesystem";

    /**
     * Processing of client input at the console from the server.
     * @param command The corresponding command of the client.
     * @return ProcessInput
     * @throws RemoteException If unable to complete the RMI call.
     * @throws ClientException If a client exception error occurs.
     */
    ProcessInput processInput(String command) throws RemoteException, ClientException;

    /**
     * Registers a new user with the specified username and password.
     *
     * @param username The client's username.
     * @param password The client's password.
     * @throws RemoteException  If unable to complete the RMI call.
     */

    void register(String username, String password) throws RemoteException, ClientException;

    /**
     * Unregister a client from the system.
     * @param username The username of the client.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws ClientException If a client error occurs.
     */

    void unregister(String username) throws RemoteException, ClientException;

    /**
     * Login a user to the system
     * @param username The username of the client.
     * @param password The password of the client.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws ClientException If a client error occurs.
     */

    void login(String username, String password) throws RemoteException, ClientException;

    /**
     * Logout a user from the system.
     * @param username The username of the client.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws ClientException If a client error occurs.
     */

    void logout(String username) throws RemoteException, ClientException;

    /**
     * Upload check before actual uploading of a file to the system.
     * @param username The username of the client that wants to upload.
     * @param filename The name of the file to upload.
     * @param size The size of the file to upload.
     * @param access The access of the file to upload.
     * @param permissions The permissions of the file to upload.
     * @throws IOException In case of IO error.
     * @throws FileException In case of file exception error.
     */

    void upload(String username, String filename, long size, String access, String permissions) throws IOException, FileException;

    /**
     * Download check before actual downloading of a file.
     * @param username The username of the client that wants to download.
     * @param filename The name of the file to download.
     * @throws IOException In case of IO error.
     * @throws FileException In case of file exception error.
     */

    void download(String username, String filename) throws IOException, FileException;

    /**
     * Delete a file.
     * @param username The username of the client that wants to delete the file.
     * @param filename The name of the file to delete.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws FileException In case of file exception error.
     */

    void delete(String username, String filename) throws RemoteException, FileException;

    /**
     * List the appropriate files for the corresponding user.
     * @param username The username of the client that asked for the list.
     * @return A string will all the appropriate files.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws FileException In case of file exception error.
     */

    String listFiles(String username) throws RemoteException, FileException;

    /**
     * Status info (retrieves, updates, deletes) about a specific file.
     * @param username The username of the client that asks for the file info.
     * @param filename The name of the file the client is interested in.
     * @return A string will usernames that performed an action to the file.
     * @throws RemoteException If unable to complete the RMI call.
     * @throws FileException In case of file exception error.
     */

    String fileInfo(String username, String filename) throws RemoteException, FileException;
}