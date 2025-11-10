package ManageSchool.service;

import ManageSchool.model.Student;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticStudent implements Statistic<Student> {
    private List<Student> data;

    public StatisticStudent(List<Student> data) {
        this.data = data;
    }

    // Tính học phí trung bình (theo payroll)
    @Override
    public double getAverage() {
        if (data.isEmpty()) return 0.0;
        return data.stream()
                   .mapToDouble(Student::payroll)
                   .average()
                   .orElse(0.0);
    }

    //Sinh viên có học phí cao nhất
    @Override
    public Student getTopEntity() {
        return data.stream()
                   .max(Comparator.comparingDouble(Student::payroll))
                   .orElse(null);
    }

    // Đếm số sinh viên theo khoa (department)
    @Override
    public long countByCategory(String department) {
        return data.stream()
                   .filter(st -> st.getDepartment().equalsIgnoreCase(department))
                   .count();
    }

    //Hiển thị thống kê tổng hợp
    @Override
    public void showStatistics() {
        System.out.println("===== STATISTIC STUDENT =====");
        System.out.println("Tổng số sinh viên: " + data.size());
        System.out.printf("Học phí trung bình: %.2f%n", getAverage());

        Student top = getTopEntity();
        if (top != null) {
            System.out.println("Sinh viên có học phí cao nhất: " + top.getName() +
                               " (" + top.getDepartment() + ") - " + top.payroll());
        }

        System.out.println("\nThống kê theo khoa:");
        Map<String, Long> depStats = data.stream()
                                         .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));
        depStats.forEach((dep, count) ->
                System.out.println(" - " + dep + ": " + count));

        System.out.println("==============================");
    }

    //  Trả về danh sách dữ liệu
    @Override
    public List<Student> getAllData() {
        return data;
    }
}
