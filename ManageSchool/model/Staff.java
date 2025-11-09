package ManageSchool.model;
public class Staff extends Human {

    private String staffID;
    private String position;
    private float workingDays;
    private float salaryCoefficient;

    public String getStaffID() {
        return staffID;
    }
    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public float getWorkingDays() {
        return workingDays;
    }
    public void setWorkingDays(float workingDays) {
        this.workingDays = workingDays;
    }

    public float getSalaryCoefficient() {
        return salaryCoefficient;
    }
    public void setSalaryCoefficient(float salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }
    public Staff(String name, int age, String humanid, String dateOfBirth, String sex, String staffID, String position,
            float workingDays, float salaryCoefficient) {
        super(name, age, humanid, dateOfBirth, sex);
        this.staffID = staffID;
        this.position = position;
        this.workingDays = workingDays;
        this.salaryCoefficient = salaryCoefficient;
    }
    
    @Override
    double payroll() {
        return salaryCoefficient * 2340000.0 + workingDays * 100000.0;
    }
}