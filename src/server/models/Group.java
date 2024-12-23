package server.models;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private List<String> members = new ArrayList<>();

    public void addMember(String member) {
        this.members.add(member);
    }

    public boolean isMember(String member) {
        return this.members.contains(member);
    }
}
