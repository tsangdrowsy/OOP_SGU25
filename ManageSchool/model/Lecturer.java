package ManageSchool.model;

public class Lecturer extends Human {
    private String department;
    private String lecturerID;
    private float teachingHours;
    private String level;

    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getLecturerID() {
        return lecturerID;
    }
    public void setLecturerID(String lecturerID) {
        this.lecturerID = lecturerID;
    }
    public float getTeachingHours() {
        return teachingHours;
    }
    public void setTeachingHours(float teachingHours) {
        this.teachingHours = teachingHours;
    }

    public Lecturer(String name, int age, String humanid, String dateOfBirth, String sex, String department,
            String lecturerID, float teachingHours,String level) {
        super(name, age, humanid, dateOfBirth, sex);
        this.department = department;
        this.lecturerID = lecturerID;
        this.teachingHours = teachingHours;
        this.level=level;
    }
    
    // Thêm default constructor
    public Lecturer() {
        super("", 0, "", "", "");
    }

    @Override
    public double payroll() {  // THÊM TỪ KHÓA PUBLIC Ở ĐÂY
        switch(level != null ? level : "") {
            case "Master":
                return teachingHours * 300000.0;
            case "PhD":
                return teachingHours * 500000.0;
            case "professor":
            case "associate professor":
                return teachingHours * 750000.0;
            default:
                return teachingHours * 200000.0; // Mức lương mặc định
        }
    }
}