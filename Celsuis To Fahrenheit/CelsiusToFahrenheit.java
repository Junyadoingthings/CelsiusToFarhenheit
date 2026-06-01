
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;

public class CelsiusToFahrenheit {

    // 'static' means it belongs to the class, not to any object.
    // It can be called directly as CelsiusToFahrenheit.convertToFahrenheit(c).
    // Formula: F = (C × 9/5) + 3



    public static double convertToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5) + 32;
    }
    // 9.0 (not 9) forces floating-point division instead of integer division




    public static void writeToFile(String conversion) {
        // ── Method: writeToFile() ────────────────────────────────
        // Appends one conversion string to "temperatures.txt".
        // 'true' in FileWriter means APPEND mode — existing content
        // is preserved; the new line is added at the end.



        try {

            FileWriter writer = new FileWriter("temperatures.txt", true);
            writer.write(conversion + "\n");// \n starts a new line after each entry


            writer.close();// always close to flush and release the file


        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }


    // ── Method: readFromFile() ───────────────────────────────
    // Reads all lines from "temperatures.txt" and prints them.
    // BufferedReader wraps FileReader for efficient line-by-line reading.
    public static void readFromFile() {
        System.out.println("--- Previously Converted Temperatures ---");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("temperatures.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) { // File may not exist on the first run — that is fine
            System.out.println("No previous records found.");
        }
        System.out.println("-----------------------------------------");
    }



    // Purpose: Handles all user interaction in a separate thread.
    //   Asks the user for temperatures in a loop
    //   Converts each input and displays the result
    //  Saves each conversion to the database AND to file
    //     (file writing is delegated to FileThread)
    // 'static' inner class — it does not need a reference to
    // the outer CelsiusToFahrenheit object to function.

    static class InputThread extends Thread {
        // run() is the body of the thread.
        // It is called by the JVM when you call inputThread.start()
        // — never call run() directly, as that would execute on
        // the current thread instead of a new one

        public void run() {
            Scanner scanner = new Scanner(System.in);
            // In-memory list of all conversions done this session
            ArrayList<String> conversions = new ArrayList<>();

            String continueChoice;
            // Database setup
            // Create the table if it does not already exist,
            // then display any records already saved in the DB.
            DatabaseHelper.createTable();
            DatabaseHelper.readTemperatures();

            // conversion loop
            do {
                System.out.print("Enter Temperature in Celsius: ");
                String input = scanner.nextLine();

                try {
                    //user string into double
                    double temperature = Double.parseDouble(input);
                    double fahrenheit = convertToFahrenheit(temperature);

                    System.out.println("-----------------");
                    System.out.println(temperature + "°C = " + fahrenheit + "°F");
                    System.out.println("-----------------");

                    String conversion = temperature + "°C = " + fahrenheit + "°F";
                    conversions.add(conversion);

                    DatabaseHelper.saveTemperature(conversion);

                    FileThread fileThread = new FileThread(conversion);
                    fileThread.start();
                    try {
                        fileThread.join();
                    } catch (InterruptedException e) {
                        System.out.println("Thread interrupted: " + e.getMessage());
                    }

                    System.out.println("All Conversions So Far:");
                    for (String c : conversions) {
                        System.out.println(c);
                    }
                    System.out.println("-----------------");

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + input);
                }

                System.out.print("Convert another? (yes/no): ");
                continueChoice = scanner.nextLine().trim().toUpperCase();

            } while (continueChoice.equals("YES") || continueChoice.equals("Y"));

            scanner.close();
        }
    }

    // static - inside CelsiusToFahrenheit
    static class FileThread extends Thread {
        private String conversion;

        public FileThread(String conversion) {
            this.conversion = conversion;
        }

        public void run() {
            writeToFile(conversion);
            System.out.println("Saved to file: " + conversion);
        }
    }

    // main - inside CelsiusToFahrenheit
    public static void main(String[] args) {
        readFromFile();

        InputThread inputThread = new InputThread();
        inputThread.start();

        try {
            inputThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }
    }



}













