package ManageSchool.model;

public abstract class Human {
    private String name;
    private int age;
    private String humanid;
    private String dateOfBirth;
    private String sex;

    // Thêm constructor mặc định
    public Human() {
    }

    public Human(String name, int age, String humanid, String dateOfBirth, String sex) {
        this.name = name;
        this.age = age;
        this.humanid = humanid;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
    }

    // Các getter và setter giữ nguyên
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHumanid() {
        return humanid;
    }

    public void setHumanid(String humanid) {
        this.humanid = humanid;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public abstract double payroll();
}