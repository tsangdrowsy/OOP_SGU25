package Java;

import java.time.LocalDate;

public class SinhVien extends Nguoi {
    String MSSV;
    String Khoa;
    String Lop;
    int TinChiTichLuy;
    String Khoas;
    int soTinChiDangKy;

    public SinhVien(String CCCD, String HoTen, LocalDate NgaySinh, String MSSV, String Khoa, String Lop,
            int TinChiTichLuy, String Khoas)

    {
        super(CCCD, HoTen, NgaySinh);
        this.MSSV = MSSV;
        this.Khoa = Khoa;
        this.Lop = Lop;
        this.TinChiTichLuy = TinChiTichLuy;
        this.Khoas = Khoas;
    }

    @Override
    public int TinhLuong() {
        return TinhHocPhi();
    }

    public int TinhHocPhi() {
        return soTinChiDangKy * 900000;
    }
}
