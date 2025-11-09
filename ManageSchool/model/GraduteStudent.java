package ManageSchool.model;

public class GraduateStudent extends Student {
    private String projectID;
    private String projectName;

    public GraduateStudent(String name, int age, String humanid, String dateOfBirth, String sex,
                           String studentID, String department, String major, int credits,
                           String projectID, String projectName) {
        super(name, age, humanid, dateOfBirth, sex, studentID, department, major, credits);
        this.projectID = projectID;
        this.projectName = projectName;
    }

    @Override
    double payroll() {
        // Phụ cấp thêm cho sinh viên cao học
        return super.payroll() + 2000000.0;
    }
}
