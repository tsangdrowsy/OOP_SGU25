package ManageSchool.view;

import ManageSchool.model.*;
import ManageSchool.service.ManageTeacher;
import ManageSchool.service.StatisticTeacher;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class JFLecturer extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JFLecturer.class.getName());
    private ManageTeacher manageTeacher = new ManageTeacher();
    private DefaultTableModel tableModel;
    private boolean isEditing = false;
    private Lecturer currentLecturer = null;

    public JFLecturer() {
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
        
        String[] columns = {"Mã GV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Số giờ dạy", "Trình độ", "Lương"};
        tableModel.setColumnIdentifiers(columns);
        
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                selectLecturerFromTable();
            }
        });
    }

    private void loadDataToTable() {
        loadDataToTable(manageTeacher.getAll());
    }

    private void loadDataToTable(List<Lecturer> lecturers) {
        tableModel.setRowCount(0);
        
        for (Lecturer lecturer : lecturers) {
            tableModel.addRow(new Object[]{
                lecturer.getLecturerID(),
                lecturer.getName(),
                lecturer.getHumanid(),
                lecturer.getDateOfBirth(),
                lecturer.getSex(),
                lecturer.getDepartment(),
                lecturer.getTeachingHours(),
                lecturer.getLevel(),
                String.format("%,.0f VND", lecturer.payroll())
            });
        }
    }

    private void selectLecturerFromTable() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            String lecturerID = tableModel.getValueAt(selectedRow, 0).toString();
            currentLecturer = manageTeacher.findByID(lecturerID);
            
            if (currentLecturer != null) {
                fillFormWithLecturer(currentLecturer);
                setButtonStates(false, true, true, false);
            }
        }
    }

    private void fillFormWithLecturer(Lecturer lecturer) {
        jTextField1.setText(lecturer.getLecturerID());
        jTextField2.setText(lecturer.getName());
        jTextField3.setText(lecturer.getHumanid());
        jTextField4.setText(lecturer.getDateOfBirth());
        jTextField5.setText(lecturer.getSex());
        jTextField6.setText(lecturer.getDepartment());
        jTextField7.setText(String.valueOf(lecturer.getTeachingHours()));
        jTextField8.setText(lecturer.getLevel());
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
        currentLecturer = null;
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

    private Lecturer getLecturerFromForm() {
        try {
            String lecturerID = jTextField1.getText().trim();
            String name = jTextField2.getText().trim();
            String humanID = jTextField3.getText().trim();
            String dateOfBirth = jTextField4.getText().trim();
            String sex = jTextField5.getText().trim();
            String department = jTextField6.getText().trim();
            String hoursStr = jTextField7.getText().trim();
            String level = jTextField8.getText().trim();

            if (lecturerID.isEmpty() || name.isEmpty() || humanID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã GV, Họ tên và CCCD không được để trống!");
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

            float teachingHours = 0;
            if (!hoursStr.isEmpty()) {
                try {
                    teachingHours = Float.parseFloat(hoursStr);
                    if (teachingHours < 0) {
                        JOptionPane.showMessageDialog(this, "Số giờ dạy không được âm!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số giờ dạy phải là số!");
                    return null;
                }
            }

            return new Lecturer(name, 0, humanID, dateOfBirth, sex, department, lecturerID, teachingHours, level);

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
        if (currentLecturer != null) {
            isEditing = true;
            setButtonStates(false, false, false, true);
            jTextField2.requestFocus();
        }
    }

    private void btnDeleteActionPerformed() {
        if (currentLecturer != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xoá giảng viên " + currentLecturer.getName() + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                manageTeacher.remove(currentLecturer.getLecturerID());
                loadDataToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Đã xoá giảng viên thành công!");
            }
        }
    }

    private void btnSaveActionPerformed() {
        Lecturer lecturer = getLecturerFromForm();
        if (lecturer != null) {
            try {
                if (isEditing) {
                    manageTeacher.update(lecturer);
                    JOptionPane.showMessageDialog(this, "Cập nhật giảng viên thành công!");
                } else {
                    if (manageTeacher.findByID(lecturer.getLecturerID()) != null) {
                        JOptionPane.showMessageDialog(this, "Mã GV đã tồn tại!");
                        return;
                    }
                    manageTeacher.add(lecturer);
                    JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công!");
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
        if (currentLecturer != null && isEditing) {
            fillFormWithLecturer(currentLecturer);
        } else {
            clearForm();
        }
        setButtonStates(true, currentLecturer != null, currentLecturer != null, false);
        isEditing = false;
    }

    // ========== TÌM KIẾM THEO TÊN ==========
    private void btnSearchActionPerformed() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        List<Lecturer> searchResults = manageTeacher.findByName(keyword);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy giảng viên với từ khóa: " + keyword);
        } else {
            loadDataToTable(searchResults);
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + searchResults.size() + " giảng viên phù hợp");
        }
    }

    // ========== TÌM KIẾM THEO ID ==========
    private void btnSearchByIdActionPerformed() {
        String lecturerID = txtSearchById.getText().trim();
        if (lecturerID.isEmpty()) {
            loadDataToTable();
            return;
        }

        Lecturer lecturer = manageTeacher.findByID(lecturerID);
        if (lecturer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy giảng viên với mã: " + lecturerID);
        } else {
            // Hiển thị giảng viên tìm được trong bảng
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{
                lecturer.getLecturerID(),
                lecturer.getName(),
                lecturer.getHumanid(),
                lecturer.getDateOfBirth(),
                lecturer.getSex(),
                lecturer.getDepartment(),
                lecturer.getTeachingHours(),
                lecturer.getLevel(),
                String.format("%,.0f VND", lecturer.payroll())
            });
            
            // Tự động chọn và điền form
            currentLecturer = lecturer;
            fillFormWithLecturer(lecturer);
            setButtonStates(false, true, true, false);
            
            JOptionPane.showMessageDialog(this, 
                "Đã tìm thấy giảng viên: " + lecturer.getName());
        }
    }

    private void btnStatisticActionPerformed() {
        List<Lecturer> lecturers = manageTeacher.getAll();
        if (lecturers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để thống kê!");
            return;
        }

        StatisticTeacher statistic = new StatisticTeacher(lecturers);
        
        StringBuilder stats = new StringBuilder();
        stats.append("===== THỐNG KÊ GIẢNG VIÊN =====\n\n");
        stats.append("Tổng số giảng viên: ").append(lecturers.size()).append("\n");
        stats.append(String.format("Lương trung bình: %,.0f VND\n", statistic.getAverage()));
        
        Lecturer topLecturer = statistic.getTopEntity();
        if (topLecturer != null) {
            stats.append("Giảng viên có lương cao nhất:\n");
            stats.append(" - ").append(topLecturer.getName()).append(" (").append(topLecturer.getLevel()).append(")\n");
            stats.append(" - Mã GV: ").append(topLecturer.getLecturerID()).append("\n");
            stats.append(" - Khoa: ").append(topLecturer.getDepartment()).append("\n");
            stats.append(String.format(" - Lương: %,.0f VND\n", topLecturer.payroll()));
        }
        
        stats.append("\nThống kê theo trình độ:\n");
        java.util.Map<String, Long> levelStats = lecturers.stream()
            .collect(java.util.stream.Collectors.groupingBy(Lecturer::getLevel, 
                     java.util.stream.Collectors.counting()));
        
        levelStats.forEach((level, count) -> {
            stats.append(" - ").append(level).append(": ").append(count).append(" giảng viên\n");
        });

        stats.append("\nThống kê theo khoa:\n");
        java.util.Map<String, Long> departmentStats = lecturers.stream()
            .collect(java.util.stream.Collectors.groupingBy(Lecturer::getDepartment, 
                     java.util.stream.Collectors.counting()));
        
        departmentStats.forEach((department, count) -> {
            stats.append(" - ").append(department).append(": ").append(count).append(" giảng viên\n");
        });

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Thống Kê Giảng Viên", JOptionPane.INFORMATION_MESSAGE);
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
        setTitle("Quản lý Giảng viên");

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CHƯƠNG TRÌNH QUẢN LÝ GIẢNG VIÊN");

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
                "Mã GV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Số giờ dạy", "Trình độ", "Lương"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        jLabel2.setText("Mã GV:");

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
        jLabel8.setText("Số giờ dạy:");

        jTextField7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Trình độ:");

        jTextField8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnAdd1.setText("Thêm");

        btnEdit.setText("Sửa");

        btnDelete.setText("Xoá");

        btnSave.setText("Lưu");

        btnCancel.setText("Huỷ");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Tìm theo mã:");

        txtSearchById.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnSearchById.setText("Tìm theo mã");

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
                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFLecturer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new JFLecturer().setVisible(true);
        });
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