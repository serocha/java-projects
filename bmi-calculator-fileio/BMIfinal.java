/* Shane Rocha
 * 5/1/23
 *
 * This program calculates BMI from user input and optionally appends the result to myBMI.txt.
 *
 * I pledge that this program represents my own program code.
 * I received help from https://stackoverflow.com/questions/8210616/printwriter-append-method-not-appending in designing
 * this program.
 *
 * I spent ~1 hour writing this program.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BMIfinal
{
    /* This class takes user input for height and weight to calculate a BMI and evaluate it.
     * It currently only supports imperial units.
     */
    private double weight;  // lbs
    private double height;  // inches
    private double BMI;

    private final Scanner scanner = new Scanner(System.in);

    public BMIfinal()
    {
        this.weight = this.height = this.BMI = 0;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public double getBMI()
    {
        return BMI;
    }

    public void setBMI(double BMI)
    {
        this.BMI = BMI;
    }

    @Override
    public String toString()
    {
        return """
                Height: %.2fin
                Weight: %.2flbs
                Calculated BMI = %.2f
                Result: %s""".formatted(getHeight(), getWeight(), getBMI(), evalBMI(calcBMI()));
    }

    private double validateDouble(String message)
    {
        /* Checks if a user-entered number is greater than zero. Accounts for non-numerical values
         */
        double value = 0;
        boolean flag = true;

        System.out.print(message);
        do
        {
            try  // check for wrong scanner input type
            {
                value = scanner.nextDouble();
                if (value <= 0)  // prevents some illogical results
                    System.out.print("Value must be greater than zero: ");
                else
                    flag = false;

            }
            catch (InputMismatchException e)
            {
                System.out.print("Whoops! Please enter a valid number: ");
            }
            finally
            {
                scanner.nextLine();  // always clear buffer
            }
        } while (flag);
        return value;
    }

    private double validateDouble()  // validateDouble with a generic message
    {
        String message = "Enter your value: ";
        return validateDouble(message);
    }

    private double calcBMI()
    {
        return (703 * getWeight() / Math.pow(getHeight(), 2));  // BMI for imperial units
    }

    private String evalBMI(double BMI)
    {
        /* Uses basic BMI statistical categories to determine what the input values fall under.
         * Categories from: https://www.thecalculatorsite.com/articles/health/bmi-formula-for-bmi-calculations.php
         */

        String result;
        if (BMI < 15)
            result = "Severely Underweight";
        else if (BMI < 18.5)
            result = "Underweight";
        else if (BMI < 25)
            result = "Normal";
        else if (BMI < 30)
            result = "Overweight";
        else if (BMI < 40)
            result = "Obese";
        else
            result = "Severely Obese";

        return result;
    }

    private void getInput()
    {
        setHeight(validateDouble("\nEnter your height in inches: "));
        setWeight(validateDouble("Enter your weight in pounds: "));
        setBMI(calcBMI());
        System.out.println("\n" + this + "\n");
    }

    private void printSession(PrintWriter pw) {
        pw.println("<NEW SESSION>");
        pw.print(this);
        pw.println("\n");
    }

    private void writeToFile(){
        File file = new File("myBMI.txt");
        if (file.exists() && !file.isDirectory())
        {
            try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true)))
            {
                printSession(printWriter);
                System.out.println("\nResults saved to myBMI.txt...\n");
            }
            catch (FileNotFoundException e) {
                System.out.println("\nERROR: File not found.\n");  // shouldn't trigger
            }
        }
        else
        {
            try (PrintWriter printWriter = new PrintWriter("myBMI.txt"))
            {
                printSession(printWriter);
                System.out.println("\nResults saved to myBMI.txt...\n");
            }
            catch (FileNotFoundException e)
            {
                System.out.println("\nERROR: File not found.\n");  // shouldn't trigger
            }
        }
    }

    private boolean testStatement(String message) {
        boolean flag = true, testEval = false;
        String userInput;
        while (flag)
        {
            System.out.print(message + " [Y/N] ");
            userInput = scanner.nextLine().toLowerCase();
            if (userInput.startsWith("n"))
            {
                flag = false;
            }
            else if (userInput.startsWith("y"))
            {
                testEval = true;
                flag = false;
            }
            else
            {
                System.out.println("Please enter 'yes' or 'no'.\n");
            }
        }
        return testEval;
    }

    public void run()
    {
        boolean runAgain = true;  // main loop
        System.out.println("Welcome to the BMI Calculator!");

        while (runAgain)
        {
            getInput();

            if (testStatement("Save results?"))
            {
                writeToFile();
            }

            if (!testStatement("Calculate again?"))
            {
                runAgain = false;
            }
        }
        System.out.println("\nExiting...");
    }
}
