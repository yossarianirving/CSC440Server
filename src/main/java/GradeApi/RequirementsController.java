package GradeApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;

public class RequirementsController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Gets the gen ed element 1 courses that student has taken.
    public String getGenEdE1Remaining() {
        List<Double> genEdE1Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E1"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE1Complete.size(); i++){
            sum += genEdE1Complete.get(i);
        }
        sum = 9 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 1 satisfied.";
        } else {
            return "Gen Ed 1 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 2 courses that student has taken.
    public String getGenEdE2Remaining() {
        List<Double> genEdE2Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E2"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE2Complete.size(); i++){
            sum += genEdE2Complete.get(i);
        }
        sum = 3 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 2 satisfied.";
        } else {
            return "Gen Ed 2 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 3 courses that student has taken.
    public String getGenEdE3Remaining() {
        List<Double> genEdE3Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E3"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE3Complete.size(); i++){
            sum += genEdE3Complete.get(i);
        }
        sum = 6 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 3 satisfied.";
        } else {
            return "Gen Ed 3 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 4 courses that student has taken.
    public String getGenEdE4Remaining() {
        List<Double> genEdE4Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E4"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE4Complete.size(); i++){
            sum += genEdE4Complete.get(i);
        }
        sum = 6 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 4 satisfied.";
        } else {
            return "Gen Ed 4 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 5 courses that student has taken.
    public String getGenEdE5Remaining() {
        List<Double> genEdE5Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E5"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE5Complete.size(); i++){
            sum += genEdE5Complete.get(i);
        }
        sum = 6 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 5 satisfied.";
        } else {
            return "Gen Ed 5 needs " + sum + " credit hours.";
        }

    }

    // Gets the gen ed element 5 courses that student has taken.
    public String getGenEdE6Remaining() {
        List<Double> genEdE6Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E6"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE6Complete.size(); i++){
            sum += genEdE6Complete.get(i);
        }
        sum = 6 - sum;

        if ( sum <= 0 ){
            return "Gen Ed 6 satisfied.";
        } else {
            return "Gen Ed 6 needs " + sum + " credit hours.";
        }

    }

    // Checks if writing intensive course has been finished
    public String getWritingIntensiveRemaining() {
        List<Double> writingCoursesComplete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"%W"},
                (rs, rowNum) -> rs.getDouble(1));

        if ( writingCoursesComplete.size() > 0 ){
            return "Writing intensive course satisfied.";
        } else {
            return "Writing intensive course still needed.";
        }

    }

    // Checks if ACCT requirement has been done
    public String getACCTRemaining() {
        List<String> ACCTComplete = jdbcTemplate.query(
                "SELECT title FROM course WHERE requirement_satisfaction = ?", new Object[]{"CSC%"},
                (rs, rowNum) -> rs.getString(1));

        if ( ACCTComplete.contains("CSC249 ") || ACCTComplete.contains("CSC440 ") || ACCTComplete.contains("CSC491 ") || ACCTComplete.contains("CSC549 ") || ACCTComplete.contains("CSC495 ") ){
            return "ACCT course satisfied.";
        } else {
            return "One course in CSC249, CSC440, CSC491, CSC549, or CSC495 still needed for ACCT.";
        }

    }

    // Checks if they have their concentration courses done

    // Checks if upper division hours have been met
    public String getUpperDivisionRemaining() {
        List<Double> upperDivisionComplete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"___3___"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < upperDivisionComplete.size(); i++){
            sum += upperDivisionComplete.get(i);
        }
        sum = 42 - sum;

        if ( sum <= 0 ){
            return "Upper Division credits satisfied.";
        } else {
            return "Still need " + sum + "credit hours in upper division courses, courses ending in a 300 or higher.";
        }

    }

    // Checks if upper division hours have been met
    public String[] getCoreRemaining() {
        List<String> coreComplete = jdbcTemplate.query(
                "SELECT title FROM course WHERE requirement_satisfaction = ?", new Object[]{"CSC%"},
                (rs, rowNum) -> rs.getString(1));
        String[] coreStatus = new String[6];

        if( coreComplete.contains("CSC185 ") ){
            coreStatus[0] = "Credit for CSC185 is satisfied.";
        } else {
            coreStatus[0] = "3 hours still needed for CSC185.";
        }

        if( coreComplete.contains("CSC190 ") ){
            coreStatus[1] = "Credit for CSC190 is satisfied.";
        } else {
            coreStatus[1] = "3 hours still needed for CSC190.";
        }

        if( coreComplete.contains("CSC191 ") ){
            coreStatus[2] = "Credit for CSC191 is satisfied.";
        } else {
            coreStatus[2] = "3 hours still needed for CSC191.";
        }

        if( coreComplete.contains("CSC195 ") ){
            coreStatus[3] = "Credit for CSC195 is satisfied.";
        } else {
            coreStatus[3] = "3 hours still needed for CSC195.";
        }

        if( coreComplete.contains("CSC310 ") ){
            coreStatus[4] = "Credit for CSC310 is satisfied.";
        } else {
            coreStatus[4] = "3 hours still needed for CSC310.";
        }

        if( coreComplete.contains("CSC340 ") ){
            coreStatus[5] = "Credit for CSC340 is satisfied.";
        } else {
            coreStatus[5] = "3 hours still needed for CSC340.";
        }

        return coreStatus;
    }
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