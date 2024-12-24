package server.models;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupName;
    private List<String> members = new ArrayList<>();

    public Group() {}

    public Group(String groupName, String[] members) {
        this.groupName = groupName;
        for (String member : members) {
            this.members.add(member);
        }
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void addMember(String member) {
        this.members.add(member);
    }

    public boolean hasMember(String member) {
        return this.members.contains(member);
    }

    public String getGroupName() {
        return this.groupName;
    }
}
