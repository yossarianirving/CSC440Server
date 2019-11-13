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
    @GetMapping("/getRemainingRequirements")
    public Object[] getRemainingRequirements(@RequestParam(value = "concentrationSelection", defaultValue = "General") String concentrationSelection) throws Exception {
        List<Assignment> assignments = jdbcTemplate.query(
                "SELECT id, title, weight, grade, course_id FROM assignment WHERE course_id = ?", new Object[]{concentrationSelection},
                (rs, rowNum) -> new Assignment(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5))
        );
        return assignments.toArray();
    }


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

    // Todo add optional parameters
    // get all courses
    @GetMapping("")
    public Course[] courses() {
        Course[] allCourses = {
                new Course(1, "CSC190", "Core", 3, "FALL", 2016, "B"),
                new Course(2, "CSC308", "Supporting", 3, "SPRING", 2018, "A")
        };

        return allCourses;
    }

    // TODO add get single course functionality
    // get one course
    @GetMapping("{id}")
    public ResponseEntity<Course> getcourse(@PathVariable("id") int id) {
        Course[] allCourses = {
                new Course(1, "CSC190", "Core", 3, "FALL", 2016, "B"),
                new Course(2, "CSC308", "Supporting", 3, "SPRING", 2018, "A")
        };
        if (id > allCourses.length) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allCourses[id - 1], HttpStatus.OK);
    }

    // TODO add Add course functionality
    // Add course
    @PostMapping("")
    public ResponseEntity<Course> addcourse(@RequestBody Course newcourse) {
        newcourse.id = 34;
        return new ResponseEntity<>(newcourse, HttpStatus.CREATED);
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
                "\tfinal_grade CHAR(1)\n" +
                ")");
        stmt.close();
        conn.close();
    }
    // TODO add Modify course functionality

}
