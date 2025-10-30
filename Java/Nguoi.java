package Java;

import java.sql.Date;

public abstract class Nguoi
{
     String CCCD, HoTen;
     Date NgaySinh; 

     public Nguoi(String CCCD, String HoTen, Date NgaySinh)
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