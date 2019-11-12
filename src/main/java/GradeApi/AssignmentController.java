package GradeApi;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.*;

@SpringBootApplication
@Component
@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/modifyAssignment/{oldTitle}/{newTitle}/{weight}/{grade}/{courseID}", method = RequestMethod.PATCH)
    public void modifyAssignment(@PathVariable String oldTitle, @PathVariable String newTitle, @PathVariable String weight, @PathVariable String grade, @PathVariable String courseID) throws Exception {
        jdbcTemplate.update("UPDATE assignment SET title = ?, weight = ?, grade = ? WHERE title = ? AND course_id = ?", new Object[]{newTitle, weight, grade, oldTitle, courseID});
    }

    @RequestMapping(path = "/deleteAssignment/{title}/{courseID}", method = RequestMethod.DELETE)
    public void deleteAssignment(@PathVariable String title, @PathVariable String courseID) throws Exception {
        jdbcTemplate.update("DELETE FROM assignment WHERE title = ? AND course_id = ?", new Object[]{title, courseID});
    }

    @PostMapping("")
    public ResponseEntity<Assignment> addAssignment(@RequestBody Assignment newAssignment) {
        System.out.println("assignment data : " + newAssignment.toString());
        /*
        int number =  jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE title = ? AND course_id = ?",new Object[]{title, courseID}, Integer.class);
        if (number > 0) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        }

        Assignment a = new Assignment(title, Double.parseDouble(weight), Double.parseDouble(grade), Integer.parseInt(courseID));
        List<Object[]> assignmentList = new ArrayList<>();
        assignmentList.add(a.toObjectArray());
        jdbcTemplate.batchUpdate("INSERT INTO assignment(title, weight, grade, course_id) VALUES (?,?,?,?)", assignmentList);
        */

        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    @GetMapping("/getAssignments")
    public Object[] getAssignments(@RequestParam(value = "courseID", defaultValue = "-1") String courseID) throws Exception {
        if (!assignmentTableExists()) {  // If the assignment table doesn't exist, then have it created.
            createAssignmentTable();
        }
        List<Assignment> assignments = jdbcTemplate.query(
                "SELECT title, weight, grade, course_id FROM assignment WHERE course_id = ?", new Object[]{courseID},
                (rs, rowNum) -> new Assignment(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getInt(4))
        );
        return assignments.toArray();
    }

    public boolean assignmentTableExists() throws Exception {
        Connection conn = null;
        Statement stmt = null;

        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:./h2/h2db", "sa", "");
        stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM assignment");
        } catch (Exception e) {
            if (e.getMessage().contains("Table \"ASSIGNMENT\" not found")) {  // Assignment table doesn't exist.
                return false;
            }
        } finally {
            stmt.close();
            conn.close();
        }
        return true;
    }

    public void createAssignmentTable() throws Exception {
        // First, need to check that the course table exists so the foreign key constraint will work.
        CourseController c = new CourseController();
        if (!c.courseTableExists()) {
            System.out.println("course table did NOT exist!");
            c.createCourseTable();
        }

        jdbcTemplate.execute("CREATE TABLE assignment(\n" +
                "\ttitle CHAR(50),\n" +
                "\tweight DOUBLE NOT NULL,\n" +
                "\tgrade DOUBLE NOT NULL,\n" +
                "\tcourse_id INT NOT NULL, \n" +
                "\tCONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id),\n" +
                "\tPRIMARY KEY (title, course_id)\n" +
                ")");
        System.out.println("created assignment table");
        testInsertsStatements();
        System.out.println("done inserting");
    }

    //////////////////////////////////////////////////////////////////////
    // BELOW IS FOR TESTING PURPOSES ONLY. ///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public void testInsertsStatements() {
        /*
        // Make sure user wants to drop the tables and insert new data.
        Scanner input = new Scanner(System.in);
        System.out.println("WARNING: WILL DROP ASSIGNMENT AND COURSE TABLES.");
        System.out.println("Continue? Y/N");
        String key = input.next();
        if (key.charAt(0) != 'Y' && key.charAt(0) == 'y') {
            System.out.println("Exiting the application. NO TABLES WERE DROPPED AND NO NEW DATA WAS INSERTED SINCE YOU TYPED " + key + ".");
            System.exit(0);
        } else {
            System.out.println("About to drop assignment and course tables...");
        }
         */


        jdbcTemplate.execute("DROP TABLE assignment");
        jdbcTemplate.execute("DROP TABLE course");

        // Attempt to insert some data into the tables.
        List<Object[]> c = new ArrayList<>();

        Course c1 = new Course(1, "CSC541", "Supporting", 3, "SPRING", 2019, "A");
        Course c2 = new Course(2, "CSC340", "Supporting", 3, "FALL", 2018, "A");

        c.add(c1.toObjectArray());
        c.add(c2.toObjectArray());

        Assignment a1 = new Assignment("Test 1", 10, 98.5, 1);
        Assignment a2 = new Assignment("Test 2", 10, 93.5, 1);
        Assignment a3 = new Assignment("Quiz 1", 4.25, 88, 1);
        Assignment a4 = new Assignment("assignment 1", 5.5, 100, 2);

        List<Object[]> a = new ArrayList<>();
        a.add(a1.toObjectArray());
        a.add(a2.toObjectArray());
        a.add(a3.toObjectArray());
        a.add(a4.toObjectArray());

        jdbcTemplate.batchUpdate("INSERT INTO course(title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?)", c);
        System.out.println("Querying for course:");
        jdbcTemplate.query(
                "SELECT id, title FROM course",
                (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), "blah", 0, "FALL", 2000, "F")
        ).forEach(course -> System.out.println(course.getId() + "..." + course.getTitle()));
        //jdbcTemplate.batchUpdate("INSERT INTO course(id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?,?)", c2.toString());

        jdbcTemplate.batchUpdate("INSERT INTO assignment(title, weight, grade, course_id) VALUES (?,?,?,?)", a);

        System.out.println("Querying for assignments:");
        jdbcTemplate.query(
                "SELECT * FROM assignment",
                (rs, rowNum) -> new Assignment(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getInt(4))
        ).forEach(assignment -> System.out.println(assignment.getTitle() + ", " + assignment.getWeight() + ", " + assignment.getGrade() + ", " + assignment.getCourseID()));
    }
    //////////////////////////////////////////////////////////////////////

}
