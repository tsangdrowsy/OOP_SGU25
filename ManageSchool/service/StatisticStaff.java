package ManageSchool.service;

import ManageSchool.model.Staff;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticStaff implements Statistic<Staff> {
    private List<Staff> data;

    public StatisticStaff(List<Staff> data) {
        this.data = data;
    }

    //  Tính lương trung bình
    @Override
    public double getAverage() {
        if (data.isEmpty()) return 0.0;
        return data.stream()
                   .mapToDouble(Staff::payroll)
                   .average()
                   .orElse(0.0);
    }

    // Nhân viên có lương cao nhất
    @Override
    public Staff getTopEntity() {
        return data.stream()
                   .max(Comparator.comparingDouble(Staff::payroll))
                   .orElse(null);
    }

    // Đếm số nhân viên theo chức vụ (position)
    @Override
    public long countByCategory(String position) {
        return data.stream()
                   .filter(st -> st.getPosition().equalsIgnoreCase(position))
                   .count();
    }

    //  Hiển thị thống kê tổng hợp
    @Override
    public void showStatistics() {
        System.out.println("===== STATISTIC STAFF =====");
        System.out.println("Tổng số nhân viên: " + data.size());
        System.out.printf("Lương trung bình: %.2f%n", getAverage());

        Staff top = getTopEntity();
        if (top != null) {
            System.out.println("Nhân viên có lương cao nhất: " + top.getName() +
                               " (" + top.getPosition() + ") - " + top.payroll());
        }

        System.out.println("\nThống kê theo chức vụ:");
        Map<String, Long> posStats = data.stream()
                                         .collect(Collectors.groupingBy(Staff::getPosition, Collectors.counting()));
        posStats.forEach((pos, count) ->
                System.out.println(" - " + pos + ": " + count));

        System.out.println("===========================");
    }

    //  Trả về danh sách dữ liệu
    @Override
    public List<Staff> getAllData() {
        return data;
    }
}
