package kth.chara.client.net;


import java.io.*;
import java.net.Socket;

/**
 * Handles the communication with TCP sockets for file exchanging with the server.
 */

public class ServerConnection {

    public ServerConnection(){}

    /**
     * Connect and send file to server.
     * @param path The filepath of the file to be send.
     * @throws IOException If an error occur with the communication.
     */

    public void sendFile(String path, String filename) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);

        DataInputStream din=new DataInputStream(socket.getInputStream());
        DataOutputStream dout=new DataOutputStream(socket.getOutputStream());

        dout.writeUTF(filename);
        dout.flush();

        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte b[]=new byte [1024];
        int read;

        while((read = fileInputStream.read(b)) != -1){
            dout.write(b, 0, read);
            dout.flush();
        }

        fileInputStream.close();
        dout.flush();
        din.close();
        socket.close();
    }

    /**
     * Connect and receive a file from the server.
     * @throws IOException If an error occur with the communication.
     */

    public void receiveFile() throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);

        DataInputStream din=new DataInputStream(socket.getInputStream());
        DataOutputStream dout=new DataOutputStream(socket.getOutputStream());

        String filename = din.readUTF();

        byte b[]=new byte [1024];
        FileOutputStream fos=new FileOutputStream(new File("src/ClientDirectory/" + filename),true);
        long bytesRead;
        do {
            bytesRead = din.read(b, 0, b.length);
            fos.write(b,0,b.length);
        } while(!(bytesRead<1024));

        fos.close();
        dout.close();
        socket.close();
    }

}
