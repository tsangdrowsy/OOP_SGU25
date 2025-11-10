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
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("CHỌN CHỨC NĂNG", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnStudent = new JButton("Quản lý Sinh viên");
        JButton btnTeacher = new JButton("Quản lý Giảng viên");
        
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
        
        panel.add(title);
        panel.add(btnStudent);
        panel.add(btnTeacher);
        
        setContentPane(panel);
        pack();
        setSize(300, 200);
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
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}