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

@SpringBootApplication
@Component
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    JdbcTemplate jdbcTemplate;

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
                "\tid INT AUTO_INCREMENT PRIMARY KEY,\n" +
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
