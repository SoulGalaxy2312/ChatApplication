package client;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import server.db.db;
import server.models.Account;

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
        JPasswordField passwordField = new JPasswordField(10);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        
        JButton logInButton = new JButton("Log in");
        logInButton.addActionListener(new SignInActionListener(usernameTextField, passwordField));
        
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
        JPasswordField passwordTextField = new JPasswordField(10);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);


        JPanel confirmPasswordPanel = new JPanel(new FlowLayout());
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        confirmPasswordLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        JPasswordField confirmPasswordField = new JPasswordField(10);
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);

        JButton registryButton = new JButton("Registry");   
        registryButton.addActionListener(new RegistryActionListener(usernameTextField, passwordTextField, confirmPasswordField));

        registryPanel.add(usernamePanel);
        registryPanel.add(passwordPanel);
        registryPanel.add(confirmPasswordPanel);
        registryPanel.add(registryButton);

        return registryPanel;
    }

    private class SignInActionListener implements ActionListener {
        private JTextField username;
        private JPasswordField password;

        public SignInActionListener(JTextField username, JPasswordField password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String strUsername = this.username.getText();
            char[] strPassword = this.password.getPassword();

            Account signInAccount = new Account();
            signInAccount.setUsername(strUsername);
            signInAccount.setPassword(new String(strPassword));

            if (db.getInstance().verifyAccount(signInAccount)) {
                ClientLogInView.this.dispose();
                new ClientMainUI(strUsername);
            } else {
                JOptionPane.showMessageDialog(
                    ClientLogInView.this, 
                    "Error: Invalid account information", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private class RegistryActionListener implements ActionListener {
        private JTextField username;
        private JPasswordField password;
        private JPasswordField confirmPassword;

        public RegistryActionListener(JTextField username, JPasswordField password, JPasswordField confirmPassword) { 
            this.username = username;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String strUsername = username.getText();
            char[] strPassword = password.getPassword();
            char[] strConfirmPassword = confirmPassword.getPassword();

            if (!Arrays.equals(strPassword, strConfirmPassword)) { 
                JOptionPane.showMessageDialog(
                    ClientLogInView.this, 
                    "Confirm password not match !!!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
    
            Account newAccount = new Account();
            newAccount.setUsername(strUsername);
            newAccount.setPassword(new String(strPassword));

            if (db.getInstance().registryAccount(newAccount)) {
                JOptionPane.showMessageDialog(
                    ClientLogInView.this, 
                    "Registry new account successfully !!!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    ClientLogInView.this, 
                    "Existing username, try another username !!!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
