package ManageSchool.model;
public class Student extends Human 
{
    private String studentID;
    private String department;
private int credits;
    private String major;
    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMajor() {
        return major;
    }
    public void setMajor(String major) {
        this.major = major;
    }
    public Student(String name, int age, String humanid, String dateOfBirth, String sex, String studentID,
            String department, String major,int credits) {
        super(name, age, humanid, dateOfBirth, sex);
        this.studentID = studentID;
        this.department = department;
        this.major = major;
        this.credits=credits;
    }
    @Override
    double payroll() {
        return 860000.0*credits;
    }
}