package GradeApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.query.Param;
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
    public String getGenEdE1Remaining() {
        List<Double> genEdE1Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E1"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE1Complete.size(); i++) {
            sum += genEdE1Complete.get(i);
        }
        sum = 9 - sum;
        if (sum <= 0) {
            return "Gen Ed 1 satisfied.";
        } else {
            return "Gen Ed 1 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 2 courses that student has taken.
    public String getGenEdE2Remaining() {
        List<Double> genEdE2Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E2"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE2Complete.size(); i++) {
            sum += genEdE2Complete.get(i);
        }
        sum = 3 - sum;
        if (sum <= 0) {
            return "Gen Ed 2 satisfied.";
        } else {
            return "Gen Ed 2 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 3 courses that student has taken.
    public String getGenEdE3Remaining() {
        List<Double> genEdE3Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E3"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE3Complete.size(); i++) {
            sum += genEdE3Complete.get(i);
        }
        sum = 6 - sum;
        if (sum <= 0) {
            return "Gen Ed 3 satisfied.";
        } else {
            return "Gen Ed 3 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 4 courses that student has taken.
    public String getGenEdE4Remaining() {
        List<Double> genEdE4Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E4"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE4Complete.size(); i++) {
            sum += genEdE4Complete.get(i);
        }
        sum = 6 - sum;
        if (sum <= 0) {
            return "Gen Ed 4 satisfied.";
        } else {
            return "Gen Ed 4 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 5 courses that student has taken.
    public String getGenEdE5Remaining() {
        List<Double> genEdE5Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E5"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE5Complete.size(); i++) {
            sum += genEdE5Complete.get(i);
        }
        sum = 6 - sum;
        if (sum <= 0) {
            return "Gen Ed 5 satisfied.";
        } else {
            return "Gen Ed 5 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 5 courses that student has taken.
    public String getGenEdE6Remaining() {
        List<Double> genEdE6Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E6"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE6Complete.size(); i++) {
            sum += genEdE6Complete.get(i);
        }
        sum = 6 - sum;
        if (sum <= 0) {
            return "Gen Ed 6 satisfied.";
        } else {
            return "Gen Ed 6 needs " + sum + " credit hours.";
        }

    }

    // get all courses of a particular type (according to value of status)
    @GetMapping("")
    public ResponseEntity<Object[]> getCourses(@Param("status") String status) throws Exception {
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
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // get information for one course
    @GetMapping("{id}")
    public ResponseEntity<Object[]> getCourse(@PathVariable("id") String id) {
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{id}, Integer.class);
        if (courseExistsCount == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Course> course = jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status FROM course WHERE id = ?", new Object[]{id}, (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8)));
        return new ResponseEntity<>(course.toArray(), HttpStatus.OK);
    }

    // modify a course
    @PatchMapping()
    public ResponseEntity<Course> modifyCourse(@RequestBody Course course) {
        // Check that the course exists
        int courseExists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{course.getId()}, Integer.class);
        if (courseExists == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Don't allow two courses with same title to be taken in same year and semester.
        int duplicateCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id != ? AND title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{course.getId(), course.getTitle(), course.getSemesterTaken(), course.getYearTaken()}, Integer.class);
        if (duplicateCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(course.getRequirementSatisfaction())) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        jdbcTemplate.update("UPDATE course SET title = ?, requirement_satisfaction = ?, credits = ?, semester_taken = ?, year_taken = ?, final_grade = ?, status = ? WHERE id = ?", new Object[]{course.getTitle(), course.getRequirementSatisfaction(), course.getCredits(), course.getSemesterTaken(), course.getYearTaken(), course.getFinalGrade(), course.getStatus(), course.getId()});
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Add course
    @PostMapping("")
    public ResponseEntity<Course> addCourse(@RequestBody Course newCourse) {
        // TODO Make sure the course title is valid.
        // To be a valid title, it must be of the form: "XXX123 " or "XXX123W".
        // 1. Remove spacing except at end of course title.
        // 2. Check that it only contains letters in first three digits.
        // 3. Check that digits 4 through 6 are numbers.
        // 4. Check that last digit is either a space or a W.
        // 5. If there isn't already a space at the end of a 6-character long title, then add it.

        // TODO  check valid status.

        // Don't allow two courses with same title to be taken in same year and semester.
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id != ? AND title = ? AND semester_taken = ? AND year_taken = ?", new Object[]{newCourse.getId(), newCourse.getTitle(), newCourse.getSemesterTaken(), newCourse.getYearTaken()}, Integer.class);
        if (courseExistsCount > 0) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Check that the requirement satisfaction is a valid type.
        if (!validRequirementSatisfaction(newCourse.getRequirementSatisfaction())) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Turn the course instance into an array.
        newCourse.setSemesterTaken(newCourse.getSemesterTaken().toUpperCase());  // Make all semesters uppercase.
        System.out.println(newCourse.getSemesterTaken());
        List<Object[]> courseList = new ArrayList<>();
        courseList.add(newCourse.toObjectArray());
        jdbcTemplate.batchUpdate("INSERT INTO course (id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status) VALUES (?,?,?,?,?,?,?,?)", courseList);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
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
                "\tstatus CHAR(12),\n" +
                "\tUNIQUE (title, semester_taken, year_taken)\n" +
                ")");
        stmt.close();
        conn.close();
    }
}
