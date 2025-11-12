package ManageSchool.view;

import ManageSchool.model.*;
import ManageSchool.service.ManageStaff;
import ManageSchool.service.StatisticStaff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class JFStaff extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JFStaff.class.getName());
    private ManageStaff manageStaff = new ManageStaff();
    private DefaultTableModel tableModel;
    private boolean isEditing = false;
    private Staff currentStaff = null;
    private javax.swing.JButton btnImportJson;

    public JFStaff() {
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
        btnImportJson.addActionListener(evt -> btnImportJsonActionPerformed());
        btnBackToMenu.addActionListener(evt -> btnBackToMenuActionPerformed());

        txtSearch.addActionListener(evt -> btnSearchActionPerformed());
        txtSearchById.addActionListener(evt -> btnSearchByIdActionPerformed());
    }

    private void initTable() {
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);

        String[] columns = { "Mã NV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Chức vụ", "Số ngày làm",
                "Hệ số lương", "Lương" };
        tableModel.setColumnIdentifiers(columns);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                selectStaffFromTable();
            }
        });
    }

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

    private void btnImportJsonActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file JSON để import");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                List<Staff> staffList = loadStaffFromJsonFile(selectedFile);

                if (!staffList.isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "Bạn có muốn xóa dữ liệu cũ trước khi import? \nChọn 'Yes' để xóa dữ liệu cũ, 'No' để thêm vào dữ liệu hiện có.",
                            "Xác nhận Import",
                            JOptionPane.YES_NO_CANCEL_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        // Xóa dữ liệu cũ và thêm mới
                        manageStaff.clearAll();
                        for (Staff staff : staffList) {
                            manageStaff.add(staff);
                        }
                        JOptionPane.showMessageDialog(this,
                                "Đã import " + staffList.size() + " nhân viên (đã xóa dữ liệu cũ)");
                    } else if (option == JOptionPane.NO_OPTION) {
                        // Thêm vào dữ liệu hiện có
                        int addedCount = 0;
                        for (Staff staff : staffList) {
                            if (manageStaff.findByID(staff.getStaffID()) == null) {
                                manageStaff.add(staff);
                                addedCount++;
                            }
                        }
                        JOptionPane.showMessageDialog(this,
                                "Đã thêm " + addedCount + " nhân viên mới vào dữ liệu hiện có");
                    }

                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu nhân viên nào được tìm thấy trong file!");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi import file JSON: " + e.getMessage(),
                        "Lỗi Import", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private List<Staff> loadStaffFromJsonFile(java.io.File jsonFile) {
        List<Staff> staffList = new ArrayList<>();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(jsonFile),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            staffList = parseJsonManually(jsonContent.toString());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file: " + e.getMessage(), e);
        }

        return staffList;
    }

    private List<Staff> parseJsonManually(String jsonString) {
        List<Staff> staffList = new ArrayList<>();

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
                Staff staff = parseStaffObject(objStr);
                if (staff != null) {
                    staffList.add(staff);
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi parse JSON: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi parse JSON: " + e.getMessage());
        }

        return staffList;
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

    private Staff parseStaffObject(String jsonObject) {
        try {
            // Loại bỏ dấu ngoặc nhọn
            String content = jsonObject.substring(1, jsonObject.length() - 1);

            System.out.println("Parsing object: " + content); // Debug

            // Parse các trường - SỬA LẠI THEO ĐÚNG CẤU TRÚC JSON
            String name = getJsonFieldValue(content, "name");
            int age = Integer.parseInt(getJsonFieldValue(content, "age"));
            String humanid = getJsonFieldValue(content, "humanid");
            String dateOfBirth = getJsonFieldValue(content, "dateOfBirth");
            String sex = getJsonFieldValue(content, "sex");
            String position = getJsonFieldValue(content, "position"); // ĐÃ SỬA: từ "department" thành "position"
            String staffID = getJsonFieldValue(content, "staffID");

            // Parse số thực - thêm xử lý lỗi
            float workingDays = 0;
            float salaryCoefficient = 0;

            try {
                String workingDaysStr = getJsonFieldValue(content, "workingDays");
                workingDays = Float.parseFloat(workingDaysStr);
            } catch (Exception e) {
                System.err.println("Lỗi parse workingDays, gán giá trị mặc định 0");
                workingDays = 0;
            }

            try {
                String salaryCoefficientStr = getJsonFieldValue(content, "salaryCoefficient");
                salaryCoefficient = Float.parseFloat(salaryCoefficientStr);
            } catch (Exception e) {
                System.err.println("Lỗi parse salaryCoefficient, gán giá trị mặc định 0");
                salaryCoefficient = 0;
            }

            return new Staff(name, age, humanid, dateOfBirth, sex, staffID, position, workingDays, salaryCoefficient);

        } catch (Exception e) {
            System.err.println("Lỗi parse staff object: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug
            return null;
        }
    }

    private String getJsonFieldValue(String jsonContent, String fieldName) {
        try {
            String searchStr = "\"" + fieldName + "\":";
            int startIndex = jsonContent.indexOf(searchStr);
            if (startIndex == -1) {
                System.err.println("Không tìm thấy field: " + fieldName + " trong: " + jsonContent);
                throw new IllegalArgumentException("Không tìm thấy field: " + fieldName);
            }

            startIndex += searchStr.length();

            // Tìm vị trí kết thúc chính xác hơn
            int endIndex = -1;
            int braceCount = 0;
            int bracketCount = 0;

            for (int i = startIndex; i < jsonContent.length(); i++) {
                char c = jsonContent.charAt(i);
                if (c == '{')
                    braceCount++;
                if (c == '}')
                    braceCount--;
                if (c == '[')
                    bracketCount++;
                if (c == ']')
                    bracketCount--;

                if (braceCount == 0 && bracketCount == 0 && (c == ',' || c == '}')) {
                    endIndex = i;
                    break;
                }
            }

            if (endIndex == -1) {
                endIndex = jsonContent.length();
            }

            String valueStr = jsonContent.substring(startIndex, endIndex).trim();

            System.out.println("Field " + fieldName + " value: " + valueStr); // Debug

            // Xử lý giá trị string (loại bỏ dấu ngoặc kép)
            if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                return valueStr.substring(1, valueStr.length() - 1);
            }

            return valueStr;
        } catch (Exception e) {
            System.err.println("Lỗi khi parse field '" + fieldName + "': " + e.getMessage());
            throw e;
        }
    }

    private void loadDataToTable() {
        loadDataToTable(manageStaff.getAll());
    }

    private void loadDataToTable(List<Staff> staffList) {
        tableModel.setRowCount(0);

        for (Staff staff : staffList) {
            tableModel.addRow(new Object[] {
                    staff.getStaffID(),
                    staff.getName(),
                    staff.getHumanid(),
                    staff.getDateOfBirth(),
                    staff.getSex(),
                    staff.getPosition(),
                    staff.getWorkingDays(),
                    staff.getSalaryCoefficient(),
                    String.format("%,.0f VND", staff.payroll())
            });
        }
    }

    private void selectStaffFromTable() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            String staffID = tableModel.getValueAt(selectedRow, 0).toString();
            currentStaff = manageStaff.findByID(staffID);

            if (currentStaff != null) {
                fillFormWithStaff(currentStaff);
                setButtonStates(false, true, true, false);
            }
        }
    }

    private void fillFormWithStaff(Staff staff) {
        jTextField1.setText(staff.getStaffID());
        jTextField2.setText(staff.getName());
        jTextField3.setText(staff.getHumanid());
        jTextField4.setText(staff.getDateOfBirth());
        jTextField5.setText(staff.getSex());
        jTextField6.setText(staff.getPosition());
        jTextField7.setText(String.valueOf(staff.getWorkingDays()));
        jTextField8.setText(String.valueOf(staff.getSalaryCoefficient()));
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
        currentStaff = null;
        jTable1.clearSelection();
        setButtonStates(true, false, false, false);
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
    }

    private Staff getStaffFromForm() {
        try {
            String staffID = jTextField1.getText().trim();
            String name = jTextField2.getText().trim();
            String humanID = jTextField3.getText().trim();
            String dateOfBirth = jTextField4.getText().trim();
            String sex = jTextField5.getText().trim();
            String position = jTextField6.getText().trim();
            String daysStr = jTextField7.getText().trim();
            String coefficientStr = jTextField8.getText().trim();

            // Kiểm tra các trường bắt buộc không được để trống
            if (staffID.isEmpty() || name.isEmpty() || humanID.isEmpty() || position.isEmpty()
                    || coefficientStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã NV, Họ tên, CCCD, Chức vụ và Hệ số lương không được để trống!");
                return null;
            }

            // Kiểm tra định dạng ngày sinh
            if (!dateOfBirth.isEmpty()) {
                try {
                    LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!");
                    return null;
                }
            }

            // Kiểm tra số ngày làm
            float workingDays = 0;
            if (!daysStr.isEmpty()) {
                try {
                    workingDays = Float.parseFloat(daysStr);
                    if (workingDays < 0) {
                        JOptionPane.showMessageDialog(this, "Số ngày làm không được âm!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số ngày làm phải là số!");
                    return null;
                }
            }

            float salaryCoefficient = 0;
            try {
                salaryCoefficient = Float.parseFloat(coefficientStr);
                if (salaryCoefficient <= 0) {
                    JOptionPane.showMessageDialog(this, "Hệ số lương phải lớn hơn 0!");
                    return null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Hệ số lương phải là số!");
                return null;
            }

            return new Staff(name, 0, humanID, dateOfBirth, sex, staffID, position, workingDays, salaryCoefficient);

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
        if (currentStaff != null) {
            isEditing = true;
            setButtonStates(false, false, false, true);
            jTextField2.requestFocus();
        }
    }

    private void btnDeleteActionPerformed() {
        if (currentStaff != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xoá nhân viên " + currentStaff.getName() + "?",
                    "Xác nhận xoá", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                manageStaff.remove(currentStaff.getStaffID());
                loadDataToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Đã xoá nhân viên thành công!");
            }
        }
    }

    private void btnSaveActionPerformed() {
        Staff staff = getStaffFromForm();
        if (staff != null) {
            try {
                if (isEditing) {
                    manageStaff.update(staff);
                    JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
                } else {
                    if (manageStaff.findByID(staff.getStaffID()) != null) {
                        JOptionPane.showMessageDialog(this, "Mã NV đã tồn tại!");
                        return;
                    }
                    manageStaff.add(staff);
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
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
        if (currentStaff != null && isEditing) {
            fillFormWithStaff(currentStaff);
        } else {
            clearForm();
        }
        setButtonStates(true, currentStaff != null, currentStaff != null, false);
        isEditing = false;
    }

    // ========== TÌM KIẾM THEO TÊN ==========
    private void btnSearchActionPerformed() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        List<Staff> searchResults = manageStaff.findByName(keyword);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên với từ khóa: " + keyword);
        } else {
            loadDataToTable(searchResults);
            JOptionPane.showMessageDialog(this,
                    "Tìm thấy " + searchResults.size() + " nhân viên phù hợp");
        }
    }

    // ========== TÌM KIẾM THEO ID ==========
    private void btnSearchByIdActionPerformed() {
        String staffID = txtSearchById.getText().trim();
        if (staffID.isEmpty()) {
            loadDataToTable();
            return;
        }

        Staff staff = manageStaff.findByID(staffID);
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên với mã: " + staffID);
        } else {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[] {
                    staff.getStaffID(),
                    staff.getName(),
                    staff.getHumanid(),
                    staff.getDateOfBirth(),
                    staff.getSex(),
                    staff.getPosition(),
                    staff.getWorkingDays(),
                    staff.getSalaryCoefficient(),
                    String.format("%,.0f VND", staff.payroll())
            });

            currentStaff = staff;
            fillFormWithStaff(staff);
            setButtonStates(false, true, true, false);

            JOptionPane.showMessageDialog(this,
                    "Đã tìm thấy nhân viên: " + staff.getName());
        }
    }

    private void btnStatisticActionPerformed() {
        List<Staff> staffList = manageStaff.getAll();
        if (staffList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để thống kê!");
            return;
        }

        StatisticStaff statistic = new StatisticStaff(staffList);

        StringBuilder stats = new StringBuilder();
        stats.append("===== THỐNG KÊ NHÂN VIÊN =====\n\n");
        stats.append("Tổng số nhân viên: ").append(staffList.size()).append("\n");
        stats.append(String.format("Lương trung bình: %,.0f VND\n", statistic.getAverage()));

        Staff topStaff = statistic.getTopEntity();
        if (topStaff != null) {
            stats.append("Nhân viên có lương cao nhất:\n");
            stats.append(" - ").append(topStaff.getName()).append(" (").append(topStaff.getPosition()).append(")\n");
            stats.append(" - Mã NV: ").append(topStaff.getStaffID()).append("\n");
            stats.append(" - Hệ số lương: ").append(topStaff.getSalaryCoefficient()).append("\n");
            stats.append(String.format(" - Lương: %,.0f VND\n", topStaff.payroll()));
        }

        stats.append("\nThống kê theo chức vụ:\n");
        java.util.Map<String, Long> positionStats = staffList.stream()
                .collect(java.util.stream.Collectors.groupingBy(Staff::getPosition,
                        java.util.stream.Collectors.counting()));

        positionStats.forEach((position, count) -> {
            stats.append(" - ").append(position).append(": ").append(count).append(" nhân viên\n");
        });

        stats.append("\nThống kê theo hệ số lương:\n");
        java.util.Map<String, Long> coefficientStats = staffList.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> String.format("%.1f - %.1f",
                                Math.floor(s.getSalaryCoefficient()),
                                Math.floor(s.getSalaryCoefficient()) + 0.9),
                        java.util.stream.Collectors.counting()));

        coefficientStats.forEach((range, count) -> {
            stats.append(" - ").append(range).append(": ").append(count).append(" nhân viên\n");
        });

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Thống Kê Nhân Viên", JOptionPane.INFORMATION_MESSAGE);
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
        btnImportJson = new javax.swing.JButton();
        btnBackToMenu = new javax.swing.JButton();
        btnBackToMenu.setText("<< Menu");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản lý Nhân viên");

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CHƯƠNG TRÌNH QUẢN LÝ NHÂN VIÊN");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Tìm theo tên:");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnSearch.setText("Tìm theo tên");

        btnStatistic.setText("Thống kê");

        btnRefresh.setText("Làm mới");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null }
                },
                new String[] {
                        "Mã NV", "Họ Tên", "CCCD", "Ngày Sinh", "Giới Tính", "Chức vụ", "Số ngày làm", "Hệ số lương",
                        "Lương"
                }) {
            Class[] types = new Class[] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class,
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false, false
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
        jLabel2.setText("Mã NV:");

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
        jLabel6.setText("Chức vụ:");

        jTextField6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Số ngày làm:");

        jTextField7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Hệ số lương:");

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

        btnImportJson.setText("Import JSON"); // THÊM DÒNG NÀY

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
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 900,
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
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAdd1)
                                                        .addComponent(btnEdit)
                                                        .addComponent(btnDelete)
                                                        .addComponent(btnBackToMenu)) // THÊM NÚT BACK VÀO ĐÂY
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnSave)
                                                        .addComponent(btnCancel)))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jDesktopPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(
                jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
                jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel2,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jDesktopPane1Layout.setVerticalGroup(
                jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel2,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

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
            java.util.logging.Logger.getLogger(JFStaff.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new JFStaff().setVisible(true);
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
    private javax.swing.JButton btnBackToMenu;
    // End of variables declaration
}