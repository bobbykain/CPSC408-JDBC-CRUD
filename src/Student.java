// CPSC 408
// Lab 3
// Bobby Kain

public class Student {
    private int id;
    private String fName;
    private String lName;
    private double gpa;
    private String major;
    private String advisor;

    public Student(int id, String fName, String lName, double gpa, String major, String advisor) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.gpa = gpa;
        this.major = major;
        this.advisor = advisor;
    }

    // getters
    public int getId() { return id; }
    public String getfName() { return fName; }
    public String getlName() { return lName; }
    public double getGpa() { return gpa; }
    public String getMajor() { return major; }
    public String getAdvisor() { return advisor; }
}
