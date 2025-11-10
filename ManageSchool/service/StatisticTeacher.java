package ManageSchool.service;

import ManageSchool.model.Lecturer;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticTeacher implements Statistic<Lecturer> {
    private List<Lecturer> data;

    public StatisticTeacher(List<Lecturer> data) {
        this.data = data;
    }

    // Tính trung bình lương giảng viên
    @Override
    public double getAverage() {
        if (data.isEmpty()) return 0.0;
        return data.stream()
                   .mapToDouble(Lecturer::payroll)
                   .average()
                   .orElse(0.0);
    }

    // Tìm giảng viên có lương cao nhất
    @Override
    public Lecturer getTopEntity() {
        return data.stream()
                   .max(Comparator.comparingDouble(Lecturer::payroll))
                   .orElse(null);
    }

    //  Đếm số giảng viên theo cấp bậc (level)
    @Override
    public long countByCategory(String level) {
        return data.stream()
                   .filter(gv -> gv.getLevel().equalsIgnoreCase(level))
                   .count();
    }

    // In thống kê tổng hợp
    @Override
    public void showStatistics() {
        System.out.println("===== STATISTIC TEACHER =====");
        System.out.println("Tổng số giảng viên: " + data.size());
        System.out.printf("Lương trung bình: %.2f%n", getAverage());

        Lecturer top = getTopEntity();
        if (top != null) {
            System.out.println("Giảng viên lương cao nhất: " + top.getName() +
                               " (" + top.getLevel() + ") - " + top.payroll());
        }

        System.out.println("\nThống kê theo cấp bậc:");
        Map<String, Long> levelStats = data.stream()
                                           .collect(Collectors.groupingBy(Lecturer::getLevel, Collectors.counting()));
        levelStats.forEach((level, count) ->
                System.out.println(" - " + level + ": " + count));
        System.out.println("=============================");
    }

    // Trả về danh sách dữ liệu
    @Override
    public List<Lecturer> getAllData() {
        return data;
    }
}
