package ManageSchool.service;

import ManageSchool.model.Student;
import ManageSchool.model.SpecialStudent;
import ManageSchool.model.GraduateStudent;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticStudent implements Statistic<Student> {
    private List<Student> data;

    public StatisticStudent(List<Student> data) {
        this.data = data;
    }

    @Override
    public double getAverage() {
        if (data.isEmpty()) return 0.0;
        return data.stream()
                   .mapToDouble(Student::payroll)
                   .average()
                   .orElse(0.0);
    }

    @Override
    public Student getTopEntity() {
        return data.stream()
                   .max(Comparator.comparingDouble(Student::payroll))
                   .orElse(null);
    }

    @Override
    public long countByCategory(String category) {
        switch (category.toLowerCase()) {
            case "special":
                return data.stream().filter(s -> s instanceof SpecialStudent).count();
            case "graduate":
                return data.stream().filter(s -> s instanceof GraduateStudent).count();
            case "regular":
                return data.stream().filter(s -> 
                    !(s instanceof SpecialStudent) && !(s instanceof GraduateStudent)).count();
            default:
                return 0;
        }
    }

    @Override
    public void showStatistics() {
        System.out.println("===== STATISTIC STUDENT =====");
        System.out.println("Tổng số sinh viên: " + data.size());
        System.out.printf("Học phí trung bình: %.2f%n", getAverage());

        Student top = getTopEntity();
        if (top != null) {
            String type = getStudentType(top);
            System.out.println("Sinh viên có học phí cao nhất: " + top.getName() +
                               " (" + type + ") - " + top.payroll());
        }

        System.out.println("\nThống kê theo loại sinh viên:");
        Map<String, Long> typeStats = data.stream()
                .collect(Collectors.groupingBy(
                    this::getStudentType,
                    Collectors.counting()
                ));
        typeStats.forEach((type, count) ->
                System.out.println(" - " + type + ": " + count));

        System.out.println("===========================");
    }

    private String getStudentType(Student student) {
        if (student instanceof SpecialStudent) return "SV Đặc biệt";
        else if (student instanceof GraduateStudent) return "SV Cao học";
        else return "SV Thường";
    }

    @Override
    public List<Student> getAllData() {
        return data;
    }
}