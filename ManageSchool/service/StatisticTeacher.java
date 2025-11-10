package ManageSchool.service;

import ManageSchool.model.Lecturer;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticTeacher implements Statistic<Lecturer> {
    private List<Lecturer> data;

    public StatisticTeacher(List<Lecturer> data) {
        this.data = data;
    }

    @Override
    public double getAverage() {
        if (data.isEmpty()) return 0.0;
        return data.stream()
                   .mapToDouble(Lecturer::payroll)
                   .average()
                   .orElse(0.0);
    }

    @Override
    public Lecturer getTopEntity() {
        return data.stream()
                   .max(Comparator.comparingDouble(Lecturer::payroll))
                   .orElse(null);
    }

    @Override
    public long countByCategory(String level) {
        return data.stream()
                   .filter(gv -> gv.getLevel().equalsIgnoreCase(level))
                   .count();
    }

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

    @Override
    public List<Lecturer> getAllData() {
        return data;
    }
}