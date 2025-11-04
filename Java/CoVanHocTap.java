package Java;

import java.time.LocalDate;

public class CoVanHocTap extends GiangVien {
    private String LopChuNhiem;
    private int PhuCap;

    public CoVanHocTap(
        String CCCD, String HoTen, LocalDate NgaySinh,
        String MaGV, LocalDate NgayVaoLam, int SNN,
        double PhuCapGV, String ChuyenNganh, String PhuTrach,
        int PhuCapThamNien, String LopChuNhiem, int PhuCap
    ) {
        super(CCCD, HoTen, NgaySinh, MaGV, NgayVaoLam, SNN, PhuCapGV, ChuyenNganh, PhuTrach, PhuCapThamNien);
        this.LopChuNhiem = LopChuNhiem;
        this.PhuCap = PhuCap;
    }

    public String getLopChuNhiem() {
        return LopChuNhiem;
    }

    public void setLopChuNhiem(String lopChuNhiem) {
        LopChuNhiem = lopChuNhiem;
    }

    public int getPhuCap() {
        return PhuCap;
    }

    public void setPhuCap(int phuCap) {
        PhuCap = phuCap;
    }

    @Override
    public int TinhLuong() {
        return super.TinhLuong() + PhuCap;
    }

    @Override
    public void HienThi() {
        super.HienThi();
        System.out.println("Lớp chủ nhiệm: " + LopChuNhiem);
        System.out.println("Phụ cấp cố vấn: " + PhuCap);
    }
}
