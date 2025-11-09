package ManageSchool.model;

public class ViceDean extends Lecturer {
    public String positive;
    public String getPositive() {
        return positive;
    }
    public void setPositive(String positive) {
        this.positive = positive;
    }
    public ViceDean(String name, int age, String humanid, String dateOfBirth, String sex, String department,
            String lecturerID, float teachingHours, String level, String positive) {
        super(name, age, humanid, dateOfBirth, sex, department, lecturerID, teachingHours, level);
        this.positive = positive;
    }
    double payroll(){


    return super.payroll() + super.payroll()*0.6;
    }
}
