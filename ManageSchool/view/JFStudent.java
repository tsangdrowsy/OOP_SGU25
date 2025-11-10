package ManageSchool.view;

import ManageSchool.model.Student;
import ManageSchool.service.ManageStudent;
import ManageSchool.service.StatisticStudent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class JFStudent extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JFStudent.class.getName());
    private ManageStudent manageStudent = new ManageStudent();
    private DefaultTableModel tableModel;
    private boolean isEditing = false;
    private Student currentStudent = null;

    public JFStudent() {
        initComponents();
        this.jPanel2.setBackground(Color.LIGHT_GRAY);
        initTable();
        loadDataToTable();
        setButtonStates(true, false, false, false);
        clearForm();
        setupEventListeners();
    }

    private void setupEventListeners() {
        btnAdd1.addActionListener(evt -> btnAddActionPerformed());
        btnEdit.addActionListener(evt -> btnEditActionPerformed());
        btnDelete.addActionListener(evt -> btnDeleteActionPerformed());
        btnSave.addActionListener(evt -> btnSaveActionPerformed());
        btnCancel.addActionListener(evt -> btnCancelActionPerformed());
        btnSearch.addActionListener(evt -> btnSearchActionPerformed());
        btnSearchById.addActionListener(evt -> btnSearchByIdActionPerformed());
        btnStatistic.addActionListener(evt -> btnStatisticActionPerformed());
        btnRefresh.addActionListener(evt -> btnRefreshActionPerformed());
        
        // Enter key listener for search
        txtSearch.addActionListener(evt -> btnSearchActionPerformed());
        txtSearchById.addActionListener(evt -> btnSearchByIdActionPerformed());
    }

    private void initTable() {
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);
        
        String[] columns = {"MSSV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Ngành", "Tín chỉ tích luỹ", "Học phí"};
        tableModel.setColumnIdentifiers(columns);
        
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                selectStudentFromTable();
            }
        });
    }

    private void loadDataToTable() {
        loadDataToTable(manageStudent.getAll());
    }

    private void loadDataToTable(List<Student> students) {
        tableModel.setRowCount(0);
        
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                student.getStudentID(),
                student.getName(),
                student.getCitizenID(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getFaculty(),
                student.getMajor(),
                student.getAccumulatedCredits(),
                String.format("%,.0f VND", student.payroll())
            });
        }
    }

    private void selectStudentFromTable() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            String studentID = tableModel.getValueAt(selectedRow, 0).toString();
            currentStudent = manageStudent.findByID(studentID);
            
            if (currentStudent != null) {
                fillFormWithStudent(currentStudent);
                setButtonStates(false, true, true, false);
            }
        }
    }

    private void fillFormWithStudent(Student student) {
        jTextField1.setText(student.getStudentID());
        jTextField2.setText(student.getName());
        jTextField3.setText(student.getCitizenID());
        jTextField4.setText(student.getDateOfBirth());
        jTextField5.setText(student.getGender());
        jTextField6.setText(student.getFaculty());
        jTextField7.setText(student.getMajor());
        jTextField8.setText(String.valueOf(student.getAccumulatedCredits()));
    }

    private void clearForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        currentStudent = null;
        jTable1.clearSelection();
        setButtonStates(true, false, false, false);
    }

    private void setButtonStates(boolean addEnabled, boolean editEnabled, boolean deleteEnabled, boolean saveCancelEnabled) {
        btnAdd1.setEnabled(addEnabled);
        btnEdit.setEnabled(editEnabled);
        btnDelete.setEnabled(deleteEnabled);
        btnSave.setEnabled(saveCancelEnabled);
        btnCancel.setEnabled(saveCancelEnabled);
        
        boolean editable = saveCancelEnabled;
        jTextField1.setEditable(editable && !isEditing);
        jTextField2.setEditable(editable);
        jTextField3.setEditable(editable);
        jTextField4.setEditable(editable);
        jTextField5.setEditable(editable);
        jTextField6.setEditable(editable);
        jTextField7.setEditable(editable);
        jTextField8.setEditable(editable);
    }

    private Student getStudentFromForm() {
        try {
            String studentID = jTextField1.getText().trim();
            String name = jTextField2.getText().trim();
            String citizenID = jTextField3.getText().trim();
            String dateOfBirth = jTextField4.getText().trim();
            String gender = jTextField5.getText().trim();
            String faculty = jTextField6.getText().trim();
            String major = jTextField7.getText().trim();
            String creditsStr = jTextField8.getText().trim();

            if (studentID.isEmpty() || name.isEmpty() || citizenID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "MSSV, Họ tên và CCCD không được để trống!");
                return null;
            }

            if (!dateOfBirth.isEmpty()) {
                try {
                    LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!");
                    return null;
                }
            }

            int age = 0;
            int credits = 0;
            if (!creditsStr.isEmpty()) {
                try {
                    credits = Integer.parseInt(creditsStr);
                    if (credits < 0) {
                        JOptionPane.showMessageDialog(this, "Tín chỉ tích lũy không được âm!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Tín chỉ tích lũy phải là số!");
                    return null;
                }
            }

            return new Student(name, age, citizenID, dateOfBirth, gender, studentID, faculty, major, credits);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi nhập dữ liệu: " + e.getMessage());
            return null;
        }
    }

    // ========== CÁC PHƯƠNG THỨC XỬ LÝ SỰ KIỆN ==========
    private void btnAddActionPerformed() {
        clearForm();
        isEditing = false;
        setButtonStates(false, false, false, true);
        jTextField1.requestFocus();
    }

    private void btnEditActionPerformed() {
        if (currentStudent != null) {
            isEditing = true;
            setButtonStates(false, false, false, true);
            jTextField2.requestFocus();
        }
    }

    private void btnDeleteActionPerformed() {
        if (currentStudent != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xoá sinh viên " + currentStudent.getName() + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                manageStudent.remove(currentStudent.getStudentID());
                loadDataToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Đã xoá sinh viên thành công!");
            }
        }
    }

    private void btnSaveActionPerformed() {
        Student student = getStudentFromForm();
        if (student != null) {
            try {
                if (isEditing) {
                    manageStudent.update(student);
                    JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
                } else {
                    if (manageStudent.findByID(student.getStudentID()) != null) {
                        JOptionPane.showMessageDialog(this, "MSSV đã tồn tại!");
                        return;
                    }
                    manageStudent.add(student);
                    JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                }
                
                loadDataToTable();
                clearForm();
                setButtonStates(true, false, false, false);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void btnCancelActionPerformed() {
        if (currentStudent != null && isEditing) {
            fillFormWithStudent(currentStudent);
        } else {
            clearForm();
        }
        setButtonStates(true, currentStudent != null, currentStudent != null, false);
        isEditing = false;
    }

    // ========== TÌM KIẾM THEO TÊN ==========
    private void btnSearchActionPerformed() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        List<Student> searchResults = manageStudent.findByName(keyword);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên với từ khóa: " + keyword);
        } else {
            loadDataToTable(searchResults);
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + searchResults.size() + " sinh viên phù hợp");
        }
    }

    // ========== TÌM KIẾM THEO ID ==========
    private void btnSearchByIdActionPerformed() {
        String studentID = txtSearchById.getText().trim();
        if (studentID.isEmpty()) {
            loadDataToTable();
            return;
        }

        Student student = manageStudent.findByID(studentID);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên với MSSV: " + studentID);
        } else {
            // Hiển thị sinh viên tìm được trong bảng
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{
                student.getStudentID(),
                student.getName(),
                student.getCitizenID(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getFaculty(),
                student.getMajor(),
                student.getAccumulatedCredits(),
                String.format("%,.0f VND", student.payroll())
            });
            
            // Tự động chọn và điền form
            currentStudent = student;
            fillFormWithStudent(student);
            setButtonStates(false, true, true, false);
            
            JOptionPane.showMessageDialog(this, 
                "Đã tìm thấy sinh viên: " + student.getName());
        }
    }

    private void btnStatisticActionPerformed() {
        List<Student> students = manageStudent.getAll();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để thống kê!");
            return;
        }

        StatisticStudent statistic = new StatisticStudent(students);
        
        StringBuilder stats = new StringBuilder();
        stats.append("===== THỐNG KÊ SINH VIÊN =====\n\n");
        stats.append("Tổng số sinh viên: ").append(students.size()).append("\n");
        stats.append(String.format("Học phí trung bình: %,.0f VND\n", statistic.getAverage()));
        
        Student topStudent = statistic.getTopEntity();
        if (topStudent != null) {
            stats.append("Sinh viên có học phí cao nhất:\n");
            stats.append(" - ").append(topStudent.getName()).append(" (").append(topStudent.getStudentID()).append(")\n");
            stats.append(" - Khoa: ").append(topStudent.getFaculty()).append("\n");
            stats.append(String.format(" - Học phí: %,.0f VND\n", topStudent.payroll()));
        }
        
        stats.append("\nThống kê theo khoa:\n");
        java.util.Map<String, Long> facultyStats = students.stream()
            .collect(java.util.stream.Collectors.groupingBy(Student::getFaculty, 
                     java.util.stream.Collectors.counting()));
        
        facultyStats.forEach((faculty, count) -> {
            stats.append(" - ").append(faculty).append(": ").append(count).append(" sinh viên\n");
        });

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Thống Kê Sinh Viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnRefreshActionPerformed() {
        loadDataToTable();
        txtSearch.setText("");
        txtSearchById.setText("");
        clearForm();
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnStatistic = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        btnAdd1 = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtSearchById = new javax.swing.JTextField();
        btnSearchById = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Tìm theo tên:");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnSearch.setText("Tìm theo tên");

        btnStatistic.setText("Thống kê");

        btnRefresh.setText("Làm mới");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "MSSV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Ngành", "Tín chỉ", "Học phí"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("MSSV:");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Họ Tên:");

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("CCCD:");

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Ngày Sinh:");

        jTextField4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Giới Tính:");

        jTextField5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Khoa:");

        jTextField6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Ngành:");

        jTextField7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Tín chỉ tích luỹ:");

        jTextField8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnAdd1.setText("Thêm");

        btnEdit.setText("Sửa");

        btnDelete.setText("Xoá");

        btnSave.setText("Lưu");

        btnCancel.setText("Huỷ");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Tìm theo MSSV:");

        txtSearchById.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnSearchById.setText("Tìm theo MSSV");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchById, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearchById)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStatistic)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefresh))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField2))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField3))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField4))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField5))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField6))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField7))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(btnAdd1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnEdit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnDelete))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(btnSave)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCancel)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(jLabel11)
                    .addComponent(txtSearchById, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchById)
                    .addComponent(btnStatistic)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdd1)
                            .addComponent(btnEdit)
                            .addComponent(btnDelete))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSave)
                            .addComponent(btnCancel)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDesktopPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>                        

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new JFStudent().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAdd1;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchById;
    private javax.swing.JButton btnStatistic;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchById;
    // End of variables declaration                   
}