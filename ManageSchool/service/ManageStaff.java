package ManageSchool.service;

import ManageSchool.model.Staff;
import ManageSchool.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageStaff implements Manage<Staff> {
    private List<Staff> staffList = new ArrayList<>();

    @Override
    public void add(Staff s) {
        staffList.add(s);
    }

    @Override
    public void update(Staff s) {
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffID().equals(s.getStaffID())) {
                staffList.set(i, s);
                return;
            }
        }
    }

    @Override
    public void remove(String id) {
        staffList.removeIf(s -> s.getStaffID().equals(id));
    }

    @Override
    public Staff findByID(String id) {
        for (Staff s : staffList) {
            if (s.getStaffID().equals(id)) return s;
        }
        return null;
    }
     @Override
    public List<Staff> findByName(String name) {
        return staffList.stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Staff> getAll() {
        return staffList;
    }
     public void clearAll() {
        staffList.clear();
    }
}
