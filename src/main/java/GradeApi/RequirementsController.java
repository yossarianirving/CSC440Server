package GradeApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@Component
@RestController
@RequestMapping("/concentrationRequirements")
public class RequirementsController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public ResponseEntity<CourseRequirements> getRemainingRequirements(@Param("concentrationSelection") String concentrationSelection) throws Exception {
        // Check that the concentration is valid.
        switch (concentrationSelection) {
            case "General": case "Digital Forensics and Cybersecurity":
            case "Computer Technology": case "Interactive Multimedia": case "Artificial Intelligence in data Science":
                break;
            default:
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        String genEdE1 = getGenEdE1Remaining();
        String genEdE2 = getGenEdE2Remaining();
        String genEdE3 = getGenEdE3Remaining();
        String genEdE4 = getGenEdE4Remaining();
        String genEdE5 = getGenEdE5Remaining();
        String genEdE6 = getGenEdE6Remaining();
        String writingIntensive = getWritingIntensiveRemaining();
        String acct = getACCTRemaining();
        String[] core = getCoreRemaining(concentrationSelection);
        String[] supporting = getSupportingRemaining(concentrationSelection);
        String upperDivision = getUpperDivisionRemaining();
        String[] concentration = getConcentrationRemaining(concentrationSelection);

        // The core, supporting, and concentration requirements are in String arrays. Ex: {"CSC311 ", "CSC320 ", "CSC360 "}
        // Derive a message from them. Ex: "Concentration requirements not yet satisfied: CSC311, CSC320, CSC360."
        String concentrationRequirementsMessage = "Concentration requirements not yet satisfied:" + createMessageFromArray(concentration);
        concentrationRequirementsMessage = chopOffLastCommaAndAddAPeriod(concentrationRequirementsMessage);

        String supportingRequirementsMessage = "Supporting requirements not yet satisfied:" + createMessageFromArray(supporting);
        supportingRequirementsMessage = chopOffLastCommaAndAddAPeriod(supportingRequirementsMessage);

        String coreRequirementsMessage = "Core requirements not yet satisfied:" + createMessageFromArray(core);
        coreRequirementsMessage = chopOffLastCommaAndAddAPeriod(coreRequirementsMessage);

        CourseRequirements courseRequirements = new CourseRequirements(genEdE1, genEdE2, genEdE3, genEdE4, genEdE5, genEdE6, writingIntensive, upperDivision, acct, coreRequirementsMessage, supportingRequirementsMessage, concentrationRequirementsMessage);
        return new ResponseEntity<>(courseRequirements, HttpStatus.OK);
    }

    public String createMessageFromArray(String[] arr) {
        String message = "";
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && !arr[i].contains("Physical Science sequence needed")) {
                message += " " + arr[i] + ",";
            } else if (arr[i] != null && arr[i].contains("Physical Science sequence needed")) { // In the case that a physical science sequence is needed, don't add a comma after the initial message.
                message += " " + arr[5];
            }
        }
        return message;
    }

    // Fix the core/supporting/concentration message to have a period at end instead of a comma.
    public String chopOffLastCommaAndAddAPeriod(String s) {
        if (!s.contains(",") && s.charAt(s.length() - 1) == ':') {  // Only the initial message appears. No requirements need to be met.
            s = null;
        } else if (s.charAt(s.length() - 1) == ','){  // Chop off last comma and add a period.
            s = s.substring(0, s.length() - 1) + ".";
        } else {  // Didn't conform to above specs - just return the string.
            return s;
        }
        return s;
    }

    // Gets the gen ed element 1 courses that student has taken.
    public String getGenEdE1Remaining() {
        List<Double> genEdE1Complete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE requirement_satisfaction = ?", new Object[]{"Gen Ed E1"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < genEdE1Complete.size(); i++) {
            sum += genEdE1Complete.get(i);
        }
        sum = 9 - sum;

        if (sum <= 0) {
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
        for (int i = 0; i < genEdE2Complete.size(); i++) {
            sum += genEdE2Complete.get(i);
        }
        sum = 3 - sum;

        if (sum <= 0) {
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
        for (int i = 0; i < genEdE3Complete.size(); i++) {
            sum += genEdE3Complete.get(i);
        }
        sum = 6 - sum;

        if (sum <= 0) {
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
        for (int i = 0; i < genEdE4Complete.size(); i++) {
            sum += genEdE4Complete.get(i);
        }
        sum = 6 - sum;

        if (sum <= 0) {
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
        for (int i = 0; i < genEdE5Complete.size(); i++) {
            sum += genEdE5Complete.get(i);
        }
        sum = 6 - sum;

        if (sum <= 0) {
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
        for (int i = 0; i < genEdE6Complete.size(); i++) {
            sum += genEdE6Complete.get(i);
        }
        sum = 6 - sum;

        if (sum <= 0) {
            return "Gen Ed 6 satisfied.";
        } else {
            return "Gen Ed 6 needs " + sum + " credit hours.";
        }

    }

    // Checks if writing intensive course has been finished
    public String getWritingIntensiveRemaining() {
        List<Double> writingCoursesComplete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE title LIKE ?", new Object[]{"%W"},
                (rs, rowNum) -> rs.getDouble(1));

        if (writingCoursesComplete.size() > 0) {
            return "Writing intensive course satisfied.";
        } else {
            return "Writing intensive course still needed.";
        }

    }

    // Checks if ACCT requirement has been done
    public String getACCTRemaining() {
        List<String> ACCTComplete = jdbcTemplate.query(
                "SELECT title FROM course WHERE title LIKE ?", new Object[]{"CSC%"},
                (rs, rowNum) -> rs.getString(1));

        if (ACCTComplete.contains("CSC249") || ACCTComplete.contains("CSC440") || ACCTComplete.contains("CSC491") || ACCTComplete.contains("CSC549") || ACCTComplete.contains("CSC495")) {
            return "ACCT course satisfied.";
        } else {
            return "One course in CSC249, CSC440, CSC491, CSC549, or CSC495 still needed for ACCT.";
        }

    }

    // Checks if upper division hours have been met
    public String getUpperDivisionRemaining() {
        List<Double> upperDivisionComplete = jdbcTemplate.query(
                "SELECT credits FROM course WHERE title LIKE ?", new Object[]{"___3%"},
                (rs, rowNum) -> rs.getDouble(1));

        double sum = 0;
        for (int i = 0; i < upperDivisionComplete.size(); i++) {
            sum += upperDivisionComplete.get(i);
        }
        sum = 42 - sum;

        if (sum <= 0) {
            return "Upper Division credits satisfied.";
        } else {
            return "Still need " + sum + "credit hours in upper division courses, courses ending in a 300 or higher.";
        }

    }

    /*
    assume all concentrations will be:
        General
        Digital Forensics and Cybersecurity
        Computer Technology
        Interactive Multimedia
        Artificial Intelligence in data Science
    */

    // Checks if core courses have been completed
    public String[] getCoreRemaining(String concentration) {
        List<String> coreComplete = jdbcTemplate.query(
                "SELECT title FROM course WHERE title LIKE ?", new Object[]{"CSC%"},
                (rs, rowNum) -> rs.getString(1));


        String[] coreNeeded = new String[12];

        // coreCourses is the courses for that core requirement to be fulfilled
        if ( concentration.equals("General") || concentration.equals("Computer Technology") ||
                concentration.equals("Interactive Multimedia") || concentration.equals("Artificial Intelligence in data Science") ||
                concentration.equals("Statistical Computing")
        ){
            String[] coreCourses = {"CSC185 ", "CSC190 ", "CSC191 ", "CSC195 ", "CSC310 ", "CSC313 ", "CSC340 ", "CSC449 "};

            for (int i = 0; i < coreCourses.length; i++){
                if( !coreComplete.contains(coreCourses[i]) ){
                    coreNeeded[i] = coreCourses[i];
                }
            }

            if( !coreComplete.contains("CSC308 ") && !coreComplete.contains("CSC309 ") ){
                coreNeeded[8] = "CSC308, CSC309";
            }

        } else if ( concentration.equals("Digital Forensics and Cybersecurity") ){

            String[] coreCourses = {"CSC189 ", "CSC210 ", "CSC313 ", "CSC410 ", "CSC536 ", "CSC537 ", "INF318 ", "INF321 ", "INF322 "};

            for (int i = 0; i < coreCourses.length; i++){
                if( !coreComplete.contains(coreCourses[i]) ){
                    coreNeeded[i] = coreCourses[i];
                }
            }

            if( !coreComplete.contains("CSC308 ") && !coreComplete.contains("CSC309 ") ){
                coreNeeded[9] = "CSC308, CSC309";
            }

            if( !coreComplete.contains("CSC349 ") && !coreComplete.contains("INF495 ") ){
                coreNeeded[10] = "CSC349, INF495";
            }

            if( !coreComplete.contains("CSC520 ") && !coreComplete.contains("CSC539 ") && !coreComplete.contains("CSC542 ") && !coreComplete.contains("CSC543 ") && !coreComplete.contains("CSC544 ")  ){
                coreNeeded[11] = "CSC520, CSC539, CSC542, CSC543, CSC544";
            }

        }

        return coreNeeded;
    }

    // Checks if supporting courses have been met
    public String[] getSupportingRemaining(String concentration) {
        String[] supportingNeeded = new String[11];

        List<String> supportingComplete = jdbcTemplate.query(
                "SELECT title FROM course",
                (rs, rowNum) -> rs.getString(1));

        if ( concentration.equals("General") ){
            String[] supportingCourses = { "EET252 ", "MAT234 ", "MAT239 ", "MAT244 ", "STA270" };

            // Checks each course to make sure that they have completed General concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

            // Checks the primary physical science requirement
            if(     (supportingComplete.contains("BIO111 ") && supportingComplete.contains("BIO112 ")) ||
                    (supportingComplete.contains("CHE111 ") && supportingComplete.contains("CHE111L") &&
                            (supportingComplete.contains("CHE112 ") && supportingComplete.contains("CHE112L"))) ||
                    (supportingComplete.contains("GLY108 ") && supportingComplete.contains("GLY109 ")) ||
                    (supportingComplete.contains("PHY201 ") && supportingComplete.contains("PHY202 "))
            ){
                if( supportingComplete.contains("BIO111 ") && supportingComplete.contains("BIO112 ") ){
                    supportingComplete.remove("BIO111 ");
                    supportingComplete.remove("BIO112 ");

                } else if ( supportingComplete.contains("CHE111 ") && supportingComplete.contains("CHE111L") &&
                            supportingComplete.contains("CHE112 ") && supportingComplete.contains("CHE112L") ){
                    supportingComplete.remove("CHE111 ");
                    supportingComplete.remove("CHE111L");
                    supportingComplete.remove("CHE112 ");
                    supportingComplete.remove("CHE112L");

                } else if ( supportingComplete.contains("GLY108 ") && supportingComplete.contains("GLY109 ") ){
                    supportingComplete.remove("GLY108 ");
                    supportingComplete.remove("GLY109 ");

                } else if ( supportingComplete.contains("PHY201 ") && supportingComplete.contains("PHY202 ") ){
                    supportingComplete.remove("PHY201 ");
                    supportingComplete.remove("PHY202 ");
                }
            } else {
                supportingNeeded[5] = "Credit for a Physical Science sequence needed.";
            }

            // Checks the extra physical science requirement
            boolean extraPhysicalScience = false;
            if( (supportingComplete.contains("BIO111 ") || supportingComplete.contains("BIO112 ")) &&
                    ((supportingComplete.contains("CHE111 ") && supportingComplete.contains("CHE111L")) ||
                            (supportingComplete.contains("CHE112 ") && supportingComplete.contains("CHE112L")))){
                extraPhysicalScience = true;
            } else if( (supportingComplete.contains("BIO111 ") || supportingComplete.contains("BIO112 ")) &&
                    (supportingComplete.contains("GLY108 ") || supportingComplete.contains("GLY109 "))
            ){
                extraPhysicalScience = true;
            } else if( (supportingComplete.contains("BIO111 ") || supportingComplete.contains("BIO112 ")) &&
                    (supportingComplete.contains("PHY201 ") || supportingComplete.contains("PHY202 "))
            ){
                extraPhysicalScience = true;
            } else if ( ((supportingComplete.contains("CHE111 ") && supportingComplete.contains("CHE111L")) ||
                    (supportingComplete.contains("CHE112 ") && supportingComplete.contains("CHE112L"))) &&
                    (supportingComplete.contains("GLY108 ") || supportingComplete.contains("GLY109 "))
            ){
                extraPhysicalScience = true;
            } else if ( ((supportingComplete.contains("CHE111 ") && supportingComplete.contains("CHE111L")) ||
                    (supportingComplete.contains("CHE112 ") && supportingComplete.contains("CHE112L"))) &&
                    (supportingComplete.contains("PHY201 ") || supportingComplete.contains("PHY202 "))
            ){
                extraPhysicalScience = true;
            } else if( (supportingComplete.contains("GLY108 ") || supportingComplete.contains("GLY109 ")) &&
                    (supportingComplete.contains("PHY201 ") || supportingComplete.contains("PHY202 "))
            ){
                extraPhysicalScience = true;
            }

            if (!extraPhysicalScience) {
                supportingNeeded[6] = "(BIO111 and BIO112, or CHE111/CHE111L and CHE112/CHE112L, or GLY108 and GLY109, or PHY201 and PHY202)";
            }


        } else if ( concentration.equals("Digital Forensics and Cybersecurity") ){
            String[] supportingCourses = { "CMS210 ", "FOR301 ", "FOR401 ", "FOR465 ","MAT234 ", "STA270" };

            // Checks each course to make sure that they have completed Digital Forensics and Cybersecurity concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

            // Checks if they have completed their physical science requirement
            int physcialScienceCount = 0;
            String physcialScienceNotCompleted = "";
            if( supportingComplete.contains("BIO111 ") ){
                physcialScienceCount++;
            } else {
                physcialScienceNotCompleted += "BIO111, ";
            }
            if( supportingComplete.contains("CHE111 ") || supportingComplete.contains("CHE111L") ){
                physcialScienceCount++;
            } else {
                physcialScienceNotCompleted += "CHE111, CHE111L, ";
            }
            if( supportingComplete.contains("PHY201 ") ){
                physcialScienceCount++;
            } else {
                physcialScienceNotCompleted += "PHY201, ";
            }
            if( physcialScienceCount < 2 ){
                supportingNeeded[6] = physcialScienceNotCompleted.substring(0, physcialScienceNotCompleted.length()-2);
            }

            String[] reACourses = { "CRJ101 ", "PLS216 ", "PLS316 ", "PLS416 " };
            // Checks if they have completed their restricted elective A requirement
            int reA = 0;
            String reANotCompleted = "";

            for (int i = 0; i < reACourses.length; i++){
                if( supportingComplete.contains(reACourses[i]) ) {
                    reA++;
                } else {
                    reANotCompleted += reACourses[i].substring(0, reACourses[i].length()-1 ) + ", ";
                }
            }

            if( reA < 2 ){
                supportingNeeded[7] = reANotCompleted.substring(0, reANotCompleted.length()-2);
            }

            // Checks if they have completed their restricted elective B requirement
            String[] reBCourses = { "CIS320 ", "CIS325 ", "HLS400 ", "HLS401 ", "HLS402 ", "HLS403 ", "NET303 ", "NET354 ", "NET454 " };
            int reB = 0;
            String reBNotCompleted = "";

            for (int i = 0; i < reBCourses.length; i++){
                if( supportingComplete.contains(reBCourses[i]) ) {
                    reB++;
                } else {
                    reBNotCompleted += reBCourses[i].substring(0, reBCourses[i].length()-1 ) + ", ";
                }
            }

            if( reB < 2 ){
                supportingNeeded[8] = reBNotCompleted.substring(0, reBNotCompleted.length()-2);
            }

        } else if ( concentration.equals("Computer Technology")){
            String[] supportingCourses = { "EET252 ", "NET302 ", "NET303 ", "NET343 ","NET354 ", "NET403", "NET454" };

            // Checks each course to make sure that they have completed Computer Technology concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

            if( !supportingComplete.contains("NET344 ") && !supportingComplete.contains("NET395 ") ){
                supportingNeeded[7] = "NET344, NET395";
            }
            if( !supportingComplete.contains("MAT234 ") && !supportingComplete.contains("MAT211 ") ){
                supportingNeeded[8] = "MAT234, MAT211";
            }

        } else if ( concentration.equals("Interactive Multimedia")){
            String[] supportingCourses = { "BEM200 ", "EET252 ", "MAT234 ", "MAT239 ","MAT244 ", "MUS290", "PHY201", "STA270" };

            // Checks each course to make sure that they have completed Computer Technology concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

            if( !supportingComplete.contains("ART200 ") && !( supportingComplete.contains("ARH390 ") || supportingComplete.contains("ARH391 ") ) ){
                supportingNeeded[8] = "ART200 and ARH390 or ARH391";
            }

            if( !( supportingComplete.contains("ART100 ") ||  supportingComplete.contains("TEC190 ") || supportingComplete.contains("GCM313 ") ) ){
                supportingNeeded[9] = "ART100, TEC190, GCM313";
            }

            if( !( supportingComplete.contains("GEO353 ") && supportingComplete.contains("GEO453 ") ) ||
                    !( supportingComplete.contains("MKT301 ") && supportingComplete.contains("MKT310 ") ) ||
                    !( supportingComplete.contains("MGT301 ") && supportingComplete.contains("MGT465 ") )
            ){
                supportingNeeded[10] = "GEO353 and GEO453, MKT301 and MKT310, MGT301 and MGT465";
            }

        } else if ( concentration.equals("Artificial Intelligence in data Science")){
            String[] supportingCourses = { "MAT234 ", "MAT244 ", "STA270 ", "STA340 ","STA375 ", "STA380", "STA575", "STA580", "STA585" };

            // Checks each course to make sure that they have completed Artificial Intelligence in data Science concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

            if( !(  (supportingComplete.contains("AEM202 ") &&
                            (supportingComplete.contains("AEM332 ") || supportingComplete.contains("AEM336 ") || supportingComplete.contains("AEM506 "))) ||
                    (supportingComplete.contains("BIO315 ") && supportingComplete.contains("BIO553 ")) ||
                    (supportingComplete.contains("ECO230 ") && supportingComplete.contains("ECO231 ")) ||
                    (supportingComplete.contains("ECO320 ") && supportingComplete.contains("ECO420 ")) ||
                    (supportingComplete.contains("RMI370 ") &&
                            (supportingComplete.contains("RMI372 ") || supportingComplete.contains("RMI374 ") || supportingComplete.contains("INS378 "))) ||
                    (supportingComplete.contains("STA520 ") && supportingComplete.contains("STA521 ")) ||
                    (supportingComplete.contains("CSC332 ") &&
                            (supportingComplete.contains("CSC542 ") || supportingComplete.contains("CSC547 ") || supportingComplete.contains("CSC548 "))) ||
                    (supportingComplete.contains("GEO353 ") && supportingComplete.contains("GEO453 ")) ||
                    (supportingComplete.contains("HLS401 ") || supportingComplete.contains("HLS402 ")) ||
                    (supportingComplete.contains("HLS401 ") || supportingComplete.contains("HLS403 ")) ||
                    (supportingComplete.contains("HLS402 ") || supportingComplete.contains("HLS403 "))
            ) ) {
                supportingNeeded[9] = "AEM202 and (AEM332 or AEM336 or AEM506), or BIO315 and BIO533, or ECO230 and ECO231, or RMI370 and (RMI372 or RMI374 or INS378), or STA520 and STA521, or CSC332 and (CSC542 or CSC547 or CSC548), or GEO353 and GEO453, or 2 from (HLS401, HLS402, or HLS403)";
            }

        } else if ( concentration.equals("Statistical Computing") ) {
            String[] supportingCourses = { "MAT124 ", "MAT224 ", "STA270 ", "STA320 ","STA375 ", "STA501", "STA575", "STA580", "STA585" };

            // Checks each course to make sure that they have completed Statistical Computing concentration
            for (int i = 0; i < supportingCourses.length; i++){
                if( !supportingComplete.contains(supportingCourses[i]) ){
                    supportingNeeded[i] = supportingCourses[i];
                }
            }

        }
        return supportingNeeded;
    }

    // Checks if concentration requirements are done
    public String[] getConcentrationRemaining(String concentration) {
        String[] concentrationNeeded = new String[10];

        List<String> concentrationComplete = jdbcTemplate.query(
                "SELECT title FROM course WHERE title LIKE ?", new Object[]{"CSC%"},
                (rs, rowNum) -> rs.getString(1));

        if ( concentration.equals("General") ){
            String[] concentrationCourses = { "CSC311 ", "CSC320 ", "CSC360 ", "CSC400 ","CSC440 ", "CSC460", "CSC541", "CSC545" };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            if ( !concentrationComplete.contains("CSC494 ") && !concentrationComplete.contains("CSC495 ") && !concentrationComplete.contains("CSC496 ") ){
                concentrationNeeded[8] = "CSC494, CSC495, CSC496";
            }

        } else if ( concentration.equals("Computer Technology") ){
            String[] concentrationCourses = { "CSC360 ", "CSC440 ", "CSC460 ", "CSC545 ","CSC349 " };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            if ( !concentrationComplete.contains("CSC330 ") && !concentrationComplete.contains("CSC544 ") ){
                concentrationNeeded[5] = "CSC330, CSC544";
            }

            // Need one CSC course about 300 level excluding CSC 349, 490, 494, 495, and 496
            // Checking from 300 level to 999 level
            boolean above300Course = false;
            for (int i = 300; i < 999; i++){
                if ( !(i == 349 || i == 490 || i == 494 || i == 495 || i == 496)  ){
                    if ( concentrationComplete.contains("CSC" + i + " ") ){
                        above300Course = true;
                        break;
                    }
                }
            }

            if ( !above300Course ) {
                concentrationNeeded[6] = "CSC300+";
            }

        } else if ( concentration.equals("Interactive Multimedia") ){
            String[] concentrationCourses = { "CSC140 ", "CSC315 ", "CSC316 ", "CSC491 ","CSC550 " };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            if ( !concentrationComplete.contains("CSC520 ") && !concentrationComplete.contains("CSC555 ") ){
                concentrationNeeded[5] = "CSC520, CSC555";
            }

            if ( !concentrationComplete.contains("CSC300 ") && !concentrationComplete.contains("CSC308 ") && !concentrationComplete.contains("CSC309 ") &&
                    !concentrationComplete.contains("CSC311 ") && !concentrationComplete.contains("CSC320 ") &&
                    !concentrationComplete.contains("CSC330 ") && !concentrationComplete.contains("CSC332 ") &&
                    !concentrationComplete.contains("CSC350 ") && !concentrationComplete.contains("CSC360 ") &&
                    !concentrationComplete.contains("CSC390 ") && !concentrationComplete.contains("CSC400 ") &&
                    !concentrationComplete.contains("CSC425 ") && !concentrationComplete.contains("CSC440 ") &&
                    !concentrationComplete.contains("CSC460 ") && !concentrationComplete.contains("CSC490 ") &&
                    !concentrationComplete.contains("CSC507 ") && !concentrationComplete.contains("CSC538 ") &&
                    !concentrationComplete.contains("CSC540 ") && !concentrationComplete.contains("CSC541 ") &&
                    !concentrationComplete.contains("CSC542 ") && !concentrationComplete.contains("CSC544 ") &&
                    !concentrationComplete.contains("CSC545 ") && !concentrationComplete.contains("CSC546 ") &&
                    !concentrationComplete.contains("CSC547 ") && !concentrationComplete.contains("CSC548 ")
            ){
                concentrationNeeded[6] = "CSC300, CSC308, CSC309, CSC311, CSC320, CSC330, CSC332, CSC 350, CSC360, CSC 390, CSC400, CSC425, CSC 440, CSC460, CSC490, CSC507, CSC538, CSC540, CSC541, CSC542, CSC544, CSC545, CSC546, CSC547, CSC 548";
            }

            if ( !concentrationComplete.contains("CSC494 ") && !concentrationComplete.contains("CSC495 ") && !concentrationComplete.contains("CSC496 ") ){
                concentrationNeeded[7] = "CSC494, CSC495, CSC496";
            }

        } else if ( concentration.equals("Artificial Intelligence in data Science") ){
            String[] concentrationCourses = { "CSC311 ", "CSC320 ", "CSC545 ", "CSC546 ","CSC581 ", "CSC582 ", "CSC583 " };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            if ( !concentrationComplete.contains("CSC494 ") && !concentrationComplete.contains("CSC495 ") && !concentrationComplete.contains("CSC496 ") ){
                concentrationNeeded[7] = "CSC494, CSC495, CSC496";
            }

        } else if ( concentration.equals("Statistical Computing") ){
            String[] concentrationCourses = { "CSC320 ", "CSC544 ", "CSC545 " };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            // Checks for two courses from list
            int statComputTwoNeeded = 0;
            String statTwoNeededAvaliable = "";
            String[] statComputTwoNeededCourses = {"CSC300 ", "CSC315 ", "CSC316 ", "CSC332 ","CSC350 ", "CSC360 ",
                    "CSC390 ", "CSC400 ", "CSC425 ", "CSC440 ","CSC460 ", "CSC490 ", "CSC491 ", "CSC520 ", "CSC546 ",
                    "CSC547 ","CSC548 ", "CSC550 ", "CSC555 " };

            for (int i = 0; i < statComputTwoNeededCourses.length; i++){
                if( concentrationComplete.contains(statComputTwoNeededCourses[i]) ) {
                    statComputTwoNeeded++;
                } else {
                    statTwoNeededAvaliable += statComputTwoNeededCourses[i].substring(0, statComputTwoNeededCourses[i].length()-1 ) + ", ";
                }
            }

            if ( !concentrationComplete.contains("CSC308 ") && !concentrationComplete.contains("CSC309 ") ){
                statComputTwoNeeded++;
            } else {
                statTwoNeededAvaliable += "CSC308, CSC309, ";
            }

            if ( statComputTwoNeeded < 2 ){
                concentrationNeeded[3] = statTwoNeededAvaliable.substring(0, statTwoNeededAvaliable.length()-2);
            }
        } else if ( concentration.equals("Digital Forensics and Cybersecurity") ){
            String[] concentrationCourses = { "CSC332 ", "CSC360 ", "CSC400 ", "CSC460 ", "CSC520 ", "CSC544 ","CSC547 ", "CSC548 ", "CSC549 " };

            for (int i = 0; i < concentrationCourses.length; i++){
                if( !concentrationComplete.contains(concentrationCourses[i]) ){
                    concentrationNeeded[i] = concentrationCourses[i];
                }
            }

            // Checks for two courses from list
            int digitalForensicsTwoNeeded = 0;
            String digitalForensicsTwoNeededAvaliable = "";
            String[] digitalForensicsTwoNeededCourses = {"CSC300 ", "CSC315 ", "CSC316 ", "CSC320 ","CSC350 ", "CSC390 ",
                    "CSC425 ", "CSC440 ", "CSC490 ", "CSC491 ","CSC538 ", "CSC540 ", "CSC545 ", "CSC546 ", "CSC550 ",
                    "CSC547 ","CSC548 ", "CSC550 " };

            for (int i = 0; i < digitalForensicsTwoNeededCourses.length; i++){
                if( concentrationComplete.contains(digitalForensicsTwoNeededCourses[i]) ) {
                    digitalForensicsTwoNeeded++;
                } else {
                    digitalForensicsTwoNeededAvaliable += digitalForensicsTwoNeededCourses[i].substring(0, digitalForensicsTwoNeededCourses[i].length()-1 ) + ", ";
                }
            }

            if ( !concentrationComplete.contains("CSC308 ") && !concentrationComplete.contains("CSC309 ") ){
                digitalForensicsTwoNeeded++;
            } else {
                digitalForensicsTwoNeededAvaliable += "CSC308, CSC309, ";
            }

            if ( digitalForensicsTwoNeeded < 2 ){
                concentrationNeeded[3] = digitalForensicsTwoNeededAvaliable.substring(0, digitalForensicsTwoNeededAvaliable.length()-2);
            }

        }

        return concentrationNeeded;
    }

}

    /*
    Matthew:
        Remember that course titles are 6 or 7 digits in length.
        Assume that the digits are stored to the left, i.e.: "CSC190 ", instead of " CSC190" or "CSC 190".
        Other requirements to check for:
                        done - Writing intensive course (the only course with a 7-digit title - always ends with a 'W')
                        done - Upper division coursework (courses ending with digits 300+)
                        done -  ACCT requirement
                        done - Core
                        done - Supporting
                        done - concentration requirements
            *120-hour requirement? Free electives? (we may not need to consider these - show me what you've got once you finish functions that check progress on other requirements)
     */
