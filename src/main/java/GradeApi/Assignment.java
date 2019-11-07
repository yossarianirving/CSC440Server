package GradeApi;

public class Assignment {

    private String title;
    private int courseID;
    private double weight;
    private double grade;

    public Assignment(String title, int courseID, double weight, double grade) {
        this.title = title;
        this.courseID = courseID;
        this.weight = weight;
        this.grade = grade;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }
}



