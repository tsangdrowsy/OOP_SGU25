package ManageSchool.service;

import ManageSchool.model.Lecturer;
import ManageSchool.model.Student;

import java.util.*;
import java.util.stream.Collectors;



public class ManageTeacher implements Manage<Lecturer> {
    private List<Lecturer> list = new ArrayList<>();

    @Override
    public void add(Lecturer le) {
        list.add(le);
    }

    @Override
    public void update(Lecturer newLE) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLecturerID().equalsIgnoreCase(newLE.getLecturerID())) {
                list.set(i, newLE);
                break;
            }
        }
    }

    @Override
    public void remove(String LecturerID) {
        list.removeIf(gv -> gv.getLecturerID().equalsIgnoreCase(LecturerID));
    }

    @Override
    public Lecturer findByID(String LecturerID) {
        return list.stream()
                   .filter(gv -> gv.getLecturerID().equalsIgnoreCase(LecturerID))
                   .findFirst()
                   .orElse(null);
    }

    @Override
    public List<Lecturer> findByName(String name) {
        return list.stream()
                .filter(gv -> gv.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

   @Override
    public List<Lecturer> getAll() {
        return list;
    }

    
}
