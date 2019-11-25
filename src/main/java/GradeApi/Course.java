package GradeApi;

public class Course {
    public int id;
    public String title;
    public String requirementSatisfaction;
    public double credits;
    public String semesterTaken;
    public int yearTaken;
    public String finalGrade;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequirementSatisfaction() {
        return requirementSatisfaction;
    }

    public void setRequirementSatisfaction(String requirementSatisfaction) {
        this.requirementSatisfaction = requirementSatisfaction;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public String getSemesterTaken() {
        return semesterTaken;
    }

    public void setSemesterTaken(String semesterTaken) {
        this.semesterTaken = semesterTaken;
    }

    public int getYearTaken() {
        return yearTaken;
    }

    public void setYearTaken(int yearTaken) {
        this.yearTaken = yearTaken;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }

    Course(int id, String title, String requirementSatisfaction, double credits, String semesterTaken, int yearTaken, String finalGrade, String status) {
        this.id = id;
        this.title = title;
        this.requirementSatisfaction = requirementSatisfaction;
        this.credits = credits;
        this.semesterTaken = semesterTaken;
        this.yearTaken = yearTaken;
        this.finalGrade = finalGrade;
        this.status = status;
    }

    public Object[] toObjectArray() {
        return new Object[]{getTitle(), getRequirementSatisfaction(), getCredits(), getSemesterTaken(), getYearTaken(), getFinalGrade(), getStatus()};
    }

    @Override
    public String toString() {
        return String.format(
                "Course[id=%d, title='%s', requirement_satisfaction='%s', credits=%f, semester_taken='%s', year_taken=%d, final_grade='%s']",
                id, title, requirementSatisfaction, credits, semesterTaken, yearTaken, finalGrade);
    }

}
