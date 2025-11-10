package ManageSchool.model;

public class AcademicAdvisor extends Lecturer {
    private String advisoryClass;

    public String getAdvisoryClass() {
        return advisoryClass;
    }

    public void setAdvisoryClass(String advisoryClass) {
        this.advisoryClass = advisoryClass;
    }

    public AcademicAdvisor(String name, int age, String humanid, String dateOfBirth, String sex,
                           String department, String lecturerID, float teachingHours, String level,
                           String advisoryClass) {
        super(name, age, humanid, dateOfBirth, sex, department, lecturerID, teachingHours, level);
        this.advisoryClass = advisoryClass;
    }

    @Override
   public double payroll() {
        return super.payroll() + 2000000.0;
    }
 
}
