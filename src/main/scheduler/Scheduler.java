package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Vaccine;

import javax.naming.OperationNotSupportedException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Scheduler {
    public static void main(String[] args) {
        // pre-define the three types of authorized vaccines
        // note: it's a poor practice to hard-code these values, but we will do this ]
        // for the simplicity of this assignment

        // construct a map of vaccineName -> vaccineObject
        Map<String, Vaccine> vaccineMap = new HashMap<>();
        // construct the Pfizer vaccine and add to map
        vaccineMap.put("Pfizer", new Vaccine.VaccineBuilder("Pfizer", 2)
                .availableDoses(33).
                        build());
        // construct the Moderna vaccine abd add to map
        vaccineMap.put("Moderna", new Vaccine.VaccineBuilder("Moderna", 2)
                .availableDoses(44)
                .build());
        // construct the Moderna vaccine abd add to map
        vaccineMap.put("Johnson & Johnson", new Vaccine.VaccineBuilder("Johnson & Johnson", 1)
                .availableDoses(7)
                .build());

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
                break;
            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    private static void createAccount() {
        // TODO: add logic for creating an account

        // once we've created the user, we can go back to processing create and login again
        processCreateAndLogin();
    }

    private static void login() {
        // TODO: add logic for logging in
        // case 0: invalid login credentials, re-try
        // case 1: user is a patient, display the corresponding commands and wait for input
        // case 2: user is a caregiver, display the corresponding commands and wait for input
    }
}
