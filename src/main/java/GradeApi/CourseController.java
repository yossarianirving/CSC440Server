package GradeApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Component
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Gets the gen ed element 1 courses that student has taken.
    public Object[] getGenEdE1Remaining() {
        List<String> genEdE1Complete = jdbcTemplate.query(
                "SELECT title FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E1"},
                (rs, rowNum) -> rs.getString(1));


        return genEdE1Complete.toArray();
    }

    // Gets the gen ed element 2 courses that student has taken.
    public Object[] getGenEdE2Remaining() throws Exception {
        List<String> genEdE1Complete = jdbcTemplate.query(
                "SELECT title FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E2"},
                (rs, rowNum) -> rs.getString(1));
        return genEdE1Complete.toArray();
    }


    /*
    assume all concentrations will be:
        General
        Statistical Computing
        Digital Forensics and Cybersecurity
        Computer Technology
        Interactive Multimedia
        Artificial Intelligence in data Science
     */


    /*
    Matthew:
        Remember that course titles are 6 or 7 digits in length.
        Assume that the digits are stored to the left, i.e.: "CSC190 ", instead of " CSC190" or "CSC 190".
        Other requirements to check for:
            Writing intensive course (the only course with a 7-digit title - always ends with a 'W')
            Upper division coursework (courses ending with digits 300+)
            ACCT requirement
            Core
            Supporting
            concentration requirements
            *120-hour requirement? Free electives? (we may not need to consider these - show me what you've got once you finish functions that check progress on other requirements)
     */

    // get all courses
    @GetMapping("")
    public ResponseEntity<Object[]> getCourses() throws Exception {
        List<Course> courses = jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade FROM course", (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7))
        );
        return new ResponseEntity<>(courses.toArray(), HttpStatus.OK);
    }

    // get one course
    @GetMapping("{id}")
    public ResponseEntity<Object[]> getCourse(@PathVariable("id") String id) {
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{id}, Integer.class);
        if (courseExistsCount == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Course> course = jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade FROM course WHERE id = ?", new Object[]{id}, (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7)));
        return new ResponseEntity<>(course.toArray(), HttpStatus.OK);
    }

    // modify a course
    @PatchMapping()
    public ResponseEntity<Course> modifyCourse(@RequestBody Course course) {
        // Don't allow two courses with same title to be taken in same year and semester.
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id != ? AND title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{course.getId(), course.getTitle(), course.getSemesterTaken(), course.getYearTaken()}, Integer.class);
        if (courseExistsCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(course.getRequirementSatisfaction())){
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        jdbcTemplate.update("UPDATE course SET title = ?, requirement_satisfaction = ?, credits = ?, semester_taken = ?, year_taken = ?, final_grade = ? WHERE id = ?", new Object[]{course.getTitle(), course.getRequirementSatisfaction(), course.getCredits(), course.getSemesterTaken(), course.getYearTaken(), course.getFinalGrade(), course.getId()});
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Add course
    @PostMapping("")
    public ResponseEntity<Course> addCourse(@RequestBody Course newCourse) {
        // Don't allow two courses with same title to be taken in same year and semester.
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id != ? AND title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{newCourse.getId(), newCourse.getTitle(), newCourse.getSemesterTaken(), newCourse.getYearTaken()}, Integer.class);
        if (courseExistsCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(newCourse.getRequirementSatisfaction())){
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Turn the course instance into an array.
        List<Object[]> courseList = new ArrayList<>();
        courseList.add(newCourse.toObjectArray());
        jdbcTemplate.batchUpdate("INSERT INTO course (id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?,?)", courseList);
        return new ResponseEntity<>(newCourse, HttpStatus.OK);
    }


    // Delete course
    @DeleteMapping()
    public ResponseEntity<Course> deleteAssignment(@RequestBody Course course) throws Exception {
        // Check that the course exists.
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{course.getId()}, Integer.class);
        if (count == 0) {
            return new ResponseEntity<>(course, HttpStatus.NOT_FOUND);
        }
        jdbcTemplate.update("DELETE FROM course WHERE id = ?", new Object[]{course.getId()});
        return new ResponseEntity<>(course, HttpStatus.OK);
    }



    public boolean validRequirementSatisfaction(String requirementSatisfaction) {
        switch (requirementSatisfaction) {
            case "Supporting":
            case "Core":
            case "Concentration":
            case "ACCT":
            case "Writing Intensive":
            case "Gen Ed E1":
            case "Gen Ed E2":
            case "Gen Ed E3":
            case "Gen Ed E4":
            case "Gen Ed E5":
            case "Gen Ed E6":
            case "Upper Division":
            case "Free Elective":
                return true;
            default:
                return false;
        }
    }

    public boolean courseTableExists() throws Exception {
        Connection conn = null;
        Statement stmt = null;

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:./h2/h2db", "sa", "");
        stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM course");
        } catch (Exception e) {
            if (e.getMessage().contains("Table \"COURSE\" not found")) {  // Course table doesn't exist.
                stmt.close();
                conn.close();
                return false;
            }
        }
        stmt.close();
        conn.close();
        return true;
    }

    public void createCourseTable() throws Exception {
        Connection conn = null;
        Statement stmt = null;

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:./h2/h2db", "sa", "");
        stmt = conn.createStatement();
        stmt.execute("CREATE TABLE course(\n" +
                "\tid INT PRIMARY KEY,\n" +
                "\ttitle CHAR(7) NOT NULL,\n" +
                "\trequirement_satisfaction CHAR(50) NOT NULL,\n" +
                "\tcredits DOUBLE NOT NULL,\n" +
                "\tsemester_taken CHAR(12) NOT NULL,\n" +
                "\tyear_taken INT(4) NOT NULL,\n" +
                "\tfinal_grade CHAR(1),\n" +
                "\tUNIQUE (title, semester_taken, year_taken)\n" +
                ")");
        stmt.close();
        conn.close();
    }
    // TODO add Modify course functionality

}
