package ManageSchool.model;
public class AdministratorLecturer extends Lecturer {
private String position;
public String getPosition() {
    return position;
}
public void setPosition(String position) {
    this.position = position;
    
}
public AdministratorLecturer(String name, int age, String humanid, String dateOfBirth, String sex, String department,
        String lecturerID, float teachingHours, String level, String position) {
    super(name, age, humanid, dateOfBirth, sex, department, lecturerID, teachingHours, level);
    this.position = position;
}
@Override
public double payroll(){
return super.payroll() + super.payroll()*0.95;
}
}