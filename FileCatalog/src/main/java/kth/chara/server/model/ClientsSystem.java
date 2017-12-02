package kth.chara.server.model;

import kth.chara.server.integration.FileSystemDAO;

import java.util.*;

/**
 * Handles all the client operations.
 */

public class ClientsSystem {
    private FileSystemDAO fileSystemDAO;
    private final List<ClientHandler> activeClients = new ArrayList<>();

    public ClientsSystem(){
        fileSystemDAO = FileSystemDAO.getThis();
    }

    public void register(String username, String password) throws ClientException {
        ClientHandler clientHandler = fileSystemDAO.findCLientByName(username);
        if (clientHandler == null) {
            fileSystemDAO.register(new ClientHandler(username, password));
        } else {
            throw new ClientException("Client with username: " + username + " already exists! " +
                    "Try a different one!");
        }
    }

    public void unregister(String username) throws ClientException {
        ClientHandler clientHandler = activeClient(username); //client must be currently logged in to unregister
        if (clientHandler != null){
            fileSystemDAO.unregister(username);
        } else {
            throw new ClientException("Unregister failure! Client was not active!");
        }
    }

    public void login(String username, String password) throws ClientException {
        ClientHandler clientHandler = fileSystemDAO.findCLientByName(username);
        if (clientHandler != null) {
            if (!clientHandler.getPassword().equals(password)){
                throw new ClientException("Wrong password! Try again!");
            } else {
                activeClients.add(clientHandler);
            }
        } else {
            throw new ClientException("The username doesn't exist! Try again!");
        }
    }

    public void logout(String username) throws ClientException {
        ClientHandler clientHandlerRemove = activeClient(username); //client must be currently logged in to logout
        if (clientHandlerRemove != null){
            activeClients.remove(clientHandlerRemove);
        } else {
            throw new ClientException("Logout failure! Client was not active!");
        }
    }

    /**
     * A method to check if the client is currently logged in.
     *
     * @param username The username to check
     * @return <code>null</code> if the client is not logged in.
     *         <code>ClientHanlder</code> if the client is logged in.
     */

    private ClientHandler activeClient(String username){
        ClientHandler client = null;
        for (ClientHandler clientHandler : activeClients){
            if (clientHandler.getUsername().equals(username)){
                client = clientHandler;
                break;
            }
        }
        return client;
    }

    public ClientHandler getClient(String username) {
        return activeClient(username);
    }
}
