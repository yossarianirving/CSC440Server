package GradeApi;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

@SpringBootApplication
@Component
@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // modify an assignment (could change title, weight, grade or any combination of these)
    @PatchMapping("")
    public ResponseEntity<Assignment> modifyAssignment(@RequestBody Assignment newAssignment) throws Exception {
        // Assume assignment id won't be modified.

        System.out.println(newAssignment.getTitle());

        // Check that the assignment exists.
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE id = ?", new Object[]{newAssignment.getId()}, Integer.class);
        if (count == 0) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_FOUND);
        }

        // Check that the title does not exist for some other assignment.
        int duplicates = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE id != ? AND title = ? AND course_id = ?", new Object[]{newAssignment.getId(), newAssignment.getTitle(), newAssignment.getCourseID()}, Integer.class);
        if (duplicates > 0) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_FOUND);
        }

        jdbcTemplate.update("UPDATE assignment SET title = ?, weight = ?, grade = ? WHERE id = ?", new Object[]{newAssignment.getTitle(), newAssignment.getWeight(), newAssignment.getGrade(), newAssignment.getId()});
        return new ResponseEntity<>(newAssignment, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Assignment> deleteAssignment(@RequestBody Assignment assignment) throws Exception {
        // Check that the assignment exists.
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE id = ?", new Object[]{assignment.getId()}, Integer.class);
        if (count == 0) {
            return new ResponseEntity<>(assignment, HttpStatus.NOT_FOUND);
        }
        jdbcTemplate.update("DELETE FROM assignment WHERE id = ?", new Object[]{assignment.getId()});
        return new ResponseEntity<>(assignment, HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<Assignment> addAssignment(@RequestBody Assignment newAssignment) {
        // Check that the assignment does not already exist.
        int number = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE (title = ? AND course_id = ?) OR id = ?", new Object[]{newAssignment.getTitle(), newAssignment.getCourseID(), newAssignment.getId()}, Integer.class);
        if (number > 0) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_ACCEPTABLE);
        }

        // Check that the course exists.
        int courseCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{newAssignment.getCourseID()}, Integer.class);
        if (courseCount == 0) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_FOUND);
        }
        List<Object[]> assignmentList = new ArrayList<>();
        assignmentList.add(newAssignment.toObjectArray());
        jdbcTemplate.batchUpdate("INSERT INTO assignment(id, title, weight, grade, course_id) VALUES (?,?,?,?,?)", assignmentList);
        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<Object[]> getAssignments(@Param("courseID") String courseID) throws Exception {
        int courseExistsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{courseID}, Integer.class);
        if (courseExistsCount == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Assignment> assignment = jdbcTemplate.query(
                "SELECT id, title, weight, grade, course_id FROM assignment WHERE course_id = ?", new Object[]{courseID},
                (rs, rowNum) -> new Assignment(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5))
        );
        return new ResponseEntity<>(assignment.toArray(), HttpStatus.OK);
    }

    public boolean assignmentTableExists() throws Exception {
        try {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment", Integer.class);
        } catch (Exception e) {
            if (e.getMessage().contains("Table \"ASSIGNMENT\" not found")) {  // Assignment table doesn't exist.
                return false;
            }
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
                "\tid INT PRIMARY KEY,\n" +
                "\ttitle CHAR(50),\n" +
                "\tweight DOUBLE NOT NULL,\n" +
                "\tgrade DOUBLE NOT NULL,\n" +
                "\tcourse_id INT NOT NULL, \n" +
                "\tCONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id),\n" +
                "\tUNIQUE (title, course_id)\n" +
                ")");
    }

    //////////////////////////////////////////////////////////////////////
    // BELOW IS FOR TESTING PURPOSES ONLY. ///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public void testInsertsStatements() {
        jdbcTemplate.execute("DROP TABLE assignment");
        jdbcTemplate.execute("DROP TABLE course");

        try {
            new CourseController().createCourseTable();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            createAssignmentTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Attempt to insert some data into the tables.
        List<Object[]> c = new ArrayList<>();

        Course c1 = new Course(1, "CSC541", "Supporting", 3, "SPRING", 2019, "A", "finished");
        Course c2 = new Course(2, "CSC340", "Supporting", 3, "FALL", 2018, "A", "in_progress");

        c.add(c1.toObjectArray());
        c.add(c2.toObjectArray());

        Assignment a1 = new Assignment(1, "Test 1", 10, 98.5, 1);
        Assignment a2 = new Assignment(2, "Test 2", 10, 93.5, 1);
        Assignment a3 = new Assignment(3, "Quiz 1", 4.25, 88, 1);
        Assignment a4 = new Assignment(4, "assignment 1", 5.5, 100, 2);

        List<Object[]> a = new ArrayList<>();
        a.add(a1.toObjectArray());
        a.add(a2.toObjectArray());
        a.add(a3.toObjectArray());
        a.add(a4.toObjectArray());

        jdbcTemplate.batchUpdate("INSERT INTO course(id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status) VALUES (?,?,?,?,?,?,?,?)", c);
        System.out.println("Querying for course:");
        jdbcTemplate.query(
                "SELECT id, title FROM course",
                (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), "blah", 0, "FALL", 2000, "F", "finished")
        ).forEach(course -> System.out.println(course.getId() + "..." + course.getTitle()));
        //jdbcTemplate.batchUpdate("INSERT INTO course(id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?,?)", c2.toString());

        jdbcTemplate.batchUpdate("INSERT INTO assignment(id, title, weight, grade, course_id) VALUES (?,?,?,?,?)", a);

        System.out.println("Querying for assignments:");
        jdbcTemplate.query(
                "SELECT * FROM assignment",
                (rs, rowNum) -> new Assignment(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5))
        ).forEach(assignment -> System.out.println(assignment.getTitle() + ", " + assignment.getWeight() + ", " + assignment.getGrade() + ", " + assignment.getCourseID()));
    }
    //////////////////////////////////////////////////////////////////////

}