package Java;

import java.time.LocalDate;

public class SinhVienDacBiet extends SinhVien{
    private int MienGiamHocPhi;

    public SinhVienDacBiet(String CCCD, String HoTen, LocalDate NgaySinh, String MSSV, String Khoa, String Lop,int TinChiTichLuy, String Khoas, int MienGiamHocPhi)

    {
        super(CCCD,HoTen,NgaySinh,MSSV,Khoa,Lop,TinChiTichLuy,Khoas);
        this.MienGiamHocPhi=MienGiamHocPhi;

    }

 
    public int HocPhi()
    {
        return TinhHocPhi()-MienGiamHocPhi;
    }

}
