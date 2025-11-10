package ManageSchool.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Student extends Human {
    private String studentID;
    private String department;
    private int credits;
    private String major;

    public Student(String name, int age, String humanid, String dateOfBirth, String sex,
            String studentID, String department, String major, int credits) {
        super(name, age, humanid, dateOfBirth, sex);
        this.studentID = studentID;
        this.department = department;
        this.major = major;
        this.credits = credits;
    }

    // Default constructor
    public Student() {
        super("", 0, "", "", "");
    }

    // Getters and Setters
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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public double payroll() {
        return 860000.0 * credits;
    }

    // Helper methods để tương thích với service
    public String getCitizenID() {
        return getHumanid();
    }

    public void setCitizenID(String citizenID) {
        setHumanid(citizenID);
    }

    public LocalDate getBirthDate() {
        if (getDateOfBirth() != null && !getDateOfBirth().isEmpty()) {
            try {
                return LocalDate.parse(getDateOfBirth(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (birthDate != null) {
            setDateOfBirth(birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            setDateOfBirth("");
        }
    }

    public String getGender() {
        return getSex();
    }

    public void setGender(String gender) {
        setSex(gender);
    }

    public String getFaculty() {
        return getDepartment();
    }

    public void setFaculty(String faculty) {
        setDepartment(faculty);
    }

    public int getAccumulatedCredits() {
        return getCredits();
    }

    public void setAccumulatedCredits(int accumulatedCredits) {
        setCredits(accumulatedCredits);
    }

    // Trong class Student, thêm method để format học phí
    public String getFormattedPayroll() {
        return String.format("%,.0f VND", payroll());
    }
}