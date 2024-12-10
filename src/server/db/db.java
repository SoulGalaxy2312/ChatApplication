package server.db;

import java.util.ArrayList;
import java.util.List;

import server.models.Account;
import server.repositories.AccountRepository;

public class db {
    private static volatile db instance;

    private List<Account> accounts = new ArrayList<>();

    private AccountRepository accountRepository = new AccountRepository();

    private db() {
    }

    public static synchronized db getInstance() {
        if (instance == null) {
            instance = new db();
            instance.init();
        }
        return instance;
    }

    public void init() {
        this.accounts = this.accountRepository.loadAccounts();
    }

    public synchronized boolean registryAccount(Account account) {
        // Check if the account is alreay exist
        for (Account acc : instance.accounts) {
            if (acc.getUsername().equals(account.getUsername())) {
                System.out.println("Error: Account already exists");
                return false;
            }
        }

        instance.accounts.add(account);
        this.accountRepository.registryAccount(account);
        return true;
    }

    public synchronized boolean verifyAccount(Account account) {
        for (Account acc : instance.accounts) {
            if (acc.equals(account)) {
                return true;
            }
        }
        return false;
    }

}
