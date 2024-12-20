package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import server.models.Message;
import server.models.MessageType;
import server.proxies.MessageProxy;
import server.repositories.MessageRepository;

public class ClientMainUI extends JFrame {
    private final String username;
    private final String HOST = "localhost";
    private final int SERVER_PORT = 1234;
    private Socket clientSocket;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private MessageProxy messageProxy;
    private boolean isUsernameSent = false;
    private String currentReceiver = null;

    private JTextArea chatHistory;
    private JTextField messageInput;
    private JButton sendButton;
    private JList<String> onlineUsersList;
    private DefaultListModel<String> onlineUsersModel;

    public ClientMainUI(String username) {
        this.username = username;
        setTitle("Chat Application - " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
        try { 
            connectServer();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void initComponents() {
        // Chat history
        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatHistory);

        // Online users list
        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        JScrollPane usersScrollPane = new JScrollPane(onlineUsersList);

        onlineUsersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && onlineUsersList.getSelectedValue() != null) {
                    currentReceiver = onlineUsersList.getSelectedValue();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            chatHistory.setText("Current chat with: " + currentReceiver + "\n");
                            MessageRepository messageRepository = new MessageRepository();
                            List<Message> messages = messageRepository.loadMessages(username, currentReceiver);

                            for (Message message : messages) {
                                String sender = message.getSender().equals(username) ? "You" : message.getSender();
                                String content = new String(message.getContent(), StandardCharsets.UTF_8);
                                chatHistory.append(sender + ": " + content + "\n");
                            }
                        }
                    });
                }
            }
        });

        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField groupNameField = new JTextField();
                JTextField membersField = new JTextField();
                Object[] message = {
                    "Group name:", groupNameField,
                    "Members (separated by \",\"):", membersField
                };
                
                int option = JOptionPane.showConfirmDialog(null, message, "Create Group", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String groupName = groupNameField.getText();
                    String members = membersField.getText();
                    try {
                        Message messageObj = new Message();
                        messageObj.setSender(username);
                        messageObj.setReceiver("server");
                        messageObj.setType(MessageType.TEXT);
                        StringBuilder builder = new StringBuilder();
                        builder.append("Create group:");
                        builder.append(groupName);
                        builder.append(":");
                        builder.append(members);
                        messageObj.setContent(builder.toString().getBytes());
                        messageProxy.sendMessage(messageObj);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(createGroupButton, BorderLayout.WEST);

        // Main layout setup
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(usersScrollPane, BorderLayout.CENTER);
        leftPanel.setSize(150, 400);

        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Send button action listener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    public void connectServer() throws Exception {        
        clientSocket = new Socket(HOST, SERVER_PORT);    
        this.bis = new BufferedInputStream(clientSocket.getInputStream());
        this.bos = new BufferedOutputStream(clientSocket.getOutputStream());

        this.messageProxy = new MessageProxy(bos);

        // Send credentials
        if (!this.isUsernameSent) {
            Message message = new Message();
            message.setSender(username);
            message.setReceiver("server");
            message.setType(MessageType.TEXT);
            String content = "Username:" + username;
            message.setContent(content.getBytes());
            messageProxy.sendMessage(message);
            isUsernameSent = true;
        }

        // Create socket to listen incoming messages
        new Thread(new Runnable() { 
            @Override 
            public void run() { 
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

                        // Reading message
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

                        // Handle the received message
                        Message message = (Message) objectInputStream.readObject();

                        String sender = message.getSender();

                        // If the sender == server
                        if (sender.equals("server")) {
                            String content = new String(message.getContent(), StandardCharsets.UTF_8);
                            String[] elements = content.split(":");
                            if (elements.length == 2) {
                                String notification = elements[0];  
                                
                                if (notification.equals("Users list")) { // Handle case: Current user is newly connected to server
                                    String[] users = elements[1].split(";");

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            onlineUsersModel.clear();
                                            for (String user : users) {
                                                onlineUsersModel.addElement(user);
                                            }
                                        } 
                                    });
                                } else if (notification.equals("New user")) { // Handle case: Current user receives notification about new user enters the server
                                    String newUser = elements[1];
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            onlineUsersModel.addElement(newUser);
                                        }
                                    });
                                }
                            }
                        } else {
                            if (sender.equals(currentReceiver)) {
                                String content = new String(message.getContent(), StandardCharsets.UTF_8);
                                chatHistory.append(sender + ": " + content + "\n");
                            }
                        }

                        System.out.println(message);
                    }
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            } 
        }).start();
    }

    private void sendMessage() {
        String input = messageInput.getText().trim();
        if (!input.isEmpty()) {
            try {
                Message message = new Message();
                message.setSender(username);
                message.setReceiver(currentReceiver);
                message.setType(MessageType.TEXT);
                message.setContent(input.getBytes());
                messageProxy.sendMessage(message);

                chatHistory.append("You: " + input + "\n");
                messageInput.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 