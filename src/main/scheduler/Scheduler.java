package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Caregiver;
import scheduler.model.Vaccine;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.naming.OperationNotSupportedException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Scheduler {

    private static final int HASH_STRENGTH = 10;
    private static final int KEY_LENGTH = 16;

    public static void main(String[] args) {
        // pre-define the three types of authorized vaccines
        // note: it's a poor practice to hard-code these values, but we will do this ]
        // for the simplicity of this assignment
        // the start of the command-line interface
        System.out.println();
        System.out.println("Welcome to the COVID-19 Vaccine Reservation Scheduling Application!");

        // process create account and login
        processCreateAndLogin();
    }

    private static void processCreateAndLogin() {
        while (true) {
            System.out.println();
            System.out.println(" *** Please enter one of the following commands *** ");
            System.out.println("> Create");
            System.out.println("> Login");
            System.out.println("> Quit");
            // read user input
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> ");
            String response = "";
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            if (response.equalsIgnoreCase("Create")) {
                createAccount();
            } else if (response.equalsIgnoreCase("Login")) {
                login();
            } else if (response.equalsIgnoreCase("Quit")) {
                System.out.println("Goodbye!");
                return;
            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    private static void createAccount() {
        System.out.println();
        System.out.println(" *** Please enter one of the following account types *** ");
        System.out.println("> Patient");
        System.out.println("> Caregiver");
        // read user input
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        String response = "";

        while (true) {
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            if (response.equalsIgnoreCase("Patient")
                    || response.equalsIgnoreCase("Caregiver")) {

                String user = readUsername();
                String email = readEmail();
                String pass = readPassword();

                byte[] salt = generateSalt();
                byte[] hash = generateHash(pass, salt);

                if (response.equalsIgnoreCase("Patient")) {
                    // TODO: handle the case for patients
                } else {
                    try {
                        // calling Caregiver.CaregiverBuilder(user, email, salt, hash).build() will insert the
                        // information in the caregiver table, no need to store this information in memory now
                        new Caregiver.CaregiverBuilder(user, email, salt, hash).build();
                    } catch (SQLException e) {
                        System.out.println("Create failed");
                        e.printStackTrace();
                    }
                }
                System.out.println();
                System.out.println(" *** Account created successfully *** ");
                break;
            } else {
                System.out.println("Invalid input!");
            }
        }

        // once we've created the user, we can go back to processing create and login again
        processCreateAndLogin();
    }

    private static String readUsername() {
        System.out.println();
        System.out.println(" *** Please enter a unique username *** ");
        // read user input
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        String response = "";
        while (true) {
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // check if the username has been taken
            if (usernameExistsInCaregiver(response)) {
                System.out.println();
                System.out.println(" *** Username taken, try again *** ");
                System.out.print("> ");
            } else {
                break;
            }
        }
        return response;
    }

    private static boolean usernameExistsInCaregiver(String username){
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
            System.out.println("Error occured when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static String readEmail() {
        System.out.println();
        System.out.println(" *** Please enter a unique email *** ");
        // read user input
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        String response = "";
        while (true) {
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // check if the email has been taken
            if (emailExistsInCaregiver(response)) {
                System.out.println();
                System.out.println(" *** Email taken, try again *** ");
                System.out.print("> ");
            } else {
                break;
            }
        }
        return response;
    }

    private static boolean emailExistsInCaregiver(String email) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Caregivers WHERE Email = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occured when checking email");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static String readPassword() {
        System.out.println();
        System.out.println(" *** Please enter a password longer than five characters that"
                + " contains at least one special character (!, @, #, $, or &) *** ");
        // read user input
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        String response = "";
        while (true) {
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            if (response.length() < 5 || !(response.contains("!") || response.contains("@")
                    || response.contains("#") || response.contains("$")
                    || response.contains("&"))) {
                System.out.println();
                System.out.println(" *** Not a strong enough password, try again *** ");
            } else {
                break;
            }
        }
        return response;
    }

    private static void login() {
        // TODO: add logic for logging in
        // case 0: invalid login credentials, re-try
        // case 1: user is a patient, display the corresponding commands and wait for input
        // case 2: user is a caregiver, display the corresponding commands and wait for input
    }

    private static byte[] generateSalt() {
        // Generate a random cryptographic salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] generateHash(String password, byte[] salt) {
        // Specify the hash parameters
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);

        // Generate the hash
        SecretKeyFactory factory = null;
        byte[] hash = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new IllegalStateException();
        }
        return hash;
    }
}
