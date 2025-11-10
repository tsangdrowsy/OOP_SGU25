package ManageSchool.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Hệ thống Quản lý Trường học");
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // Đổi thành 4 hàng
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("CHỌN CHỨC NĂNG", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnStudent = new JButton("Quản lý Sinh viên");
        JButton btnTeacher = new JButton("Quản lý Giảng viên");
        JButton btnStaff = new JButton("Quản lý Nhân viên"); // Thêm nút mới
        
        btnStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStudentManagement();
            }
        });
        
        btnTeacher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTeacherManagement();
            }
        });
        
        btnStaff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStaffManagement();
            }
        });
        
        panel.add(title);
        panel.add(btnStudent);
        panel.add(btnTeacher);
        panel.add(btnStaff); // Thêm nút vào panel
        
        setContentPane(panel);
        pack();
        setSize(300, 250); // Tăng chiều cao để chứa thêm nút
    }
    
    private void openStudentManagement() {
        java.awt.EventQueue.invokeLater(() -> {
            new JFStudent().setVisible(true);
        });
    }
    
    private void openTeacherManagement() {
        java.awt.EventQueue.invokeLater(() -> {
            new JFLecturer().setVisible(true);
        });
    }
    
    private void openStaffManagement() {
        java.awt.EventQueue.invokeLater(() -> {
            new JFStaff().setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}