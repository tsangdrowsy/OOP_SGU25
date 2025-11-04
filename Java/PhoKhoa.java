package Java;

import java.time.LocalDate;

public class PhoKhoa extends GiangVien {
    private int PhuCapPhoKhoa;

    public PhoKhoa(
        String CCCD, String HoTen, LocalDate NgaySinh,
        String MaGV, LocalDate NgayVaoLam, int SNN,
        double PhuCap, String ChuyenNganh, String PhuTrach,
        int PhuCapThamNien, int PhuCapPhoKhoa
    ) {
        super(CCCD, HoTen, NgaySinh, MaGV, NgayVaoLam, SNN, PhuCap, ChuyenNganh, PhuTrach, PhuCapThamNien);
        this.PhuCapPhoKhoa = PhuCapPhoKhoa;
    }

    public int getPhuCapPhoKhoa() {
        return PhuCapPhoKhoa;
    }

    public void setPhuCapPhoKhoa(int phuCapPhoKhoa) {
        PhuCapPhoKhoa = phuCapPhoKhoa;
    }

    @Override
    public int TinhLuong() {
        return super.TinhLuong() + PhuCapPhoKhoa;
    }

    @Override
    public void HienThi() {
        super.HienThi();
        System.out.println("Phụ cấp phó khoa: " + PhuCapPhoKhoa);
    }
}