/* Shane Rocha
 * 4/19/23
 *
 * A program that calculates standard deviation and mean from a set of generated values.
 * Version 3 - incorporates fault tolerance and file I/O
 *
 * NOTE: This program considers any file in the /data directory that ends with .txt as a save file.
 *
 * TODO: Add file deletion
 * TODO: Replace File with Path
 *
 * I pledge that this program represents my own program code.
 * I received help from a few stackoverflow threads:
 *     - https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
 *     - https://stackoverflow.com/questions/10928387/check-file-extension-in-java
 *
 * Calculator used to compare results: https://www.calculatorsoup.com/calculators/statistics/standard-deviation-calculator.php
 *
 * I spent ~4 hrs writing this program. It's amazing how much more had to go into I/O stuff compared to Python.
 * I also really missed having lists.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StandardDeviation {
    private final Scanner scanner = new Scanner(System.in);
    private final String DIRECTORY = "data";
    private final String FILE_EXTENSION = ".txt";

    private ArrayList<String> saveNames = new ArrayList<>();  // a list of filename strings from /data directory
    private int[] values;
    private double standardDeviation;
    private double mean;

    public StandardDeviation(int[] values, double standardDeviation, double mean) {
        File directory = new File(DIRECTORY);
        try {
            if (!directory.exists())
                if (directory.mkdir()) {
                    System.out.println("Data directory created.");
                }
        } catch (SecurityException e) {
            System.out.println("Unable to create the save directory. Saving and loading will not function, check file permissions.");
        }

        this.values = values;
        this.standardDeviation = standardDeviation;
        this.mean = mean;
    }

    public StandardDeviation() {
        this(null, 0, 0);
    }

    public ArrayList<String> getSaveNames() {
        return saveNames;
    }

    public void setSaveNames(ArrayList<String> saveNames) {
        this.saveNames = saveNames;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    @Override
    public String toString() {
        System.out.print("\n");
        return "Mean: %.2f\nStandard Deviation: %.2f".formatted(getMean(), getStandardDeviation());
    }

    // utility methods
    public double calculateMean() {
        return calculateSum() / getValues().length;
    }

    public double calculateDeviation() {  // standard deviation of a sample
        int count = getValues().length;
        return Math.sqrt((calculateSumOfSquares() - (Math.pow(calculateSum(), 2) / count)) / (count - 1));
    }

    private double calculateSum() {
        double sum = 0;
        for (double value : getValues())
            sum += value;
        return sum;
    }

    private double calculateSumOfSquares() {
        double sumOfSquares = 0;
        for (double value : getValues())
            sumOfSquares += Math.pow(value, 2);
        return sumOfSquares;
    }

    private boolean isInString(String truthy, String falsy, String msgPrompt, String validPrompt) {
        /* Tests user input against characters that you either want to return true or false
         * validPrompt tells the user what values are expected
         * ex: truthy = "Yy", falsy = "NnQq", msgPrompt = "Continue?", validPrompt = "[yes/no]"
         *
         * currently more flexible than it needs to be
         */

        char userInput;
        System.out.print(msgPrompt + " " + validPrompt + "\n> ");

        while (true) {
            userInput = scanner.nextLine().charAt(0);

            if (truthy.indexOf(userInput) != -1)
                return true;
            else if (falsy.indexOf(userInput) != -1)
                return false;
            else
                System.out.printf("I didn't recognize that. Please enter %s \n> ", validPrompt);
            }
    }

    // print
    private boolean printExistingFiles(ArrayList<String> saveNames) {
        if (getSaveNames().isEmpty()) {
            System.out.println("No existing saves.");
            return false;
        }

        System.out.println("Existing save files:");
        for (String name : saveNames) {
            System.out.println(name);
        }
        return true;
    }

    public void printValues() {  // could fold toString() into here
        int counter = 0;
        System.out.print("Values:");
        for (int value : getValues()) {
            if (counter % 5 == 0) {
                System.out.print("\n\t");
            }
            System.out.printf("%4d ", value);
            counter++;
        }
        System.out.println();
    }

    // main features
    public void getUserInput() {
        boolean flag = true;
        int num_values = 0;

        System.out.print("How many numbers would you like to generate?\n> ");
        while (flag) {  // determine size of value set
            try {
                num_values = scanner.nextInt();
                flag = false;
            } catch (InputMismatchException e) {
                System.out.print("Please use an integer:\n> ");
            } finally {
                scanner.nextLine();
            }
        }

        if (isInString("Yy", "Nn", "Use random values?", "[yes/no]")) {
            randomGeneration(num_values);
            printValues();
        } else
            userGeneration(num_values);

        System.out.println("\nValues recorded.");
    }

    private void randomGeneration(int num_values) {
        int[] values = new int[num_values];
        for (int i = 0; i < num_values; i++) {
            values[i] = (int) (Math.random() * 501);
        }
        setValues(values);
    }

    private void userGeneration(int num_values) {
        int[] values = new int[num_values];
        boolean flag = true;

        while (flag) {  // populate set
            try {
                System.out.printf("Enter %d whole numbers, each separated by a space:\n> ", num_values);
                for (int i = 0; i < num_values; i++)
                    values[i] = scanner.nextInt();  // kind of hard to look at, maybe use a different separator?
                flag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Try again: ");
            } finally {
                scanner.nextLine();
            }
        }
        setValues(values);
    }

    private void generateValues() {
        boolean flag = true;

        while (flag) {
            // get set of numbers and perform calculations
            getUserInput();
            setMean(calculateMean());
            setStandardDeviation(calculateDeviation());
            System.out.println(this);

            // prompt to save data
            System.out.println();
            if (isInString("Yy", "Nn", "Save data?", "[yes/no]")) {
                writeFile();
            }
            // create another set?
            flag = isInString("Yy", "NnQq", "Create a new set of numbers?", "[yes/no]");
            System.out.println();
        }
    }

    // file I/O
    private void writeFile() {
        /* saves a .txt file with the following:
         *   values (a series of values separated by spaces)
         *   standard deviation
         *   mean
         * entries are separated by newline
         */

        System.out.print("Enter a file name:\n");
        String userInput = "";

        // validation
        boolean flag = true;
        while (flag) {
            System.out.print("> ");
            userInput = scanner.nextLine();
            if (userInput.contains(" ") || userInput.contains(".")) {  // very basic filename validation
                System.out.println("Filename cannot contain spaces or periods. Try again:");
            } else {
                flag = false;
            }
        }

        String filename = userInput + FILE_EXTENSION;

        File file = new File(DIRECTORY + "/" + filename);
        if (file.exists()) {
            System.out.println("File already exists.");
            if (!isInString("Yy", "Nn", "Overwrite?", "[yes/no]"))
                return;
        }

        // prepare to write
        StringBuilder valueString = new StringBuilder();
        for (int value : getValues()) {
            valueString.append(value).append(" ");  // convert values to a string
        }

        // write
        try (PrintWriter output = new PrintWriter(file)) {
            output.println(valueString);
            output.println(getStandardDeviation());
            output.println(getMean());
        } catch (FileNotFoundException e) {  // just in case
            System.out.println("Something went wrong. Exiting save process.");
            return;
        }

        if (!getSaveNames().contains(filename))
            getSaveNames().add(filename);  // update internal filename list
        System.out.println("File '" + filename + "' successfully saved.");
        System.out.println();
    }

    private void readFile() {
        updateExistingFiles();  // make sure filenames are up-to-date

        System.out.print("\nWhich file would you like to open?\n> ");
        String userInput = scanner.nextLine();

        if (!getSaveNames().contains(userInput)) {
            if (!getSaveNames().contains(userInput + ".txt")) {
                System.out.println("File not found.");
                return;
            } else
                userInput = userInput + ".txt";
        }

        File file = new File(DIRECTORY + "/" + userInput);
        try {
            Scanner input = new Scanner(file);
            while (input.hasNext()) {

                // convert values back into an array of doubles
                String[] s = input.nextLine().split(" ");
                int[] values = new int[s.length];
                for (int i = 0; i < s.length; i++) {
                    values[i] = Integer.parseInt(s[i]);  // potential error if save is in wrong format
                }

                setValues(values);
                setStandardDeviation(Double.parseDouble(input.nextLine()));
                setMean(Double.parseDouble(input.nextLine()));

                System.out.println("\nFile successfully opened.\n");
                printValues();
                System.out.println(this);
            }
            input.close();

        } catch (IOException e) {  // just in case
            System.out.println("File could not be opened. Exiting.");
        }
    }

    private void updateExistingFiles() {
        // NOTE: this method assumes that any .txt file in the /data directory is a valid save

        File directory = new File(DIRECTORY);
        File[] fileList = directory.listFiles();
        String filename;
        ArrayList<String> saveNames = new ArrayList<>();

        if (fileList != null) {
            for (File file : fileList) {
                filename = file.getName();
                if (filename.substring(filename.lastIndexOf(".")).equals(FILE_EXTENSION)) {
                    saveNames.add(filename);
                }
            }
            setSaveNames(saveNames);  // update internal record
        }
    }

    private void readFileMenu() {
        boolean flag = true;

        while (flag) {
            if (printExistingFiles(getSaveNames())) {
                readFile();
            }
            System.out.println();
            flag = isInString("Yy", "Nn", "Open a different file?", "[yes/no]");
            System.out.println();
        }
    }

    // app
    public void run() {
        boolean flag = true;
        int userInput;
        System.out.println("Welcome to the Standard Deviation Calculator!");
        String options = """
                Options:
                1 -> Generate a set of numbers
                2 -> Open an existing file
                0 -> Exit
                
                Enter an option:
                >\s""";

        updateExistingFiles();

        while (flag) {
            System.out.print(options);

            try {
                userInput = scanner.nextInt();
                scanner.nextLine();
                System.out.println();

                if (userInput == 1)
                    generateValues();
                else if (userInput == 2)
                    readFileMenu();
                else if (userInput == 0) {
                    flag = false;
                    System.out.println("See you next time!");
                } else
                    System.out.println("Command not recognized.");

            } catch (InputMismatchException e) {
                System.out.println("Invalid command.\n");
                scanner.nextLine();
            }
        }
        System.out.println("Exiting...");
    }

    public static void main(String[] args) {
        StandardDeviation stDev = new StandardDeviation();
        stDev.run();
    }
}