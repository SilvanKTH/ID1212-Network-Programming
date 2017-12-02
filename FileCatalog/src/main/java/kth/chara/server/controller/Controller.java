package kth.chara.server.controller;

import kth.chara.common.FilesCatalog;
import kth.chara.common.ProcessInput;
import kth.chara.server.model.*;
import kth.chara.server.net.FileTransfer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementations of the file's catalog remote methods,
 * this is the only server class that can be called remotely.
 */

public class Controller extends UnicastRemoteObject implements FilesCatalog {
    private final ClientsSystem clientsSystem;
    private final ClientsInputProcess clientsInputProcess;
    private final FilesSystem filesSystem;

    public Controller() throws RemoteException {
        super(); //Creates and exports a new UnicastRemoteObject object using an anonymous port.
        clientsSystem = new ClientsSystem();
        clientsInputProcess = new ClientsInputProcess();
        filesSystem = new FilesSystem();
    }

    @Override
    public ProcessInput processInput(String command) throws RemoteException, ClientException {
        return clientsInputProcess.processInput(command);
    }

    @Override
    public void register(String username, String password) throws RemoteException, ClientException {
        clientsSystem.register(username, password);
    }

    @Override
    public void unregister(String username) throws RemoteException, ClientException {
        clientsSystem.unregister(username);
    }

    @Override
    public void login(String username, String password) throws RemoteException, ClientException {
        clientsSystem.login(username, password);
    }

    @Override
    public void logout(String username) throws RemoteException, ClientException {
        clientsSystem.logout(username);
    }

    @Override
    public void upload(String username, String filename, long size, String access, String permissions) throws IOException, FileException {
        ClientHandler clientHandler = clientsSystem.getClient(username);
        if (filesSystem.upload(filename, size, clientHandler, access, permissions)){
            FileTransfer fileTransfer = new FileTransfer();
            fileTransfer.receiveFile();
        } else {
            throw new FileException("The file cannot be changed!");
        }
    }

    @Override
    public void download(String username, String filename) throws IOException, FileException {
        ClientHandler clientHandler = clientsSystem.getClient(username);
        if (filesSystem.download(filename, clientHandler)){
            FileTransfer fileTransfer = new FileTransfer();
            fileTransfer.sendFile(filename);
        }else {
            throw new FileException("The file is either private or not in server directory!");
        }
    }

    @Override
    public void delete(String username, String filename) throws RemoteException, FileException {
        ClientHandler clientHandler = clientsSystem.getClient(username);
        if (!filesSystem.delete(filename, clientHandler)){
            throw new FileException("The file is either private or public with read permissions or not in server directory!");
        }
    }

    @Override
    public String listFiles(String username) throws RemoteException, FileException {
        ClientHandler clientHandler = clientsSystem.getClient(username);
        return filesSystem.listFiles(clientHandler);
    }

    @Override
    public String fileInfo(String username, String filename) throws RemoteException, FileException {
        ClientHandler clientHandler = clientsSystem.getClient(username);
        return filesSystem.fileInfo(filename, clientHandler);
    }
}
