package ParentPackage.utility;

import ParentPackage.db.FromTable;

import java.util.List;

public class PrintUtils {

    public static void printDivider() {
        System.out.println("-".repeat(177));
    }

    public static void printInvalidChoice() {
        System.out.println("Invalid choice!");
    }

    public static void printChooseOptions() {
        System.out.println("Choose from the following options:\n" +
                "(Enter corresponding number for the option)");
        System.out.println();
    }

    public static void printChooseFromOptions(List<FromTable> entities, boolean cancellationPossible) {
        printChooseOptions();
        if (cancellationPossible) {
            System.out.println("0 -> Cancel.");
        } else {
            System.out.println("0 -> Uncaught Pokémon.");
        }

        for (int i = 0; i < entities.size(); i++) {
            System.out.println((i + 1) + " -> " + entities.get(i));
        }
    }
}
