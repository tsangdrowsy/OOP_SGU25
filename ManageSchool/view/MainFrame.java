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
    
    // private void loadDataFromJson() {
    //     try {
    //         // Đọc file JSON
    //         BufferedReader reader = new BufferedReader(new FileReader("Student.json"));
    //         StringBuilder jsonContent = new StringBuilder();
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             jsonContent.append(line);
    //         }
    //         reader.close();
            
    //         // Parse JSON thủ công
    //         List<Student> students = parseJsonManually(jsonContent.toString());
            
    //         // Xóa dữ liệu cũ và thêm dữ liệu mới
    //         manageStudent.clearAll();
    //         for (Student student : students) {
    //             if (student != null) {
    //                 manageStudent.add(student);
    //             }
    //         }
            
    //         JOptionPane.showMessageDialog(this, 
    //             "Đã tải " + students.size() + " sinh viên từ file JSON!");
            
    //     } catch (Exception e) {
    //         JOptionPane.showMessageDialog(this, 
    //             "Lỗi khi đọc file JSON: " + e.getMessage(), 
    //             "Lỗi", JOptionPane.ERROR_MESSAGE);
    //         e.printStackTrace();
    //     }
    // }
    
    private List<Student> parseJsonManually(String jsonString) {
        List<Student> students = new ArrayList<>();
        
        try {
            // Loại bỏ khoảng trắng và xuống dòng
            String cleanJson = jsonString.replaceAll("\\s+", "");
            
            // Kiểm tra xem có phải mảng JSON không
            if (!cleanJson.startsWith("[") || !cleanJson.endsWith("]")) {
                throw new IllegalArgumentException("JSON phải là một mảng");
            }
            
            // Loại bỏ dấu ngoặc vuông
            String content = cleanJson.substring(1, cleanJson.length() - 1);
            
            // Tách các object JSON
            List<String> jsonObjects = splitJsonObjects(content);
            
            for (String objStr : jsonObjects) {
                Student student = parseStudentObject(objStr);
                if (student != null) {
                    students.add(student);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi parse JSON: " + e.getMessage());
        }
        
        return students;
    }
    
    private List<String> splitJsonObjects(String jsonArrayContent) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        StringBuilder currentObject = new StringBuilder();
        
        for (char c : jsonArrayContent.toCharArray()) {
            if (c == '{') {
                braceCount++;
            }
            if (c == '}') {
                braceCount--;
            }
            
            currentObject.append(c);
            
            if (braceCount == 0 && currentObject.length() > 0) {
                String obj = currentObject.toString().trim();
                if (obj.startsWith("{") && obj.endsWith("}")) {
                    objects.add(obj);
                }
                currentObject = new StringBuilder();
                
                // Bỏ qua dấu phẩy phân cách
                while (jsonArrayContent.indexOf(',', currentObject.length()) == currentObject.length()) {
                    currentObject.append(',');
                }
            }
        }
        
        return objects;
    }
    
   private Student parseStudentObject(String jsonObject) {
    try {
        // Loại bỏ dấu ngoặc nhọn
        String content = jsonObject.substring(1, jsonObject.length() - 1);
        
        // Parse các trường - SỬA LẠI THEO ĐÚNG TÊN TRƯỜNG TRONG JSON
        String name = getJsonFieldValue(content, "name");
        int age = Integer.parseInt(getJsonFieldValue(content, "age"));
        String citizenID = getJsonFieldValue(content, "humanid");
        String dateOfBirth = getJsonFieldValue(content, "dateOfBirth");
        String gender = getJsonFieldValue(content, "gender"); // Sửa từ "sex" thành "gender"
        String studentID = getJsonFieldValue(content, "studentID");
        String faculty = getJsonFieldValue(content, "faculty"); // Sửa từ "department" thành "faculty"
        String major = getJsonFieldValue(content, "major");
        int credits = Integer.parseInt(getJsonFieldValue(content, "accumulatedCredits")); // Sửa từ "credits" thành "accumulatedCredits"
        
        // Kiểm tra loại sinh viên - THÊM CHECK TRƯỜNG "type"
        if (hasJsonField(content, "type")) {
            String type = getJsonFieldValue(content, "type");
            switch (type) {
                case "SpecialStudent":
                    float rateScholarship = Float.parseFloat(getJsonFieldValue(content, "rateScholarship"));
                    return new SpecialStudent(name, age, citizenID, dateOfBirth, gender, 
                                            studentID, faculty, major, credits, rateScholarship);
                case "GraduateStudent":
                    String projectID = getJsonFieldValue(content, "projectID");
                    String projectName = getJsonFieldValue(content, "projectName");
                    String supervisorID = getJsonFieldValue(content, "supervisorID");
                    return new GraduateStudent(name, age, citizenID, dateOfBirth, gender,
                                             studentID, faculty, major, credits, 
                                             projectID, projectName, supervisorID);
            }
        }
        
        // Kiểm tra theo trường đặc biệt (fallback nếu không có trường "type")
        if (hasJsonField(content, "rateScholarship")) {
            float rateScholarship = Float.parseFloat(getJsonFieldValue(content, "rateScholarship"));
            return new SpecialStudent(name, age, citizenID, dateOfBirth, gender, 
                                    studentID, faculty, major, credits, rateScholarship);
        }
        
        if (hasJsonField(content, "projectID")) {
            String projectID = getJsonFieldValue(content, "projectID");
            String projectName = getJsonFieldValue(content, "projectName");
            String supervisorID = getJsonFieldValue(content, "supervisorID");
            return new GraduateStudent(name, age, citizenID, dateOfBirth, gender,
                                     studentID, faculty, major, credits, 
                                     projectID, projectName, supervisorID);
        }
        
        // Mặc định là sinh viên thường
        return new Student(name, age, citizenID, dateOfBirth, gender, 
                         studentID, faculty, major, credits);
        
    } catch (Exception e) {
        System.err.println("Lỗi parse student object: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    private String getJsonFieldValue(String jsonContent, String fieldName) {
        String searchStr = "\"" + fieldName + "\":";
        int startIndex = jsonContent.indexOf(searchStr);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Không tìm thấy field: " + fieldName);
        }
        
        startIndex += searchStr.length();
        int endIndex = jsonContent.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonContent.length();
        }
        
        String valueStr = jsonContent.substring(startIndex, endIndex).trim();
        
        // Xử lý giá trị string (loại bỏ dấu ngoặc kép)
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            return valueStr.substring(1, valueStr.length() - 1);
        }
        
        return valueStr;
    }
    
    private boolean hasJsonField(String jsonContent, String fieldName) {
        return jsonContent.contains("\"" + fieldName + "\":");
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