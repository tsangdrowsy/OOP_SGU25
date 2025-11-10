package ManageSchool.model;

public class SpecialStudent extends Student {
    public float rateScholarship;
    public float getRateScholarship() {
        return rateScholarship;
    }
    public void setRateScholarship(float rateScholarship) {
        this.rateScholarship = rateScholarship;
    }

    
    public SpecialStudent(String name, int age, String humanid, String dateOfBirth, String sex, String studentID,
            String department, String major, int credits, float rateScholarship) {
        super(name, age, humanid, dateOfBirth, sex, studentID, department, major, credits);
        this.rateScholarship = rateScholarship;
    }
   public double payroll() {
        return super.payroll() - super.payroll() * rateScholarship;
    }
}
