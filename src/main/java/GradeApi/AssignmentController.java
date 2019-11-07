package GradeApi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("")
    public Assignment[] getAssignments(@RequestParam(value="name", defaultValue="Error") int courseID) {
        Assignment[] a = {new Assignment("CSC190", 1, 15.4, 89.2)};
        return a;
    }

    // auto wire for the database connection


    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}
