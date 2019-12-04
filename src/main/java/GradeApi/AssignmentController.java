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
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // modify an assignment (could change title, weight, grade or any combination of these)
    @PatchMapping("/{id}")
    public ResponseEntity<Assignment> modifyAssignment(@RequestBody Assignment assignment, @PathVariable("id") String id) throws Exception {
        // Check that the assignment exists.
        if (!assignmentExists(Integer.parseInt(id))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Check that the course exists.
        if (!courseExists(assignment.getCourseID())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // Set the assignment id to that of the path variable.
        assignment.setId(Integer.parseInt(id));

        // Check that the title does not exist for some other assignment.
        int duplicates = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE id != ? AND title = ? AND course_id = ?", new Object[]{id, assignment.getTitle(), assignment.getCourseID()}, Integer.class);
        if (duplicates > 0) {
            return new ResponseEntity<>(assignment, HttpStatus.NOT_FOUND);
        }

        jdbcTemplate.update("UPDATE assignment SET title = ?, weight = ?, grade = ? WHERE id = ?", new Object[]{assignment.getTitle(), assignment.getWeight(), assignment.getGrade(), id});
        return new ResponseEntity<>(assignment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Assignment> deleteAssignment(@PathVariable("id") String id) throws Exception {
        // Make sure assignment exists.
        if (!assignmentExists(Integer.parseInt(id))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        jdbcTemplate.update("DELETE FROM assignment WHERE id = ?", new Object[]{id});
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<Assignment> addAssignment(@RequestBody Assignment newAssignment) {
        // Check that the course exists.
        if (!courseExists(newAssignment.getCourseID())) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_FOUND);
        }

        // Check that the assignment does not already exist.
        int number = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE title = ? AND course_id = ?", new Object[]{newAssignment.getTitle(), newAssignment.getCourseID()}, Integer.class);
        if (number > 0) {
            return new ResponseEntity<>(newAssignment, HttpStatus.NOT_ACCEPTABLE);
        }

        List<Object[]> assignmentList = new ArrayList<>();
        assignmentList.add(newAssignment.toObjectArray());
        jdbcTemplate.batchUpdate("INSERT INTO assignment(title, weight, grade, course_id) VALUES (?,?,?,?)", assignmentList);

        // Get the id of the new assignment.
        int id = jdbcTemplate.queryForObject("SELECT SYSTEM_SEQUENCE_243624BC_0113_49E2_A746_10DDB1169396.NEXTVAL - 1 FROM DUAL", Integer.class);
        newAssignment.setId(id);

        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    // Get all assignments for one course.
    @GetMapping("")
    public ResponseEntity<Object[]> getAssignments(@Param(value = "courseID") String courseID) throws Exception {
        // Check that the course exists.
        if (!courseExists(Integer.parseInt(courseID))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        List<Assignment> assignment = jdbcTemplate.query(
                "SELECT id, title, weight, grade, course_id FROM assignment WHERE course_id = ?", new Object[]{courseID},
                (rs, rowNum) -> new Assignment(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5))
        );
        return new ResponseEntity<>(assignment.toArray(), HttpStatus.OK);
    }


    // Get one assignment.
    @GetMapping("/{id}")
    public ResponseEntity getAssignment(@PathVariable("id") String id) throws Exception {
        if (!assignmentExists(Integer.parseInt(id))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Assignment> assignment = jdbcTemplate.query(
                "SELECT id, title, weight, grade, course_id FROM assignment WHERE id = ?", new Object[]{id}, (rs, rowNum) -> new Assignment(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5)));
        return new ResponseEntity<>(assignment.get(0), HttpStatus.OK);
    }

    // courseExists ~ checks that the course exists.
    public boolean courseExists(int id) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE id = ?", new Object[]{id}, Integer.class);
        if (count == 0) {
            return false;
        }
        return true;
    }

    public boolean assignmentExists(int id) {
        // Check that the assignment exists.
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM assignment WHERE id = ?", new Object[]{id}, Integer.class);
        if (count == 0) {
            return false;
        }
        return true;
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
            c.createCourseTable();
        }

        jdbcTemplate.execute("CREATE TABLE assignment(\n" +
                "\tid INTEGER AUTO_INCREMENT PRIMARY KEY,\n" +
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

        jdbcTemplate.batchUpdate("INSERT INTO course(title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade, status) VALUES (?,?,?,?,?,?,?)", c);
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