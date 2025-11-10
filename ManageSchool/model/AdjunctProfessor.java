package ManageSchool.model;

public class AdjunctProfessor extends Lecturer {
    private double rateOfPay;
    private String mainInstitution;
    public double getRateOfPay() {
        return rateOfPay;
    }
    public void setRateOfPay(double rateOfPay) {
        this.rateOfPay = rateOfPay;
    }
    public String getmainInstitution() {
        return mainInstitution;
    }
    public void setmainInstitution(String mainInstitution) {
        this.mainInstitution = mainInstitution;
    }
    public AdjunctProfessor(String name, int age, String humanid, String dateOfBirth, String sex, String department,
            String lecturerID, float teachingHours, String level, double rateOfPay, String mainInstitution) {
        super(name, age, humanid, dateOfBirth, sex, department, lecturerID, teachingHours, level);
        this.rateOfPay = rateOfPay;
        this.mainInstitution = mainInstitution;
    } 
    @Override
  public  double payroll(){
        return getTeachingHours() * rateOfPay;
    }

}
