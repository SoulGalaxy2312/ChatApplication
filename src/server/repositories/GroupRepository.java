package server.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import server.models.Group;

public class GroupRepository {
    private final String FILEPATH = "data/groups.txt";

    public void saveGroup(String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILEPATH, true));
            writer.write(content);
            writer.flush();
            writer.newLine();
            
            writer.close();
        } catch (Exception e) {
            
        }
    }

    public List<Group> loadGroups() {
        List<Group> groups = new ArrayList<>();

        try {
            File groupFile = new File(FILEPATH);
            if (!groupFile.exists()) {
                groupFile.createNewFile();
                return groups;
            }

            BufferedReader reader = new BufferedReader(new FileReader(groupFile));
            String line = reader.readLine();

            while (line != null) {
                String[] elements = line.split(":");

                if (elements.length == 2) {
                    Group newGroup = new Group();
                    newGroup.setGroupName(elements[0]);
                    
                    String[] members = elements[1].split(",");
                    for (String member : members) {
                        newGroup.addMember(member);
                    }

                    groups.add(newGroup);
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Error: File not found");
            e.printStackTrace();
        }

        return groups;
    }

    public List<String> findGroupListByUsername(List<Group> groups, String username) {
        List<String> groupList = new ArrayList<>();

        for (Group group : groups) {
            if (group.hasMember(username)) {
                groupList.add(group.getGroupName());
            }
        }

        return groupList;
    }
}
