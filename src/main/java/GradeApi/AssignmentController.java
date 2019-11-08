package GradeApi;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.jdbc.core.JdbcTemplate;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@SpringBootApplication
@Component
@RestController
@RequestMapping("/assignments")
public class AssignmentController implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {

        jdbcTemplate.execute("DROP TABLE assignment IF EXISTS");
        jdbcTemplate.execute("DROP TABLE course IF EXISTS");

        jdbcTemplate.execute("CREATE TABLE course(\n" +
                "\tid INT PRIMARY KEY,\n" +
                "\ttitle CHAR(7) NOT NULL,\n" +
                "\trequirement_satisfaction CHAR(50) NOT NULL,\n" +
                "\tcredits DOUBLE NOT NULL,\n" +
                "\tsemester_taken CHAR(12) NOT NULL,\n" +
                "\tyear_taken INT(4) NOT NULL,\n" +
                "\tfinal_grade CHAR(1)\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE assignment(\n" +
                "\ttitle CHAR(50) PRIMARY KEY,\n" +
                "\tweight DOUBLE NOT NULL,\n" +
                "\tgrade DOUBLE NOT NULL,\n" +
                "\tcourse_id INT NOT NULL, CONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id)\n" +
                ")");

        // Attempt to insert some data into the tables.
        List<Object[]> c1 = Arrays.asList("1 CSC190 Core 3 FALL 2016 B", "2 CSC191 Core 3 FALL 2017 A", "3 CSC195 Core 3 FALL 2017 A").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());
        List<Object[]> a1 = Arrays.asList("Test1 15 98.3 1").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO course(id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?,?)", c1);
        jdbcTemplate.batchUpdate("INSERT INTO assignment(title, weight, grade, course_id) VALUES (?,?,?,?)", a1);

        // Query the tables.
        jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade FROM course WHERE id = ?", new Object[]{"1"},
                (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7))
        ).forEach(course -> System.out.println(course.toString()));

        System.out.println("*** DONE ***");

        System.exit(0);
        /////////////////////////////

        System.out.println("about to drop, create, and insert !!!");
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> System.out.println(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        System.out.println("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(customer -> System.out.println(customer.toString()));

        System.out.println("--- DONE ---");
    }


    public static void main(String[] args) throws Exception {

        SpringApplication.run(AssignmentController.class, args);
    }

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("")
    public Assignment[] getAssignments(@RequestParam(value = "name", defaultValue = "-1") String courseID) throws Exception {

        // Create the Assignment and Course table if they don't already exist.
        //jdbcTemplate.execute("DROP TABLE assignment IF EXISTS");
        //jdbcTemplate.execute("DROP TABLE course IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE course(\n" +
                "\tid INT PRIMARY KEY,\n" +
                "\ttitle CHAR(7) NOT NULL,\n" +
                "\trequirement_satisfaction CHAR(50) NOT NULL,\n" +
                "\tcredits DOUBLE NOT NULL,\n" +
                "\tsemester_taken CHAR(12) NOT NULL,\n" +
                "\tyear_taken INT(4) NOT NULL,\n" +
                "\tfinal_grade CHAR(1)\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE assignment(\n" +
                "\ttitle CHAR(50) PRIMARY KEY,\n" +
                "\tweight DOUBLE NOT NULL,\n" +
                "\tgrade DOUBLE NOT NULL,\n" +
                "\tcourse_id INT NOT NULL, CONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id)\n" +
                ")");

        // Attempt to insert some data into the tables.
        List<Object[]> c1 = Arrays.asList("1 CSC190 Core 3 FALL 2016 B", "2 CSC191 Core 3 FALL 2017 A", "3 CSC195 Core 3 FALL 2017 A").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());
        List<Object[]> a1 = Arrays.asList("Test1 15 98.3 1").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO course(id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade) VALUES (?,?,?,?,?,?,?)", c1);
        jdbcTemplate.batchUpdate("INSERT INTO assignment(title, weight, grade, course_id) VALUES (?,?,?,?)", a1);

        // Query the tables.
        jdbcTemplate.query(
                "SELECT id, title, requirement_satisfaction, credits, semester_taken, year_taken, final_grade FROM course WHERE id = ?", new Object[]{"1"},
                (rs, rowNum) -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7))
        ).forEach(course -> System.out.println(course.toString()));

        System.out.println("*** DONE ***");

        Assignment a0 = null;
        Connection conn = new Database().getConnection();
        String query = "SELECT * FROM users";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            a0 = new Assignment(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getInt(4));

            // print the results
            System.out.println(a0.getTitle() + " " + a0.getGrade());
        }
        conn.close();
        Assignment[] a = {a0, new Assignment("CSC190", 10.5, 91, 1)};
        return a;
    }


    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}
