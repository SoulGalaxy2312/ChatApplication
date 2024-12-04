package client;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ClientLogInView extends JFrame {
    public ClientLogInView() {
        setSize(450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();


        tabs.addTab("Log in", createLogInTab());
        tabs.addTab("Registry", createRegistryTab());

        add(tabs);
        setVisible(true);
    }
    
    public JPanel createLogInTab() {
        JPanel logInPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel usernamePanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JTextField usernameTextField = new JTextField(10);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);


        JPanel passwordPanel = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JTextField passwordField = new JTextField(10);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        
        JButton logInButton = new JButton("Log in");
        
        logInPanel.add(usernamePanel);
        logInPanel.add(passwordPanel);
        logInPanel.add(logInButton);

        return logInPanel;
    }

    public JPanel createRegistryTab() {
        JPanel registryPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        JPanel usernamePanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JTextField usernameTextField = new JTextField(10);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);


        JPanel passwordPanel = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JTextField passwordTextField = new JTextField(10);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);


        JPanel confirmPasswordPanel = new JPanel(new FlowLayout());
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        confirmPasswordLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JTextField confirmPasswordField = new JTextField(10);
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);

        JButton registryButton = new JButton("Registry");

        registryPanel.add(usernamePanel);
        registryPanel.add(passwordPanel);
        registryPanel.add(confirmPasswordPanel);
        registryPanel.add(registryButton);

        return registryPanel;
    }
}
