package Java;

import java.time.LocalDate;

public class GiangVienThinhGiang extends GiangVien {
    private String ThuocCoQuan;

    public GiangVienThinhGiang(
        String CCCD, String HoTen, LocalDate NgaySinh,
        String MaGV, LocalDate NgayVaoLam, int SNN,
        double PhuCap, String ChuyenNganh, String PhuTrach,
        int PhuCapThamNien, String ThuocCoQuan
    ) {
        super(CCCD, HoTen, NgaySinh, MaGV, NgayVaoLam, SNN, PhuCap, ChuyenNganh, PhuTrach, PhuCapThamNien);
        this.ThuocCoQuan = ThuocCoQuan;
    }

    public String getThuocCoQuan() {
        return ThuocCoQuan;
    }

    public void setThuocCoQuan(String thuocCoQuan) {
        ThuocCoQuan = thuocCoQuan;
    }

    @Override
    public int TinhLuong() {
        return super.TinhLuong(); // có thể thêm hệ số riêng nếu cần
    }

    @Override
    public void HienThi() {
        super.HienThi();
        System.out.println("Thuộc cơ quan: " + ThuocCoQuan);
    }
}
