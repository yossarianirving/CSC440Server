package GradeApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import GradeApi.Class;

@RestController
@RequestMapping("/classes")
public class ClassController {
    // Todo add optional parameters
    // get all classes
    @GetMapping("")
    public Class[] classes() {
        Class[] allClasses = {
                new Class(1, "Not fun", "CSC195", 3, "completed"),
                new Class(2, "Very fun", "CSC440", 3, "in_progress")
        };

        return allClasses;
    }

    // TODO add get single class functionality
    // get one class
    @GetMapping("{id}")
    public ResponseEntity<Class> getClass(@PathVariable("id") int id) {
        Class[] allClasses = {
                new Class(1, "Not fun", "CSC195", 3, "completed"),
                new Class(2, "Very fun", "CSC440", 3, "in_progress")
        };
        if (id > allClasses.length) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allClasses[id-1], HttpStatus.OK);
    }

    // TODO add Add class functionality
    // Add class
    @PostMapping("")
    public ResponseEntity<Class> addClass(@RequestBody Class newClass) {
        newClass.id = 34;
        return new ResponseEntity<>(newClass, HttpStatus.CREATED);
    }

    // TODO add Modify class functionality

}
