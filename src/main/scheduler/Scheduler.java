package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Caregiver;
import scheduler.model.Patient;
import scheduler.model.Vaccine;
import scheduler.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

public class Scheduler {

    // objects to keep track of the currently logged-in user
    // Note: it is always true that at most one of currentCaregiver and currentPatient is not null
    //       since only one user can be logged-in at a time
    private static Caregiver currentCaregiver = null;
    private static Patient currentPatient = null;

    public static void main(String[] args) {
        // printing greetings text
        System.out.println();
        System.out.println("Welcome to the COVID-19 Vaccine Reservation Scheduling Application!");
        System.out.println("*** Please enter one of the following commands ***");
        System.out.println("> create_patient <username> <password>");  //TODO: implement create_patient (Part 1)
        System.out.println("> create_caregiver <username> <password>");
        System.out.println("> login_patient <username> <password>");  // TODO: implement login_patient (Part 1)
        System.out.println("> login_caregiver <username> <password>");
        System.out.println("> search_caregiver_schedule <date>");  // TODO: implement search_caregiver_schedule (Part 2)
        System.out.println("> reserve <date> <vaccine>");  // TODO: implement reserve (Part 2)
        System.out.println("> upload_availability <date>");
        System.out.println("> cancel <appointment_id>");  // TODO: implement cancel (extra credit)
        System.out.println("> add_doses <vaccine> <number>");
        System.out.println("> show_appointments");  // TODO: implement show_appointments (Part 2)
        System.out.println("> logout");  // TODO: implement logout (Part 2)
        System.out.println("> quit");
        System.out.println();

        // read input from user
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String response = "";
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // split the user input by spaces
            String[] tokens = response.split(" ");
            // check if input exists
            if (tokens.length == 0) {
                System.out.println("Please try again!");
                continue;
            }
            // determine which operation to perform
            String operation = tokens[0];
            if (operation.equals("create_patient")) {
                createPatient(tokens);
            } else if (operation.equals("create_caregiver")) {
                createCaregiver(tokens);
            } else if (operation.equals("login_patient")) {
                loginPatient(tokens);
            } else if (operation.equals("login_caregiver")) {
                loginCaregiver(tokens);
            } else if (operation.equals("search_caregiver_schedule")) {
                searchCaregiverSchedule(tokens);
            } else if (operation.equals("reserve")) {
                reserve(tokens);
            } else if (operation.equals("upload_availability")) {
                uploadAvailability(tokens);
            } else if (operation.equals("cancel")) {
                cancel(tokens);
            } else if (operation.equals("add_doses")) {
                addDoses(tokens);
            } else if (operation.equals("show_appointments")) {
                showAppointments(tokens);
            } else if (operation.equals("logout")) {
                logout(tokens);
            } else if (operation.equals("quit")) {
                System.out.println("Bye!");
                return;
            } else {
                System.out.println("Invalid operation name!");
            }
        }
    }

    private static void createPatient(String[] tokens) {
        // TODO: Part 1
        // create_patient <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsPatient(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the patient
        try {
            currentPatient = new Patient.PatientBuilder(username, salt, hash).build();
            // save to caregiver information to our database
            currentPatient.saveToDB();
            System.out.println(" *** Account created successfully *** ");
        } catch (SQLException e) {
            System.out.println("Create failed");
            e.printStackTrace();
        }
    }

    private static void createCaregiver(String[] tokens) {
        // create_caregiver <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsCaregiver(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            currentCaregiver = new Caregiver.CaregiverBuilder(username, salt, hash).build();
            // save to caregiver information to our database
            currentCaregiver.saveToDB();
            System.out.println(" *** Account created successfully *** ");
        } catch (SQLException e) {
            System.out.println("Create failed");
            e.printStackTrace();
        }
    }

    private static boolean usernameExistsPatient(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Patient WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static boolean usernameExistsCaregiver(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Caregivers WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void loginPatient(String[] tokens) {
        // TODO: Part 1
        // login_patient <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentPatient != null || currentCaregiver != null) {
            System.out.println("Already logged-in!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Patient patient = null;
        try {
            patient = new Patient.PatientGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when logging in");
            e.printStackTrace();
        }
        // check if the login was successful
        if (patient == null) {
            System.out.println("Login was unsuccessful, please try again");
        } else {
            System.out.println("Patient logged in as: " + username);
            currentPatient = patient;
        }
    }

    private static void loginCaregiver(String[] tokens) {
        // login_caregiver <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("Already logged-in!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Caregiver caregiver = null;
        try {
            caregiver = new Caregiver.CaregiverGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when logging in");
            e.printStackTrace();
        }
        // check if the login was successful
        if (caregiver == null) {
            System.out.println("Please try again!");
        } else {
            System.out.println("Caregiver logged in as: " + username);
            currentCaregiver = caregiver;
        }
    }

    private static void searchCaregiverSchedule(String[] tokens) {
        // TODO: Part 2
        // check if user is logged in
        if (currentPatient == null && currentCaregiver == null) {
            System.out.println("Please login first!");
            return;
        }
        // check the length of tokens, needs to be 2
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        Patient patient = null;

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String caregiversAvail = "SELECT Username FROM Availabilities WHERE Time = ?";
        String allVaccines = "SELECT * FROM Vaccines";
        try {
            Date d = Date.valueOf(date);
            PreparedStatement statement1 = con.prepareStatement(caregiversAvail);
            statement1.setDate(1, d);
            ResultSet resultSet = statement1.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("Username");
                System.out.println("Caregiver: " + username);
            }

            PreparedStatement statement2 = con.prepareStatement(allVaccines);
            ResultSet rs = statement2.executeQuery();
            while (rs.next()) {
                String vaccine = rs.getString("Name");
                int doseNum = rs.getInt("Doses");
                System.out.println("Vaccine:" + vaccine + " Number of doses: " + doseNum);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid date!");
        } catch (SQLException e) {
            System.out.println("Error occurred when uploading vaccines");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
    }

    private static void reserve(String[] tokens) {
        // TODO: Part 2
        // check 1: check if the current logged-in user is a patient
        if (currentPatient == null) {
            System.out.println("Please login as a patient first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 2 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        String vaccine = tokens[2];
        Vaccine vaccineDose = null;

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String addApp = "INSERT INTO Appointments(AID, Time, vaccine, caregiver_username, patient_username) VALUES (?, ? , ?, ?, ?)";
        String getCaregiver = "SELECT TOP 1 Username FROM Availabilities WHERE Time = ?";
        Date d = Date.valueOf(date);
        String caregiver = "";
        try {
            String uniqueID = UUID.randomUUID().toString();
            // Get an available caregiver for the date selected
            PreparedStatement statement1 = con.prepareStatement(getCaregiver);
            statement1.setDate(1, d);
            ResultSet rs = statement1.executeQuery();
            rs.next();
            caregiver = rs.getString("Username");

            // Insert into Appointments database
            PreparedStatement statement2 = con.prepareStatement(addApp);
            statement2.setString(1, uniqueID);
            statement2.setDate(2, d);
            statement2.setString(3, vaccine);
            statement2.setString(4, caregiver);
            statement2.setString(5, currentPatient.getUsername());
            statement2.executeUpdate();
            System.out.println("Your reservation has been set!");
            System.out.println("Your appointment ID will be " + uniqueID + " and your caregiver is " + caregiver);
        } catch (SQLException e) {
            System.out.println("Error when uploading reservation!");
            e.printStackTrace();
        }
        //delete from availabilities
        String deleteSlot = "DELETE FROM Availabilities WHERE Time = ? AND Username = ?";
        try {
            PreparedStatement statement3 = con.prepareStatement(deleteSlot);
            statement3.setDate(1, d);
            statement3.setString(2, caregiver);
            statement3.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error when deleting caregiver availability!");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        try {
            vaccineDose = new Vaccine.VaccineGetter(vaccine).get();
            vaccineDose.decreaseAvailableDoses(1);
        } catch (SQLException e) {
            System.out.println("Error occurred when decreasing doses");
            e.printStackTrace();
        }
    }

    private static String getFirstUsername(Date d) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
        String caregiver = "";
        String getCaregiver = "SELECT Username FROM Availabilities WHERE Time = ?";
        try {
            // Get an available caregiver for the date selected
            PreparedStatement statement1 = con.prepareStatement(getCaregiver);
            statement1.setDate(1, d);
            ResultSet rs = statement1.executeQuery();
            caregiver += rs.getString("Username");
        } catch (SQLException e) {
            System.out.println("Error when uploading caregivers!");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return caregiver;
    }

    private static boolean appointmentIdExists(int num) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectAID = "SELECT AID FROM Appointments";
        try {
            PreparedStatement statement = con.prepareStatement(selectAID);
            ResultSet rs = statement.executeQuery();
            int[] appointmentIds;
            while (rs.next()) {
                int ids = rs.getInt("AID");

            }
        } catch (SQLException e) {
            System.out.println("Error occurred when setting appointment ID");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void uploadAvailability(String[] tokens) {
        // upload_availability <date>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 2 to include all information (with the operation name)
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        try {
            Date d = Date.valueOf(date);
            currentCaregiver.uploadAvailability(d);
            System.out.println("Availability uploaded!");
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid date!");
        } catch (SQLException e) {
            System.out.println("Error occurred when uploading availability");
            e.printStackTrace();
        }
    }

    private static void cancel(String[] tokens) {
        // TODO: Extra credit
    }

    private static void addDoses(String[] tokens) {
        // add_doses <vaccine> <number>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String vaccineName = tokens[1];
        int doses = Integer.parseInt(tokens[2]);
        Vaccine vaccine = null;
        try {
            vaccine = new Vaccine.VaccineGetter(vaccineName).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when adding doses");
            e.printStackTrace();
        }
        // check 3: if getter returns null, it means that we need to create the vaccine and insert it into the Vaccines
        //          table
        if (vaccine == null) {
            try {
                vaccine = new Vaccine.VaccineBuilder(vaccineName, doses).build();
                vaccine.saveToDB();
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        } else {
            // if the vaccine is not null, meaning that the vaccine already exists in our table
            try {
                vaccine.increaseAvailableDoses(doses);
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        }
        System.out.println("Doses updated!");
    }

    private static void showAppointments(String[] tokens) {
        // TODO: Part 2
        // check for account log in
        if (currentPatient == null && currentCaregiver == null) {
            System.out.println("Please log in to or create a profile first!");
            return;
        }

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
        // for Patients
        if (currentCaregiver == null && currentPatient != null) {
            String patientApp = "SELECT AID, vaccine, Time, caregiver_username FROM Appointments WHERE patient_username = ?";
            try {
                PreparedStatement statement = con.prepareStatement(patientApp);
                statement.setString(1, currentPatient.getUsername());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String appointmentId = rs.getString("AID");
                    String vaccineName = rs.getString("vaccine");
                    Date time = rs.getDate("Time");
                    String caregiverName = rs.getString("caregiver_username");
                    System.out.println("Appointment: " + "Appointment ID: " + appointmentId + " Vaccine: " + vaccineName + " Date: " + time + " Caregiver: " + caregiverName);
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while uploading appointments");
                e.printStackTrace();
            } finally {
                cm.closeConnection();
            }
        }
        // for Caregivers
        if (currentCaregiver != null && currentPatient == null) {
            String patientApp = "SELECT AID, vaccine, Time, patient_username FROM Appointments WHERE caregiver_username = ?";
            try {
                PreparedStatement statement = con.prepareStatement(patientApp);
                statement.setString(1, currentCaregiver.getUsername());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String appointmentId = rs.getString("AID");
                    String vaccineName = rs.getString("vaccine");
                    Date time = rs.getDate("Time");
                    String patientName = rs.getString("patient_username");
                    System.out.println("Appointment: " + "Appointment ID: " + appointmentId + " Vaccine: " + vaccineName + " Date: " + time + " Patient: " + patientName);
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while uploading appointments");
                e.printStackTrace();
            } finally {
                cm.closeConnection();
            }
        }
    }

    private static void logout(String[] tokens) {
        // TODO: Part 2
        // check 1: if someone's already logged out
        if (currentCaregiver == null && currentPatient == null) {
            System.out.println("You are already logged-out!");
            return;
        }
        // check 2: the length for tokens need to be exactly 1 to include all information (with the operation name)
        if (tokens.length != 1) {
            System.out.println("Please try again!");
            return;
        }
        // if logged in as a caregiver
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("You have successfully logged out!");
            currentCaregiver = null;
            currentPatient = null;
        }
    }
}
