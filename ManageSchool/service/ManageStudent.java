package ManageSchool.service;

import ManageSchool.model.Student;
import ManageSchool.model.SpecialStudent;
import ManageSchool.model.GraduateStudent;
import java.util.*;
import java.util.stream.Collectors;

public class ManageStudent implements Manage<Student> {
    private List<Student> list = new ArrayList<>();

    @Override
    public void add(Student s) {
        list.add(s);
    }

    @Override
    public void update(Student newStu) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStudentID().equalsIgnoreCase(newStu.getStudentID())) {
                list.set(i, newStu);
                return;
            }
        }
    }

    @Override
    public void remove(String studentID) {
        list.removeIf(s -> s.getStudentID().equalsIgnoreCase(studentID));
    }

    @Override
    public Student findByID(String studentID) {
        return list.stream()
                .filter(s -> s.getStudentID().equalsIgnoreCase(studentID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Student> findByName(String name) {
        return list.stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> getAll() {
        return list;
    }

    // Thêm phương thức lọc theo loại sinh viên
    public <T extends Student> List<Student> getByType(Class<T> studentType) {
        return list.stream()
                .filter(studentType::isInstance)
                .collect(Collectors.toList());
    }

    // Thống kê theo loại sinh viên
    public Map<String, Long> getStatisticsByType() {
        return list.stream()
                .collect(Collectors.groupingBy(
                    student -> {
                        if (student instanceof SpecialStudent) return "SV Đặc biệt";
                        else if (student instanceof GraduateStudent) return "SV Cao học";
                        else return "SV Thường";
                    },
                    Collectors.counting()
                ));
    }
}