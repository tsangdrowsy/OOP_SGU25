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
import java.util.ArrayList;
import java.util.List;

public class JFLecturer extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger(JFLecturer.class.getName());
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
        hideAllAdditionalPanels();
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
        btnImportJson.addActionListener(evt -> btnImportJsonActionPerformed());
        btnFilter.addActionListener(evt -> btnFilterActionPerformed());
        btnClearFilter.addActionListener(evt -> btnClearFilterActionPerformed());
        btnBackToMenu.addActionListener(evt -> btnBackToMenuActionPerformed());

        // Enter key listener for search
        txtSearch.addActionListener(evt -> btnSearchActionPerformed());
        txtSearchById.addActionListener(evt -> btnSearchByIdActionPerformed());

        // Listener cho combobox loại giảng viên
        cbLecturerType.addActionListener(evt -> showAdditionalFields());
    }

    // ========== QUAY LẠI MENU CHÍNH ==========
    private void btnBackToMenuActionPerformed() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn quay lại menu chính?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Đóng cửa sổ hiện tại
            new MainFrame().setVisible(true); // Mở lại menu chính
        }
    }

    private void initTable() {
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);

        String[] columns = { "Mã GV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Số giờ dạy", "Trình độ",
                "Loại GV", "Thông tin thêm", "Lương" };
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
            String lecturerType = "";
            String additionalInfo = "";

            if (lecturer instanceof AcademicAdvisor) {
                lecturerType = "Cố vấn học tập";
                additionalInfo = "Lớp: " + ((AcademicAdvisor) lecturer).getAdvisoryClass();
            } else if (lecturer instanceof AdjunctProfessor) {
                lecturerType = "Giảng viên thỉnh giảng";
                additionalInfo = String.format("Tỷ lệ: %,.0f VND/giờ - %s",
                        ((AdjunctProfessor) lecturer).getRateOfPay(),
                        ((AdjunctProfessor) lecturer).getmainInstitution());
            } else if (lecturer instanceof AdministratorLecturer) {
                lecturerType = "Giảng viên quản lý";
                additionalInfo = "Chức vụ: " + ((AdministratorLecturer) lecturer).getPosition();
            } else {
                lecturerType = "Giảng viên thường";
                additionalInfo = "";
            }

            tableModel.addRow(new Object[] {
                    lecturer.getLecturerID(),
                    lecturer.getName(),
                    lecturer.getHumanid(),
                    lecturer.getDateOfBirth(),
                    lecturer.getSex(),
                    lecturer.getDepartment(),
                    lecturer.getTeachingHours(),
                    lecturer.getLevel(),
                    lecturerType,
                    additionalInfo,
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

        // Xác định và thiết lập loại giảng viên
        if (lecturer instanceof AcademicAdvisor) {
            cbLecturerType.setSelectedItem("Cố vấn học tập");
            txtAdvisoryClass.setText(((AcademicAdvisor) lecturer).getAdvisoryClass());
        } else if (lecturer instanceof AdjunctProfessor) {
            cbLecturerType.setSelectedItem("Giảng viên thỉnh giảng");
            txtRateOfPay.setText(String.valueOf(((AdjunctProfessor) lecturer).getRateOfPay()));
            txtMainInstitution.setText(((AdjunctProfessor) lecturer).getmainInstitution());
        } else if (lecturer instanceof AdministratorLecturer) {
            cbLecturerType.setSelectedItem("Giảng viên quản lý");
            txtPosition.setText(((AdministratorLecturer) lecturer).getPosition());
        } else {
            cbLecturerType.setSelectedItem("Giảng viên thường");
        }

        showAdditionalFields();
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
        txtAdvisoryClass.setText("");
        txtRateOfPay.setText("");
        txtMainInstitution.setText("");
        txtPosition.setText("");
        cbLecturerType.setSelectedItem("Giảng viên thường");
        currentLecturer = null;
        jTable1.clearSelection();
        setButtonStates(true, false, false, false);
        hideAllAdditionalPanels();
    }

    private void setButtonStates(boolean addEnabled, boolean editEnabled, boolean deleteEnabled,
            boolean saveCancelEnabled) {
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
        cbLecturerType.setEnabled(editable && !isEditing);
        txtAdvisoryClass.setEditable(editable);
        txtRateOfPay.setEditable(editable);
        txtMainInstitution.setEditable(editable);
        txtPosition.setEditable(editable);
    }

    private void hideAllAdditionalPanels() {
        panelAcademicAdvisor.setVisible(false);
        panelAdjunctProfessor.setVisible(false);
        panelAdministrator.setVisible(false);
    }

    private void showAdditionalFields() {
        hideAllAdditionalPanels();

        String selectedType = cbLecturerType.getSelectedItem().toString();
        switch (selectedType) {
            case "Cố vấn học tập":
                panelAcademicAdvisor.setVisible(true);
                break;
            case "Giảng viên thỉnh giảng":
                panelAdjunctProfessor.setVisible(true);
                break;
            case "Giảng viên quản lý":
                panelAdministrator.setVisible(true);
                break;
        }

        // Cập nhật layout
        revalidate();
        repaint();
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
            String lecturerType = cbLecturerType.getSelectedItem().toString();

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

            // Validation cho từng loại giảng viên
            switch (lecturerType) {
                case "Cố vấn học tập":
                    String advisoryClass = txtAdvisoryClass.getText().trim();
                    if (advisoryClass.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Lớp cố vấn không được để trống!");
                        return null;
                    }
                    return new AcademicAdvisor(name, 0, humanID, dateOfBirth, sex,
                            department, lecturerID, teachingHours, level, advisoryClass);

                case "Giảng viên thỉnh giảng":
                    String rateStr = txtRateOfPay.getText().trim();
                    String mainInstitution = txtMainInstitution.getText().trim();
                    if (rateStr.isEmpty() || mainInstitution.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Tỷ lệ lương và cơ quan chính không được để trống!");
                        return null;
                    }
                    try {
                        double rateOfPay = Double.parseDouble(rateStr);
                        if (rateOfPay <= 0) {
                            JOptionPane.showMessageDialog(this, "Tỷ lệ lương phải lớn hơn 0!");
                            return null;
                        }
                        return new AdjunctProfessor(name, 0, humanID, dateOfBirth, sex,
                                department, lecturerID, teachingHours, level, rateOfPay, mainInstitution);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Tỷ lệ lương phải là số!");
                        return null;
                    }

                case "Giảng viên quản lý":
                    String position = txtPosition.getText().trim();
                    if (position.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Chức vụ không được để trống!");
                        return null;
                    }
                    return new AdministratorLecturer(name, 0, humanID, dateOfBirth, sex,
                            department, lecturerID, teachingHours, level, position);

                default: // Giảng viên thường
                    return new Lecturer(name, 0, humanID, dateOfBirth, sex,
                            department, lecturerID, teachingHours, level);
            }

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

            String lecturerType = "";
            String additionalInfo = "";

            if (lecturer instanceof AcademicAdvisor) {
                lecturerType = "Cố vấn học tập";
                additionalInfo = "Lớp: " + ((AcademicAdvisor) lecturer).getAdvisoryClass();
            } else if (lecturer instanceof AdjunctProfessor) {
                lecturerType = "Giảng viên thỉnh giảng";
                additionalInfo = String.format("Tỷ lệ: %,.0f VND/giờ - %s",
                        ((AdjunctProfessor) lecturer).getRateOfPay(),
                        ((AdjunctProfessor) lecturer).getmainInstitution());
            } else if (lecturer instanceof AdministratorLecturer) {
                lecturerType = "Giảng viên quản lý";
                additionalInfo = "Chức vụ: " + ((AdministratorLecturer) lecturer).getPosition();
            } else {
                lecturerType = "Giảng viên thường";
                additionalInfo = "";
            }

            tableModel.addRow(new Object[] {
                    lecturer.getLecturerID(),
                    lecturer.getName(),
                    lecturer.getHumanid(),
                    lecturer.getDateOfBirth(),
                    lecturer.getSex(),
                    lecturer.getDepartment(),
                    lecturer.getTeachingHours(),
                    lecturer.getLevel(),
                    lecturerType,
                    additionalInfo,
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

    // ========== LỌC THEO LOẠI GIẢNG VIÊN ==========
    private void btnFilterActionPerformed() {
        String selectedType = cbFilterType.getSelectedItem().toString();

        if ("Tất cả".equals(selectedType)) {
            loadDataToTable();
            return;
        }

        List<Lecturer> allLecturers = manageTeacher.getAll();
        List<Lecturer> filteredLecturers = new ArrayList<>();

        for (Lecturer lecturer : allLecturers) {
            boolean matches = false;

            switch (selectedType) {
                case "Giảng viên thường":
                    matches = (lecturer instanceof Lecturer) &&
                            !(lecturer instanceof AcademicAdvisor) &&
                            !(lecturer instanceof AdjunctProfessor) &&
                            !(lecturer instanceof AdministratorLecturer);
                    break;
                case "Cố vấn học tập":
                    matches = lecturer instanceof AcademicAdvisor;
                    break;
                case "Giảng viên thỉnh giảng":
                    matches = lecturer instanceof AdjunctProfessor;
                    break;
                case "Giảng viên quản lý":
                    matches = lecturer instanceof AdministratorLecturer;
                    break;
            }

            if (matches) {
                filteredLecturers.add(lecturer);
            }
        }

        if (filteredLecturers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy giảng viên thuộc loại: " + selectedType);
        } else {
            loadDataToTable(filteredLecturers);
            JOptionPane.showMessageDialog(this,
                    "Tìm thấy " + filteredLecturers.size() + " giảng viên loại: " + selectedType);
        }
    }

    private void btnClearFilterActionPerformed() {
        cbFilterType.setSelectedItem("Tất cả");
        loadDataToTable();
        JOptionPane.showMessageDialog(this, "Đã xóa bộ lọc!");
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

        stats.append("\nThống kê theo loại giảng viên:\n");
        java.util.Map<String, Long> typeStats = lecturers.stream()
                .collect(java.util.stream.Collectors.groupingBy(lec -> {
                    if (lec instanceof AcademicAdvisor)
                        return "Cố vấn học tập";
                    if (lec instanceof AdjunctProfessor)
                        return "Giảng viên thỉnh giảng";
                    if (lec instanceof AdministratorLecturer)
                        return "Giảng viên quản lý";
                    return "Giảng viên thường";
                }, java.util.stream.Collectors.counting()));

        typeStats.forEach((type, count) -> {
            stats.append(" - ").append(type).append(": ").append(count).append(" giảng viên\n");
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
        cbFilterType.setSelectedItem("Tất cả"); // Reset bộ lọc
        clearForm();
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!");
    }

    // ========== IMPORT JSON ==========
    private void btnImportJsonActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file JSON để import");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                List<Lecturer> lecturers = loadLecturersFromJsonFile(selectedFile);

                if (!lecturers.isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "Bạn có muốn xóa dữ liệu cũ trước khi import? \nChọn 'Yes' để xóa dữ liệu cũ, 'No' để thêm vào dữ liệu hiện có.",
                            "Xác nhận Import",
                            JOptionPane.YES_NO_CANCEL_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        // Xóa dữ liệu cũ và thêm mới
                        for (Lecturer lecturer : lecturers) {
                            manageTeacher.remove(lecturer.getLecturerID()); // Xóa nếu tồn tại
                            manageTeacher.add(lecturer);
                        }
                        JOptionPane.showMessageDialog(this,
                                "Đã import " + lecturers.size() + " giảng viên (đã xóa dữ liệu cũ)");
                    } else if (option == JOptionPane.NO_OPTION) {
                        // Thêm vào dữ liệu hiện có
                        int addedCount = 0;
                        for (Lecturer lecturer : lecturers) {
                            if (manageTeacher.findByID(lecturer.getLecturerID()) == null) {
                                manageTeacher.add(lecturer);
                                addedCount++;
                            }
                        }
                        JOptionPane.showMessageDialog(this,
                                "Đã thêm " + addedCount + " giảng viên mới vào dữ liệu hiện có");
                    }

                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu giảng viên nào được tìm thấy trong file!");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi import file JSON: " + e.getMessage(),
                        "Lỗi Import", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private List<Lecturer> loadLecturersFromJsonFile(java.io.File jsonFile) {
        List<Lecturer> lecturers = new ArrayList<>();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(jsonFile),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            lecturers = parseJsonManually(jsonContent.toString());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file: " + e.getMessage(), e);
        }

        return lecturers;
    }

    private List<Lecturer> parseJsonManually(String jsonString) {
        List<Lecturer> lecturers = new ArrayList<>();

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
                Lecturer lecturer = parseLecturerObject(objStr);
                if (lecturer != null) {
                    lecturers.add(lecturer);
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi parse JSON: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi parse JSON: " + e.getMessage());
        }

        return lecturers;
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
            }
        }

        return objects;
    }

    private Lecturer parseLecturerObject(String jsonObject) {
        try {
            // Loại bỏ dấu ngoặc nhọn
            String content = jsonObject.substring(1, jsonObject.length() - 1);

            // Parse các trường cơ bản
            String name = getJsonFieldValue(content, "name");
            int age = Integer.parseInt(getJsonFieldValue(content, "age"));
            String humanID = getJsonFieldValue(content, "humanid");
            String dateOfBirth = getJsonFieldValue(content, "dateOfBirth");
            String sex = getJsonFieldValue(content, "sex");
            String department = getJsonFieldValue(content, "department");
            String lecturerID = getJsonFieldValue(content, "lecturerID");
            float teachingHours = Float.parseFloat(getJsonFieldValue(content, "teachingHours"));
            String level = getJsonFieldValue(content, "level");

            // Xác định loại giảng viên dựa trên các trường đặc biệt
            if (hasJsonField(content, "advisoryClass")) {
                String advisoryClass = getJsonFieldValue(content, "advisoryClass");
                return new AcademicAdvisor(name, age, humanID, dateOfBirth, sex,
                        department, lecturerID, teachingHours, level, advisoryClass);
            }

            if (hasJsonField(content, "rateOfPay")) {
                double rateOfPay = Double.parseDouble(getJsonFieldValue(content, "rateOfPay"));
                String mainInstitution = getJsonFieldValue(content, "mainInstitution");
                return new AdjunctProfessor(name, age, humanID, dateOfBirth, sex,
                        department, lecturerID, teachingHours, level, rateOfPay, mainInstitution);
            }

            if (hasJsonField(content, "position")) {
                String position = getJsonFieldValue(content, "position");
                return new AdministratorLecturer(name, age, humanID, dateOfBirth, sex,
                        department, lecturerID, teachingHours, level, position);
            }

            // Mặc định là giảng viên thường
            return new Lecturer(name, age, humanID, dateOfBirth, sex,
                    department, lecturerID, teachingHours, level);

        } catch (Exception e) {
            System.err.println("Lỗi parse lecturer object: " + e.getMessage());
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
        jLabel12 = new javax.swing.JLabel();
        cbLecturerType = new javax.swing.JComboBox<>();
        panelAcademicAdvisor = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtAdvisoryClass = new javax.swing.JTextField();
        panelAdjunctProfessor = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtRateOfPay = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtMainInstitution = new javax.swing.JTextField();
        panelAdministrator = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtPosition = new javax.swing.JTextField();
        btnImportJson = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        cbFilterType = new javax.swing.JComboBox<>();
        btnFilter = new javax.swing.JButton();
        btnClearFilter = new javax.swing.JButton();
        btnBackToMenu = new javax.swing.JButton();

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

        btnImportJson.setText("Import JSON");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null, null }
                },
                new String[] {
                        "Mã GV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Khoa", "Số giờ dạy", "Trình độ",
                        "Loại GV", "Thông tin thêm", "Lương"
                }) {
            Class[] types = new Class[] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
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

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Loại GV:");

        cbLecturerType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbLecturerType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Giảng viên thường",
                "Cố vấn học tập", "Giảng viên thỉnh giảng", "Giảng viên quản lý" }));

        panelAcademicAdvisor.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin cố vấn học tập"));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Lớp cố vấn:");

        txtAdvisoryClass.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelAcademicAdvisorLayout = new javax.swing.GroupLayout(panelAcademicAdvisor);
        panelAcademicAdvisor.setLayout(panelAcademicAdvisorLayout);
        panelAcademicAdvisorLayout.setHorizontalGroup(
                panelAcademicAdvisorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAcademicAdvisorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAdvisoryClass, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelAcademicAdvisorLayout.setVerticalGroup(
                panelAcademicAdvisorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAcademicAdvisorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelAcademicAdvisorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel13)
                                        .addComponent(txtAdvisoryClass, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        panelAdjunctProfessor
                .setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin giảng viên thỉnh giảng"));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Tỷ lệ lương:");

        txtRateOfPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Cơ quan chính:");

        txtMainInstitution.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelAdjunctProfessorLayout = new javax.swing.GroupLayout(panelAdjunctProfessor);
        panelAdjunctProfessor.setLayout(panelAdjunctProfessorLayout);
        panelAdjunctProfessorLayout.setHorizontalGroup(
                panelAdjunctProfessorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAdjunctProfessorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRateOfPay, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMainInstitution, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelAdjunctProfessorLayout.setVerticalGroup(
                panelAdjunctProfessorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAdjunctProfessorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelAdjunctProfessorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel14)
                                        .addComponent(txtRateOfPay, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel15)
                                        .addComponent(txtMainInstitution, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        panelAdministrator.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin giảng viên quản lý"));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("Chức vụ:");

        txtPosition.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout panelAdministratorLayout = new javax.swing.GroupLayout(panelAdministrator);
        panelAdministrator.setLayout(panelAdministratorLayout);
        panelAdministratorLayout.setHorizontalGroup(
                panelAdministratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAdministratorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelAdministratorLayout.setVerticalGroup(
                panelAdministratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAdministratorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelAdministratorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel16)
                                        .addComponent(txtPosition, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Lọc theo loại:");

        cbFilterType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbFilterType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
                "Tất cả",
                "Giảng viên thường",
                "Cố vấn học tập",
                "Giảng viên thỉnh giảng",
                "Giảng viên quản lý"
        }));

        btnFilter.setText("Lọc");

        btnClearFilter.setText("Xóa lọc");

        btnBackToMenu.setText("<< Menu");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel10)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtSearch,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnSearch)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel11)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtSearchById,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnSearchById)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel17)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cbFilterType,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnFilter)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnClearFilter)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnStatistic)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnRefresh)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnImportJson))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        false)
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel2)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField1,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        150,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel3)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField2))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel4)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField3))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel7)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField4))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel5)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField5))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel6)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField6))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel8)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField7,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        100,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel9)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField8,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        100,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(jLabel12)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(cbLecturerType, 0,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE))
                                                                        .addComponent(panelAcademicAdvisor,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(panelAdjunctProfessor,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(panelAdministrator,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(btnAdd1)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnEdit)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnDelete)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnBackToMenu))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(btnSave)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnCancel)))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 1000,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 10, Short.MAX_VALUE)))
                                .addContainerGap()));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel10)
                                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSearch)
                                        .addComponent(jLabel11)
                                        .addComponent(txtSearchById, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSearchById)
                                        .addComponent(jLabel17)
                                        .addComponent(cbFilterType, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnFilter)
                                        .addComponent(btnClearFilter)
                                        .addComponent(btnStatistic)
                                        .addComponent(btnRefresh)
                                        .addComponent(btnImportJson))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jTextField1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jTextField2,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jTextField3,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel7)
                                                        .addComponent(jTextField4,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jTextField5,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jTextField6,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel8)
                                                        .addComponent(jTextField7,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel9)
                                                        .addComponent(jTextField8,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel12)
                                                        .addComponent(cbLecturerType,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panelAcademicAdvisor,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panelAdjunctProfessor,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panelAdministrator,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAdd1)
                                                        .addComponent(btnEdit)
                                                        .addComponent(btnDelete)
                                                        .addComponent(btnBackToMenu))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnSave)
                                                        .addComponent(btnCancel)))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jDesktopPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
                jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jDesktopPane1Layout.setVerticalGroup(
                jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jDesktopPane1));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jDesktopPane1));

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFLecturer.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new JFLecturer().setVisible(true);
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnAdd1;
    private javax.swing.JButton btnBackToMenu;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnImportJson;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchById;
    private javax.swing.JButton btnStatistic;
    private javax.swing.JComboBox<String> cbFilterType;
    private javax.swing.JComboBox<String> cbLecturerType;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JPanel panelAcademicAdvisor;
    private javax.swing.JPanel panelAdjunctProfessor;
    private javax.swing.JPanel panelAdministrator;
    private javax.swing.JTextField txtAdvisoryClass;
    private javax.swing.JTextField txtMainInstitution;
    private javax.swing.JTextField txtPosition;
    private javax.swing.JTextField txtRateOfPay;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchById;
    // End of variables declaration
}