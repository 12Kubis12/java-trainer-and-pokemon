package ParentPackage.utility;

import ParentPackage.db.FromTable;
import ParentPackage.db.TableType;

import java.util.List;

public class PrintUtils {

    public static void printDivider() {
        System.out.println("-".repeat(177));
    }

    public static void printInvalidChoice() {
        System.out.println("Invalid choice!");
    }

    public static void printChooseOptions() {
        System.out.println("Choose from the following options:\n");
    }

    public static void printChooseFromOptions(List<FromTable> allObjects, TableType tableType) {
        System.out.println("0. Cancel.");
        for (int i = 0; i < allObjects.size(); i++) {
            System.out.println((i + 1) + ". " + allObjects.get(i));
        }
        System.out.println("Enter corresponding index of " + tableType + ":");
    }
}
