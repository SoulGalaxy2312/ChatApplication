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
import java.util.ArrayList;
import java.util.List;

import server.models.Message;
import server.models.MessageType;
import server.proxies.MessageProxy;
import server.repositories.MessageRepository;

public class Server {
    private final int SERVER_PORT = 1234;

    ServerSocket serverSocket;

    private List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server is running at port 1234");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("An user enters");

                ClientHandler client = new ClientHandler(clientSocket);
                this.clients.add(client);
                new Thread(client).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientHandler getClient(String username) {
        for (ClientHandler client : this.clients) {
            if (username.equals(client.getUsername())) {
                return client;
            }
        }
        return null;
    }

    private void broadCastOnlineUsersList(String newUser) throws IOException {
        for (ClientHandler clientHandler : this.clients) {
            Message notification = new Message();
            notification.setSender("server");
            notification.setReceiver(clientHandler.getUsername());
            notification.setType(MessageType.TEXT);

            StringBuilder content = new StringBuilder();

            if (clientHandler.getUsername().equals(newUser)) {
                content.append("Users list:");
                for (ClientHandler clientHandler2 : this.clients) {
                    content.append(clientHandler2.getUsername());
                    content.append(";");
                }
            } else {
                content.append("New user:");
                content.append(newUser);
            }

            notification.setContent(content.toString().getBytes());
            clientHandler.sendMessage(notification);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String username;
        private BufferedInputStream bis;
        private BufferedOutputStream bos;
        private final MessageProxy messageProxy;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.bis = new BufferedInputStream(clientSocket.getInputStream());
            this.bos = new BufferedOutputStream(clientSocket.getOutputStream());       
            this.messageProxy = new MessageProxy(bos);
        }

        public String getUsername() {
            return this.username;
        }

        @Override public void run() { 
            try { 
                byte[] lengthBuffer = new byte[4]; 
                int bytesRead;

                while ((bytesRead = bis.read(lengthBuffer)) != -1) { 
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

                    // Handle the received message
                    Message message = (Message) objectInputStream.readObject();

                    String receiver = message.getReceiver();

                    if (receiver.equals("server")) {
                        String content = new String(message.getContent(), StandardCharsets.UTF_8);
                        String[] elements = content.split(":");
                        if (elements.length == 2) {
                            String request = elements[0].trim();
                            if (request.equals("Username")) {
                                this.username = elements[1].trim();
                                broadCastOnlineUsersList(this.username);
                            }
                        }  
                    } else {
                        ClientHandler receiverHandler = getClient(receiver);
                        if (receiverHandler != null) {
                            try {
                                receiverHandler.sendMessage(message);    
                            } catch (Exception e) {
                                System.out.println("Error: Message Proxy has problem !!!");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Error: Receiver is not found !!!");
                        }

                        MessageRepository messageRepository = new MessageRepository();
                        messageRepository.saveMessage(message);
                    }

                    System.out.println(message);
                    System.out.println("---------------");
                }
                System.out.println("Client out");
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        }

        public void sendMessage(Message message) throws IOException {
            this.messageProxy.sendMessage(message);
        }
    } 

    public static void main(String[] args) {
        Server server = new Server();    
    }
}
