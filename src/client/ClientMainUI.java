package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import server.models.Message;

public class ClientMainUI {
    private final String HOST = "localhost";

    private final int SERVER_PORT = 1234;

    private Socket clientSocket;

    private BufferedInputStream bis;

    private BufferedOutputStream bos;

    public ClientMainUI() {
        try { 
            connectServer(); 
            String input = ""; 
            Scanner scanner = new Scanner(System.in); 
            while (!input.equals("quit")) { 
                System.out.print("Enter message: "); 
                input = scanner.nextLine(); 
                Message message = new Message(); 
                message.setSender("tricloc10"); 
                message.setReceiver("ngotri"); 
                message.setType(1); 
                message.setContent(input.getBytes()); 
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream); 
                objectOutputStream.writeObject(message); 
                objectOutputStream.flush(); 
                
                byte[] messageBytes = byteArrayOutputStream.toByteArray(); 
                int messageLength = messageBytes.length + 1; 
                byte[] lengthBytes = ByteBuffer.allocate(4).putInt(messageLength).array(); 
                bos.write(lengthBytes); 
                bos.write(1);
                bos.write(messageBytes); 
                bos.flush(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    public void connectServer() throws Exception {        
        clientSocket = new Socket(HOST, SERVER_PORT);    

        this.bis = new BufferedInputStream(clientSocket.getInputStream());
        this.bos = new BufferedOutputStream(clientSocket.getOutputStream());

        new Thread(new Runnable() { 
            @Override 
            public void run() { 
                try { 
                    byte[] lengthBuffer = new byte[4]; 
                    int bytesRead; 
                    while ((bytesRead = bis.read(lengthBuffer)) != -1) { 
                        int totalBytesRead = bytesRead; 
                        while (totalBytesRead < 4) { 
                            bytesRead = bis.read(lengthBuffer, totalBytesRead, 4 - totalBytesRead); 
                            if (bytesRead == -1) { 
                                throw new IOException("Stream closed prematurely"); 
                            } 
                            totalBytesRead += bytesRead; 
                        } 
                        int messageLength = ByteBuffer.wrap(lengthBuffer).getInt(); 
                        byte[] messageBuffer = new byte[messageLength]; 
                        totalBytesRead = 0; 
                        while (totalBytesRead < messageLength) { 
                            bytesRead = bis.read(messageBuffer, totalBytesRead, messageLength - totalBytesRead); 
                            if (bytesRead == -1) { 
                                throw new IOException("Stream closed prematurely"); 
                            } 
                            totalBytesRead += bytesRead; 
                        } 
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(messageBuffer); 
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); 
                        if (messageBuffer[0] == 1) { 
                                Message message = (Message) objectInputStream.readObject(); 
                            } 
                        } 
                    } 
                    catch (Exception e) { e.printStackTrace(); } } }).start();

    }
}