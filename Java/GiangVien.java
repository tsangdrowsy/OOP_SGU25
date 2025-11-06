package Java;

import java.time.LocalDate;

public class GiangVien extends Nguoi {

    private String MaGV;              // Mã giảng viên
    private LocalDate NgayVaoLam;     // Ngày vào làm
    private int SNN;                  // Số ngày nghỉ
    private final double PhuCap;      // Phụ cấp (hằng số)
    private String ChuyenNganh;       // Chuyên ngành
    private String PhuTrach;          // Phụ trách
    private int PhuCapThamNien;       // Phụ cấp thâm niên

    // Constructor
    public GiangVien(String CCCD,String HoTen,LocalDate NgaySinh,String MaGV,LocalDate NgayVaoLam,int SNN,double PhuCap, String ChuyenNganh,String PhuTrach,int PhuCapThamNien) {
        super(CCCD, HoTen, NgaySinh);
        this.MaGV = MaGV;
        this.NgayVaoLam = NgayVaoLam;
        this.SNN = SNN;
        this.PhuCap = PhuCap;
        this.ChuyenNganh = ChuyenNganh;
        this.PhuTrach = PhuTrach;
        this.PhuCapThamNien = PhuCapThamNien;
    }

    // Getter & Setter
    public String getMaGV() {
        return MaGV;
    }

    public void setMaGV(String maGV) {
        this.MaGV = maGV;
    }

    public LocalDate getNgayVaoLam() {
        return NgayVaoLam;
    }

    public void setNgayVaoLam(LocalDate NgayVaoLam) {
        this.NgayVaoLam = NgayVaoLam;
    }

    public int getSNN() {
        return SNN;
    }

    public void setSNN(int SNN) {
        this.SNN = SNN;
    }

    public double getPhuCap() {
        return PhuCap;
    }

    public String getChuyenNganh() {
        return ChuyenNganh;
    }

    public void setChuyenNganh(String chuyenNganh) {
        this.ChuyenNganh = chuyenNganh;
    }

    public String getPhuTrach() {
        return PhuTrach;
    }

    public void setPhuTrach(String phuTrach) {
        this.PhuTrach = phuTrach;
    }

    public int getPhuCapThamNien() {
        return PhuCapThamNien;
    }

    public void setPhuCapThamNien(int phuCapThamNien) {
        PhuCapThamNien = phuCapThamNien;
    }

    // Tính lương (ví dụ: phụ cấp + phụ cấp thâm niên - tiền phạt ngày nghỉ)
    @Override
    public int TinhLuong() {
        int TienPhat = SNN * 200000; // phạt 200k cho mỗi ngày nghỉ
        return (int) (PhuCap + PhuCapThamNien - TienPhat);
    }

    // Hiển thị thông tin giảng viên
    @Override
    public void HienThi() {
        super.HienThi();
        System.out.println("Mã GV: " + MaGV);
        System.out.println("Ngày vào làm: " + NgayVaoLam);
        System.out.println("Số ngày nghỉ (SNN): " + SNN);
        System.out.println("Phụ cấp: " + PhuCap);
        System.out.println("Chuyên ngành: " + ChuyenNganh);
        System.out.println("Phụ trách: " + PhuTrach);
        System.out.println("Phụ cấp thâm niên: " + PhuCapThamNien);
        System.out.println("Lương thực nhận: " + TinhLuong() + " VND");
    }
}
