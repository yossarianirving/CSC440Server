package GradeApi;

public class Course {
    public int id;
    public String description;
    public String course;
    public int hours;
    public String status;

    Course(int id, String description, String course, int hours, String status) {
        this.id = id;
        this.description = description;
        this.course = course;
        this.hours = hours;
        this.status = status;
    }
}
