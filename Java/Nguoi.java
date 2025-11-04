package Java;

import java.time.LocalDate;

public abstract class Nguoi
{
     String CCCD, HoTen;
     LocalDate NgaySinh; 

     public Nguoi(String CCCD, String HoTen, LocalDate NgaySinh)
     {
        this.CCCD=CCCD;
        this.HoTen=HoTen;
        this.NgaySinh=NgaySinh;
     }

     abstract public int TinhLuong();

     public void HienThi()
     {
        System.out.println(CCCD);
        System.out.println(HoTen);
        System.out.println(NgaySinh);
     }



}