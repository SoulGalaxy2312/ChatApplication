package server.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import server.models.Account;

public class AccountRepository {
    private final String ACCOUNT_FILE = "data/accounts.txt";

    public List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE));) {
            String line = reader.readLine();

            while (line != null) {
                String[] elements = line.split(";");
                if (elements.length != 2) {
                    System.out.println("Error: Invalid Account Record");
                    System.err.println(line);
                } else {
                    Account account = new Account();
                    account.setUsername(elements[0]);
                    account.setPassword(elements[1]);
                    accounts.add(account);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accounts;
    }

    public void registryAccount(Account account) {
        File accountFile = new File(ACCOUNT_FILE);

        try {
            if (!accountFile.exists()) {
                accountFile.getParentFile().mkdir();
                accountFile.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountFile, true))) {
                writer.write(account.getUsername());
                writer.write(";");
                writer.write(account.getPassword());
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
