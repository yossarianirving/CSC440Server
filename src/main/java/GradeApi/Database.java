package GradeApi;

import java.io.File;
import java.sql.*;

public class Database {

    public Connection getConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Success!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/grades", "root", "granted27");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("here?");
        return con;
    }


    public static void createNewTable() {
        String basePath = new File("").getAbsolutePath();
        // SQLite connection string
        String url = "jdbc:sqlite:" + basePath.replace("\\", "/") + "/grades.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE course(\n" +
                "\tid INT PRIMARY KEY,\n" +
                "\ttitle CHAR(7) NOT NULL,\n" +
                "\trequirement_satisfaction CHAR(50) NOT NULL,\n" +
                "\tcredits DOUBLE NOT NULL,\n" +
                "\tsemester_taken CHAR(12) NOT NULL,\n" +
                "\tyear_taken INT(4) NOT NULL,\n" +
                "\tfinal_grade CHAR(1)\n" +
                ");\n" +
                "CREATE TABLE assignment(\n" +
                "\ttitle CHAR(50) PRIMARY KEY,\n" +
                "\tweight DOUBLE NOT NULL,\n" +
                "\tgrade DOUBLE NOT NULL,\n" +
                "\tcourse_id INT NOT NULL, CONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id)\n" +
                ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
