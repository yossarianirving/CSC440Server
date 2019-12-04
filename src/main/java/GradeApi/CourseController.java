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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Component
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // get all courses of a particular type (according to value of status)
    @GetMapping("")
    public ResponseEntity<Object[]> getCourses(@RequestParam(value = "status", defaultValue = "") String status) throws Exception {
        if (status.equals("")) {
            List<Course> courses = jdbcTemplate.query(
                    "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status FROM course", (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8))
            );
            return new ResponseEntity<>(courses.toArray(), HttpStatus.OK);
        } else if (status.equals("finished")) { // Get finished courses.
            List<Course> courses = jdbcTemplate.query(
                    "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status FROM course WHERE status = 'finished'", (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8))
            );
            return new ResponseEntity<>(courses.toArray(), HttpStatus.OK);
        } else if (status.equals("in_progress")) { // Get in progress courses.
            List<Course> courses = jdbcTemplate.query(
                    "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status FROM course WHERE status = 'in_progress'", (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8))
            );
            return new ResponseEntity<>(courses.toArray(), HttpStatus.OK);
        } else {  // Invalid status!
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // get information for one course
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") String id) throws Exception {
        if (!courseExists(Integer.parseInt(id))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Course> course = jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status FROM course WHERE id = ?", new Object[]{id}, (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8)));
        return new ResponseEntity<>(course.get(0), HttpStatus.OK);
    }

    // modify a course
    @PatchMapping("/{id}")
    public ResponseEntity<Course> modifyCourse(@RequestBody Course course, @PathVariable("id") String id) throws Exception {
        course.setId(Integer.parseInt(id));

        // Check that the course exists
        if (!courseExists(Integer.parseInt(id))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Capitalize the semester, final grade, and title.
        course.setSemesterTaken(course.getSemesterTaken().toUpperCase());
        course.setFinalGrade(course.getFinalGrade().toUpperCase());
        course.setTitle(course.getTitle().toUpperCase());

        // Course titles cannot contain special characters (except for spaces).
        if (containsSpecialCharacter(course.getTitle())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Don't allow two courses with same title to be taken in same year and semester.
        int duplicateCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id != ? AND title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{course.getId(), course.getTitle(), course.getSemesterTaken(), course.getYearTaken()}, Integer.class);
        if (duplicateCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Try making the course title valid (remove spaces and special characters).
        try {
            course.setTitle(fixCourseTitle(course.getTitle()));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(course.getRequirementSatisfaction())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        jdbcTemplate.update("UPDATE course SET title = ?, requirement_satisfaction = ?, credits = ?, semester_taken = ?, year_taken = ?, final_grade = ?, status = ? WHERE id = ?", new Object[]{course.getTitle(), course.getRequirementSatisfaction(), course.getCredits(), course.getSemesterTaken(), course.getYearTaken(), course.getFinalGrade(), course.getStatus(), course.getId()});
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Add course
    @PostMapping("")
    public ResponseEntity<Course> addCourse(@RequestBody Course newCourse) throws Exception {
        // Capitalize the semester, final grade, and title.
        newCourse.setSemesterTaken(newCourse.getSemesterTaken().toUpperCase());
        newCourse.setFinalGrade(newCourse.getFinalGrade().toUpperCase());
        newCourse.setTitle(newCourse.getTitle().toUpperCase());

        // Course titles cannot contain special characters (except for spaces).
        if (containsSpecialCharacter(newCourse.getTitle())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Try making the course title valid.
        try {
            newCourse.setTitle(fixCourseTitle(newCourse.getTitle()));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Don't allow two courses with same title to be taken in same year and semester.
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{newCourse.getTitle(), newCourse.getSemesterTaken(), newCourse.getYearTaken()}, Integer.class);
        if (courseExistsCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(newCourse.getRequirementSatisfaction())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        List<Object[]> courseList = new ArrayList<>();
        courseList.add(newCourse.toObjectArray());

        jdbcTemplate.batchUpdate("INSERT INTO course (title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status) VALUES (?,?,?,?,?,?,?)", courseList);

        // Get the id of the new course.
        int id = jdbcTemplate.queryForObject("SELECT SYSTEM_SEQUENCE_318E6C69_933E_40FF_A195_9EB33D6E5BA0.NEXTVAL - 1 FROM DUAL", Integer.class);
        newCourse.setId(id);

        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }


    // Delete course
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCourse(@PathVariable("id") String id) throws Exception {
        try {
            // Check that the course exists.
            if (!courseExists(Integer.parseInt(id))) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            jdbcTemplate.update("DELETE FROM Xcourse WHERE id = ?", new Object[]{id});
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().contains("Table \"COURSE\" not found")) {
                createCourseTable();
            } else {
                throw e;
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // Returns a corrected course title corrected to the system standards.
    // Ex: "csc 190" becomes "CSC190 ".
    private String fixCourseTitle(String courseTitle) throws Exception {
        // To be a valid title, it must be 6 or 7 chars long.
        if (courseTitle.length() > 7 || courseTitle.length() < 6) {
            throw new Exception("Invalid course title length.");
        }

        // Remove spacing except at end of course title.
        int currentLength = courseTitle.length();

        // Below loop will fix issues with "embedded spaces". Ex.: "CSC 190" would become "CSC190 ".
        for (int i = 0; i < currentLength - 1; i++) {
            if (courseTitle.charAt(i) == ' ') {  // Remove the embedded space.
                courseTitle = courseTitle.substring(0, i) + courseTitle.substring(i + 1, currentLength);
            }
        }
        // Upper case the title.
        courseTitle = courseTitle.toUpperCase();

        // If there isn't already a space at the end of the course title (and the title is 6 chars long), then add one.
        if (courseTitle.length() == 6) {
            courseTitle = courseTitle + " ";
        }
        return courseTitle;
    }

    // courseExists ~ checks that the course exists.
    public boolean courseExists(int id) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{id}, Integer.class);
        if (count == 0) {
            return false;
        }
        return true;
    }

    // Checks if a string contains a character that is not a letter, number, or space.
    public boolean containsSpecialCharacter(String s) {
        String numbersAndLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        for (int i = 0; i < s.length(); i++) {
            if (!numbersAndLetters.contains("" + s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // Checks that a requirement satisfaction is valid.
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
                "\tid INTEGER AUTO_INCREMENT PRIMARY KEY,\n" +
                "\ttitle CHAR(7) NOT NULL,\n" +
                "\trequirement_satisfaction CHAR(50) NOT NULL,\n" +
                "\tcredits DOUBLE NOT NULL,\n" +
                "\tsemester_taken CHAR(12) NOT NULL,\n" +
                "\tyear_taken INT(4) NOT NULL,\n" +
                "\tfinal_grade CHAR(1),\n" +
                "\tstatus CHAR(12),\n" +
                "\tUNIQUE (title, semester_taken, year_taken)\n" +
                ")");
        stmt.close();
        conn.close();
    }
}
