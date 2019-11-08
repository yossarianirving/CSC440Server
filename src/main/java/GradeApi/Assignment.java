package GradeApi;

public class Assignment {

    private String title;
    private double weight;
    private double grade;
    private int courseID;

    public Assignment(String title, double weight, double grade, int courseID) {
        this.title = title;
        this.weight = weight;
        this.grade = grade;
        this.courseID = courseID;
    }

    @Override
    public String toString() {
        return String.format(
                "Assignment[title='%s', weight=%f, grade=%f, courseID=%d]",
                title, weight, grade, courseID);
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



