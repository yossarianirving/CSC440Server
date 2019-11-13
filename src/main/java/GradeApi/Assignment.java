package GradeApi;

public class Assignment {

    private int id;
    private String title;
    private double weight;
    private double grade;
    private int courseID;


    public Assignment(int id, String title, double weight, double grade, int courseID) {
        this.id = id;
        this.title = title;
        this.weight = weight;
        this.grade = grade;
        this.courseID = courseID;
    }


    public Object[] toObjectArray() {
        return new Object[]{getId(), getTitle(), getWeight(), getGrade(), getCourseID()};
    }

    @Override
    public String toString() {
        return String.format(
                "Assignment[id=%d, title='%s', weight=%f, grade=%f, courseID=%d]",
                id, title, weight, grade, courseID);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}



