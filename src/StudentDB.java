// CPSC 408
// Lab 3
// Bobby Kain

import java.sql.*;
import java.util.Scanner;

public class StudentDB {
    private Connection conn;
    private PreparedStatement ps;

    public StudentDB() {
        try {
            // connecting to db stuff
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to db...");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudentDB?serverTimezone=UTC", "root", "mei");
            System.out.println(("Connect to db successfully!"));
        } catch (Exception e) {
            System.out.println("Error connecting to db:");
            e.printStackTrace();
        }
    }

    // main handles showing user all available options and asking for inputs
    public static void main(String[] args) {
        StudentDB studentDB = new StudentDB();
        while (true) {
            System.out.println();
            System.out.println("a) Display all students and info");
            System.out.println("b) Create student");
            System.out.println("c) Update student");
            System.out.println("d) Delete student");
            System.out.println("e) Search students");
            System.out.println("q) Quit");

            String input = getString("Enter choice: ");
            System.out.println();
            switch (input) {
                // a. Display All Students and all their attributes.
                case "a":
                    studentDB.displayStudents();
                    break;
                // b. Create Students
                case "b":
                    int id = getInt("Enter StudentID: ");
                    String firstName = getString("Enter FirstName: ");
                    String lastName = getString("Enter LastName: ");
                    double gpa = getDouble("Enter GPA: ");
                    String major = getString("Enter Major: ");
                    String fa = getString("Enter FacultyAdvisor: ");
                    Student s = new Student(id, firstName, lastName, gpa, major, fa);
                    studentDB.insertStudent(s);
                    break;
                // c. Update Students
                case "c":
                    int idUp = getInt("Enter StudentID to update: ");
                    System.out.println("a) Update Major");
                    System.out.println("b) Update FacultyAdvisor");
                    input = getString("Enter your choice: ");
                    System.out.println();
                    switch (input) {
                        case "a":
                            String newMajor = getString("Enter new Major: ");
                            studentDB.updateStudent(idUp, 0, newMajor);
                            break;
                        case "b":
                            String newFA = getString("Enter new FacultyAdvisor: ");
                            studentDB.updateStudent(idUp, 1, newFA);
                            break;
                        default:
                            System.out.println("Invalid input");
                    }
                    break;
                // d. Delete Students by StudentId
                case "d":
                    id = getInt("Enter StudentId to delete: ");
                    studentDB.deleteStudent(id);
                    break;
                // e. Search/Display students by Major, GPA and Advisor.
                case "e":
                    System.out.println("What would you like to search by?");
                    System.out.println("a) GPA");
                    System.out.println("b) Major");
                    System.out.println("c) FacultyAdvisor");
                    input = getString("Enter choice: ");
                    System.out.println();
                    switch (input) {
                        case "a":
                            double gpaSearch = getDouble("Enter GPA: ");
                            studentDB.displayStudents(gpaSearch);
                            break;
                        case "b":
                            String majorSearch = getString("Enter major: ");
                            studentDB.displayStudents(0, majorSearch);
                            break;
                        case "c":
                            String faSearch = getString("Enter FacultyAdvisor: ");
                            studentDB.displayStudents(1, faSearch);
                            break;
                        default:
                            System.out.println("Invalid input");
                    }
                    break;
                case "q":
                    System.out.println("Bye.");
                    return;
                default :
                    System.out.println("Invalid input");
            }
        }
    }

    // update student by StudentID
    // update_type 0 = Major
    // update_type 1 = Advisor
    private void updateStudent(int id, int update_type, String newVal) {
        String sql = "";
        try {
            if (update_type == 0) {
                sql = "UPDATE Student SET Major = ? WHERE StudentID = ?";
            }
            else {
                sql = "UPDATE Student SET FacultyAdvisor = ? WHERE StudentID = ?";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, newVal);
            ps.setInt(2, id);
            System.out.println("Updating student...");
            ps.executeUpdate();
            System.out.println("Student updated succesfully");
        } catch (Exception e) {
            System.out.println("Update not successful:");
            e.printStackTrace();
        }
    }

    // delete student by StudentID
    private void deleteStudent(int id) {
        try {
            String sql = "DELETE FROM Student WHERE StudentID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            System.out.println("Deleting student...");
            ps.executeUpdate();
            System.out.println("Student deleted successfully");
        } catch (Exception e) {
            System.out.println("Student not deleted successfully");
            e.printStackTrace();
        }
    }

    // insert a new Student in db
    private void insertStudent(Student s) {
        try {
            String sql = "INSERT INTO Student"
                       + "(StudentID, FirstName, LastName, GPA, Major, FacultyAdvisor) VALUES"
                       + "(?,?,?,?,?,?)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getId());
            ps.setString(2, s.getfName());
            ps.setString(3, s.getlName());
            ps.setDouble(4, s.getGpa());
            ps.setString(5, s.getMajor());
            ps.setString(6, s.getAdvisor());
            System.out.println("Inserting student...");
            ps.executeUpdate();
            System.out.println("Student inserted succesfully!");
        } catch (Exception e) {
            System.out.println("Student not inserted:");
            e.printStackTrace();
        }
    }

    // display all students in db
    private void displayStudents() {
        try {
            String sql = "SELECT * FROM Student";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            displayResultSet(rs);
            rs.close();
        } catch (Exception e) {
            System.out.println("Error selecting from Student table:");
            e.printStackTrace();
        }
    }

    // display all students with a certain Major/FacultyAdvisor
    // display_type 0 = Major
    // display_type 1 = Advisor
    private void displayStudents(int display_type, String search) {
        String sql = "";
        try {
            if (display_type == 0) {
                sql = "SELECT * FROM Student WHERE Major = ? ";
            }
            else {
                sql = "SELECT * FROM Student WHERE FacultyAdvisor = ?";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, search);
            ResultSet rs = ps.executeQuery();
            displayResultSet(rs);
            rs.close();
        } catch (Exception e) {
            System.out.println("Error selecting from Student table:");
            e.printStackTrace();
        }
    }

    // display all students with a certain gpa
    private void displayStudents(double gpaSearch) {
        try {
            String sql = "SELECT * FROM Student WHERE GPA = ?";
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, gpaSearch);
            ResultSet rs = ps.executeQuery();
            displayResultSet(rs);
            rs.close();
        } catch (Exception e) {
            System.out.println("Error selecting from Student table:");
            e.printStackTrace();
        }
    }

    // print a ResultSet
    private static void displayResultSet(ResultSet rs) {
        try {
            System.out.println("Displaying students...");
            while (rs.next()) {
                int id = rs.getInt("StudentID");
                String fName = rs.getString("FirstName");
                String lName = rs.getString("LastName");
                float gpa = rs.getFloat("GPA");
                String major = rs.getString("Major");
                String advisor = rs.getString("FacultyAdvisor");

                System.out.println();
                System.out.println("StudentID: " + id);
                System.out.println("FirstName: " + fName);
                System.out.println("LastName: " + lName);
                System.out.println("GPA: " + gpa);
                System.out.println("Major: " + major);
                System.out.println("Advisor: " + advisor);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Error selecting from Student table:");
            e.printStackTrace();
        }
    }

    // return user input as String
    private static String getString(String prompt) {
        System.out.print(prompt);
        Scanner keys = new Scanner(System.in);
        return keys.nextLine();
    }

    // return user input as double
    private static double getDouble(String prompt) {
        while (true) {
            String input = getString(prompt);
            try {
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Please enter a double");
            }
        }
    }

    // return user input as int
    private static int getInt(String prompt) {
        while (true) {
            String input = getString(prompt);
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Please enter an integer");
            }
        }
    }
}
