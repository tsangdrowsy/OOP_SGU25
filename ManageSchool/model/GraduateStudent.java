package ManageSchool.model;

public class GraduateStudent extends Student {
    private String projectID;
    private String projectName;
    private String supervisorID;

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSupervisorID() {
        return supervisorID;
    }

    public void setSupervisorID(String supervisorID) {
        this.supervisorID = supervisorID;
    }


    public GraduateStudent(String name, int age, String humanid, String dateOfBirth, String sex, String studentID,
            String department, String major, int credits, String projectID, String projectName, String supervisorID) {
        super(name, age, humanid, dateOfBirth, sex, studentID, department, major, credits);
        this.projectID = projectID;
        this.projectName = projectName;
        this.supervisorID = supervisorID;
    }

    @Override
    double payroll() {
        return super.payroll() + super.payroll() * 0.5;
    }
}
