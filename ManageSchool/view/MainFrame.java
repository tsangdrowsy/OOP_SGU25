package ManageSchool.view;

import ManageSchool.model.*;
import ManageSchool.service.ManageStudent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private ManageStudent manageStudent = new ManageStudent();

    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);

    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Hệ thống Quản lý Trường học");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("CHỌN CHỨC NĂNG", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnStudent = new JButton("Quản lý Sinh viên");
        JButton btnTeacher = new JButton("Quản lý Giảng viên");
        JButton btnStaff = new JButton("Quản lý Nhân viên");

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
        panel.add(btnStaff);

        setContentPane(panel);
        pack();
        setSize(350, 300);
    }


    private void openStudentManagement() {
        java.awt.EventQueue.invokeLater(() -> {
            JFStudent studentFrame = new JFStudent();
            studentFrame.setManageStudent(manageStudent);
            studentFrame.setVisible(true);
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