package kth.chara.server.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handle the communication with TCP sockets for file exchanging with a client.
 */

public class FileTransfer{

    public FileTransfer(){}

    /**
     * The method for receiving a file.
     * @throws IOException In case of IO error.
     */

    public void receiveFile() throws IOException {
        ServerSocket serverSocket=new ServerSocket(8080);
        System.out.println("Waiting...");
        Socket socket = serverSocket.accept();

        DataInputStream din=new DataInputStream(socket.getInputStream());
        DataOutputStream dout=new DataOutputStream(socket.getOutputStream());

        String filename = din.readUTF();

        byte b[]=new byte [1024];
        FileOutputStream fos=new FileOutputStream(new File("src/ServerDirectory/" + filename),true);
        long bytesRead;
        do {
            bytesRead = din.read(b, 0, b.length);
            fos.write(b,0,b.length);
        } while(!(bytesRead<1024));

        fos.close();
        dout.close();
        socket.close();
        serverSocket.close();
    }

    /**
     * The method for sending a file.
     * @param filename The name of the file to send.
     * @throws IOException In case of IO error.
     */

    public void sendFile(String filename) throws IOException{
        ServerSocket serverSocket=new ServerSocket(8080);
        System.out.println("Waiting...");
        Socket socket = serverSocket.accept();

        DataInputStream din=new DataInputStream(socket.getInputStream());
        DataOutputStream dout=new DataOutputStream(socket.getOutputStream());

        System.out.println("Sending File: " + filename);
        dout.writeUTF(filename);
        dout.flush();

        File file = new File("src/ServerDirectory/" + filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        byte b[] = new byte[1024];

        int read;

        while ((read = fileInputStream.read(b)) != -1){
            dout.write(b, 0, read);
            dout.flush();
        }
        fileInputStream.close();
        dout.flush();
        din.close();
        socket.close();
        serverSocket.close();
    }
}
