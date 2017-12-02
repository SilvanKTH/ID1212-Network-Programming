package kth.chara.client.startup;

import kth.chara.client.view.ClientUI;
import kth.chara.common.FilesCatalog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Start the client by returning the stub, for the remote object associated
 * with our implementation.
 */

public class Main {
    public static void main(String[] args){
        try{
            FilesCatalog filesCatalog = (FilesCatalog) Naming.lookup(FilesCatalog.FILESYSTEM_NAME_IN_REGISTRY);
            new ClientUI().start(filesCatalog);
        } catch (NotBoundException | MalformedURLException | RemoteException ex){
            System.out.println("Problem with starting filesystem in client!");
        }
    }
}
