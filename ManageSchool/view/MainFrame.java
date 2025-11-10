package ManageSchool.view;

public class MainFrame {
    public static void main(String[] args) {
        // Gọi JFStudent thay vì các class khác
        java.awt.EventQueue.invokeLater(() -> {
            new JFStudent().setVisible(true);
        });
    }
}