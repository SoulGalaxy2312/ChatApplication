package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import server.models.Message;

public class Server {
    private final int SERVER_PORT = 1234;

    ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server is running at port 1234");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("An user enters");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        private BufferedInputStream bis;
        
        private BufferedOutputStream bos;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.bis = new BufferedInputStream(clientSocket.getInputStream());
            this.bos = new BufferedOutputStream(clientSocket.getOutputStream());                
        }

        @Override public void run() { 
            try { 
                byte[] lengthBuffer = new byte[4]; 
                int bytesRead;
                System.out.println("Waiting for messages...");

                while ((bytesRead = bis.read(lengthBuffer)) != -1) { 
                    int totalBytesRead = bytesRead; 
                    while (totalBytesRead < 4) { 
                        bytesRead = bis.read(lengthBuffer, totalBytesRead, 4 - totalBytesRead); 
                        if (bytesRead == -1) { 
                            System.out.println("Stream closed prematurely");
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
                    byte messageType = messageBuffer[0];
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(messageBuffer, 1, messageLength - 1);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                    if (messageType == 1) {
                        Message message = (Message) objectInputStream.readObject(); 
                        System.out.println(message);
                    } 
                }
                System.out.println("Not enter the while loop");
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        }
    }

    public static void main(String[] args) {
        Server server = new Server();    
    }
}
