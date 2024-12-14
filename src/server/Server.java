package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

        private String username;

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
                    System.out.println("Username: " + username);
                    int totalBytesRead = bytesRead; 

                    // Reading length of message
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

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(messageBuffer, 0, messageLength);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                    Message message = (Message) objectInputStream.readObject();

                    String receiver = message.getReceiver();

                    if (receiver.equals("server")) {
                        String content = new String(message.getContent(), StandardCharsets.UTF_8);
                        String[] elements = content.split(":");
                        if (elements.length == 2) {
                            String request = elements[0];
                            if (request.equals("Username")) {
                                this.username = elements[1];
                            }
                        }  
                    }

                    System.out.println(message);
                    System.out.println("---------------");
                }
                System.out.println("Client out");
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        }
    }

    public static void main(String[] args) {
        Server server = new Server();    
    }
}
