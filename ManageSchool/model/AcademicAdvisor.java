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
    double payroll() {
        return super.payroll() + 2000000.0;
    }
    public void display() {
        System.out.println("----- Academic Advisor Information -----");
        System.out.println("Name: " + getName());
        System.out.println("Age: " + getAge());
        System.out.println("CCCD: " + getHumanid());
        System.out.println("Date of Birth: " + getDateOfBirth());
        System.out.println("Sex: " + getSex());
        System.out.println("Department: " + getDepartment());
        System.out.println("Lecturer ID: " + getLecturerID());
        System.out.println("Teaching Hours: " + getTeachingHours());
        System.out.println("Level: " + getLevel());
        System.out.println("Advisory Class: " + advisoryClass);
        System.out.println("Payroll: " + payroll());
        System.out.println("----------------------------------------");
    }
}
