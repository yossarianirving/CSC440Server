package GradeApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {
    // Todo add optional parameters
    // get all coursees
    @GetMapping("")
    public Course[] courses() {
        Course[] allCourses = {
                new Course(1, "Not fun", "CSC195", 3, "completed"),
                new Course(2, "Very fun", "CSC440", 3, "in_progress")
        };

        return allCourses;
    }

    // TODO add get single course functionality
    // get one course
    @GetMapping("{id}")
    public ResponseEntity<Course> getcourse(@PathVariable("id") int id) {
        Course[] allCourses = {
                new Course(1, "Not fun", "CSC195", 3, "completed"),
                new Course(2, "Very fun", "CSC440", 3, "in_progress")
        };
        if (id > allCourses.length) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allCourses[id-1], HttpStatus.OK);
    }

    // TODO add Add course functionality
    // Add course
    @PostMapping("")
    public ResponseEntity<Course> addcourse(@RequestBody Course newcourse) {
        newcourse.id = 34;
        return new ResponseEntity<>(newcourse, HttpStatus.CREATED);
    }

    // TODO add Modify course functionality

}
