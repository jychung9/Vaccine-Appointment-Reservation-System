package scheduler;

import scheduler.model.Vaccine;

import javax.naming.OperationNotSupportedException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Scheduler {
    public static void main(String[] args) throws IOException {
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
        System.out.println();

        // process create account and login
        processCreateAndLogin();

    }

    private static void processCreateAndLogin() throws IOException {
        System.out.println(" *** Please enter one of the following commands *** ");
        System.out.println("> Create");
        System.out.println("> Login");
        System.out.println("> Quit");
        // read user input
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        String response = r.readLine();
    }

    private static void createAccount() {
        // TODO: replace this with the logic for creating an account
    }

    private static void login() {
        // TODO: replace this with the logic for logging in
    }
}
