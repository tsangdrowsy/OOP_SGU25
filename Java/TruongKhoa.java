package Java;

import java.time.LocalDate;

public class TruongKhoa extends GiangVien{
    private int LuongThem;

    public TruongKhoa(String CCCD, String HoTen, LocalDate NgaySinh,
        String MaGV, LocalDate NgayVaoLam, int SNN,
        double PhuCap, String ChuyenNganh, String PhuTrach,
        int PhuCapThamNien, int LuongThem)
        {
            super(CCCD,HoTen,NgaySinh,MaGV,NgayVaoLam,SNN,PhuCap,ChuyenNganh,PhuTrach,PhuCapThamNien);
            this.LuongThem=LuongThem;
        }

    public int getLuongThem() {
        return LuongThem;
    }

    public void setLuongThem(int luongThem) {
        LuongThem = luongThem;
    }

    @Override
    public int TinhLuong() {
        return super.TinhLuong() + LuongThem;
    }

    @Override
    public void HienThi() {
        super.HienThi();
        System.out.println("Lương thêm: " + LuongThem);
    }

}