package server.models;

public class Account {
    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object compared) {
        if (compared == this) return true;
        if (compared == null || !(compared instanceof Account)) return false;
        Account comparedObj = (Account) compared;

        return this.username.equals(comparedObj.getUsername());
    }
}
