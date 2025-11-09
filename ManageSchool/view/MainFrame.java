package ManageSchool.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;




public class MainFrame extends JFrame {
    private JTextField txtID, txtName, txtAge;
    private DefaultTableModel tableModel;
    private JTable table;

    public MainFrame() {
        setTitle("CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));

        inputPanel.add(new JLabel("Mã sinh viên:"));
        txtID = new JTextField();
        inputPanel.add(txtID);

        inputPanel.add(new JLabel("Họ tên sinh viên:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Tuổi sinh viên:"));
        txtAge = new JTextField();
        inputPanel.add(txtAge);

        // Các nút chức năng
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Bảng dữ liệu
        String[] columnNames = { "No", "ID", "Name", "Age" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Thêm dữ liệu mẫu
        Object[][] sampleData = {
                { 1, "1111", "Nguyễn Văn Sơn", 23 },
                { 2, "2222", "Trần Thị Mai", 20 },
                { 3, "3333", "Lê Thảo Mai", 21 },
                { 4, "4444", "Nguyễn Quang Hải", 24 },
                { 5, "5555", "Cấn Văn Minh", 21 }
        };
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }

        // Xử lý sự kiện
        btnAdd.addActionListener(e -> {
            int rowCount = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[] {
                    rowCount,
                    txtID.getText(),
                    txtName.getText(),
                    Integer.parseInt(txtAge.getText())
            });
            clearFields();
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.setValueAt(txtID.getText(), selectedRow, 1);
                tableModel.setValueAt(txtName.getText(), selectedRow, 2);
                tableModel.setValueAt(Integer.parseInt(txtAge.getText()), selectedRow, 3);
                clearFields();
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
                clearFields();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                txtID.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtAge.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void clearFields() {
        txtID.setText("");
        txtName.setText("");
        txtAge.setText("");
    }

    public static void main(String[] args) {
            SwingUtilities.invokeLater(MainFrame::new);
    }



    

}