package kth.chara.server.startup;

import kth.chara.common.FilesCatalog;
import kth.chara.server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Start the server and create a remote object registry
 * that accepts calls on a specific port.
 */

public class Server {

    public static void main(String args[]){
        try{
            Server server = new Server();
            server.startRMI();
            System.out.println("File Catalog server started!");
        }
        catch(RemoteException e) {
            System.out.println("File Catalog server failed to start!");
            e.printStackTrace();
        }
    }

    private void startRMI() throws RemoteException{
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        try {
            Naming.rebind(FilesCatalog.FILESYSTEM_NAME_IN_REGISTRY, new Controller());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
