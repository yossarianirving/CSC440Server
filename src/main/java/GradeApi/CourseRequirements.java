package GradeApi;

public class CourseRequirements {

    public String genEdE1;
    public String genEdE2;
    public String genEdE3;
    public String genEdE4;
    public String genEdE5;
    public String genEdE6;
    public String writingIntensive;
    public String acct;
    public String[] core;
    public String[] supporting;
    public String[] concentration;
    // etc...

    CourseRequirements(String genEdE1, String genEdE2, String genEdE3, String genEdE4, String genEdE5, String genEdE6, String writingIntensive, String upperDivisionHours,
                       String acct, String[] core, String[] supporting, String[] concentration) {
        this.genEdE1 = genEdE1;
        this.genEdE2 = genEdE2;
        this.genEdE3 = genEdE3;
        this.genEdE4 = genEdE4;
        this.genEdE5 = genEdE5;
        this.genEdE6 = genEdE6;
        this.writingIntensive = writingIntensive;
        this.acct = acct;
        this.core = core;
        this.supporting = supporting;
        this.concentration = concentration;
    }
}
