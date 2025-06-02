package ParentPackage.service;

import ParentPackage.db.*;
import ParentPackage.utility.InputUtils;
import ParentPackage.utility.PrintUtils;

import java.util.*;

public class CRUDManager {
    private final DBTrainerAndPokemonService trainerAndPokemonService;

    public CRUDManager() {
        this.trainerAndPokemonService = new DBTrainerAndPokemonService();
    }

    public void printOptions() {
        PrintUtils.printDivider();
        System.out.println("Hello, welcome to trainer and pokemon manager.");
        PrintUtils.printChooseOptions();
        while (true) {
            System.out.println("0. Exit.");
            System.out.println("1. Work with the list of trainers.");
            System.out.println("2. Work with the list of Pokémon.");
            System.out.println("3. List the Pokémon that belong to a given trainer.");
            System.out.println("4. List trainers sorted by number of owned Pokémon.");
            System.out.println("5. List Pokémon that are not caught (do not belong to anyone).");
            System.out.println("6. Catch a Pokémon (" +
                    "you choose the trainer who catches, then a Pokémon that doesn't belong to anyone).");
            PrintUtils.printDivider();

            final int choice = InputUtils.readInt();
            switch (choice) {
                case 1 -> this.printTableOptions(TableType.TRAINER);
                case 2 -> this.printTableOptions(TableType.POKEMON);
                case 3 -> this.printPokemonByTrainerId();
                case 4 -> this.printTrainerByNUmberOfPokemon();
                case 5 -> this.printNotCaughtPokemon();
                case 6 -> this.catchPokemon();
                case 0 -> {
                    System.out.println("Good Bye!");
                    return;
                }
                default -> PrintUtils.printInvalidChoice();
            }
        }

    }

    public void printTableOptions(TableType tableType) {
        PrintUtils.printChooseOptions();
        while (true) {
            System.out.println("0. Go back.");
            System.out.println("1. Get every " + tableType + ".");
            System.out.println("2. Edit " + tableType + ".");
            System.out.println("3. Add " + tableType + ".");
            System.out.println("4. Delete " + tableType + ".");
            System.out.println("5. Search " + tableType + " by name.");
            PrintUtils.printDivider();

            final int choice = InputUtils.readInt();

            switch (choice) {
                case 1 -> this.printAll(tableType);
                case 2 -> this.edit(tableType);
                case 3 -> this.create(tableType);
                case 4 -> this.delete(tableType);
                case 5 -> this.searchByName(tableType);
                case 0 -> {
                    PrintUtils.printChooseOptions();
                    return;
                }
                default -> PrintUtils.printInvalidChoice();
            }
        }
    }

    private void printAll(TableType tableType) {
        List<FromTable> entities = this.trainerAndPokemonService.readAll(tableType);
        entities.forEach(System.out::println);
        PrintUtils.printDivider();
    }

    private void printPokemonByTrainerId() {
        int trainerId = this.giveOptions();
        List<FromTable> entities;
        if (trainerId == 0) {
            entities = this.trainerAndPokemonService.readNotCaughtPokemon();
        } else {
            entities = this.trainerAndPokemonService.searchPokemonByTrainerId(trainerId);
        }
        entities.forEach(System.out::println);
        PrintUtils.printDivider();
    }

    private void printTrainerByNUmberOfPokemon() {
        final Map<FromTable, Integer> trainerByPokemon = this.trainerAndPokemonService.readTrainerByNUmberOfPokemon();
        trainerByPokemon.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(System.out::println);
        PrintUtils.printDivider();
    }

    private void printNotCaughtPokemon() {
        final List<FromTable> entities = this.trainerAndPokemonService.readNotCaughtPokemon();
        entities.forEach(System.out::println);
        PrintUtils.printDivider();
    }

    private void create(TableType tableType) {
        switch (tableType) {
            case TRAINER -> {
                System.out.println("Enter full name:");
                final String name = InputUtils.readString();
                final int result = this.trainerAndPokemonService.createTrainer(name);
                if (result > 0) {
                    System.out.println("Trainer created successfully.");
                }
            }
            case POKEMON -> {
                System.out.println("Enter name:");
                final String name = InputUtils.readString();
                final int trainerId = this.giveOptions();
                final int result = this.trainerAndPokemonService.createPokemon(name, trainerId);
                if (result > 0) {
                    System.out.println("Pokémon created successfully.");
                }
            }

        }
    }

    private void delete(TableType tableType) {
        final List<FromTable> allObjects = this.trainerAndPokemonService.readAll(tableType);

        int choice = this.chooseFromOptions(allObjects, tableType);

        if (choice != 0) {
            if (this.trainerAndPokemonService.delete(tableType, allObjects.get(choice - 1).getId()) > 0) {
                System.out.println(tableType + " deleted successfully.");
            }
        }
    }

    private void edit(TableType tableType) {
        final List<FromTable> allObjects = this.trainerAndPokemonService.readAll(tableType);

        int choice = this.chooseFromOptions(allObjects, tableType);

        if (choice != 0) {
            final Optional<FromTable> entityToEdit = this.editFromInput(tableType, allObjects.get(choice - 1));
            if (entityToEdit.isPresent()) {
                switch (tableType) {
                    case TRAINER -> {
                        if (this.trainerAndPokemonService.editTrainer((Trainer) entityToEdit.get()) > 0) {
                            System.out.println("Trainer edited successfully.");
                        }
                    }
                    case POKEMON -> {
                        int resultOption = this.trainerAndPokemonService.editPokemon((Pokemon) entityToEdit.get());
                        if (resultOption > 0) {
                            System.out.println("Pokémon edited successfully.");
                        }
                    }
                }
            }
        }
    }

    private Optional<FromTable> editFromInput(TableType tableType, FromTable fromTable) {
        String name = fromTable.getName();
        int trainerId = 0;
        if (tableType == TableType.POKEMON) {
            trainerId = ((Pokemon) fromTable).getTrainerId();
        }

        while (true) {
            System.out.println("0. Back");
            System.out.println("1. -> Edit name (" + name + ")");
            if (tableType == TableType.POKEMON) {
                System.out.println("2. -> Edit trainer_id (" + trainerId + ")");
            }
            System.out.println("Write corresponding index of option (from options above):");
            final int choice = InputUtils.readInt();
            switch (choice) {
                case 0 -> {
                    return Optional.empty();
                }
                case 1 -> {
                    System.out.println("Enter new name:");
                    name = InputUtils.readString();
                }
                case 2 -> {
                    if (tableType == TableType.POKEMON) {
                        trainerId = this.giveOptions();
                    } else {
                        PrintUtils.printInvalidChoice();
                        continue;
                    }
                }
                default -> {
                    PrintUtils.printInvalidChoice();
                    continue;
                }
            }

            switch (tableType) {
                case TRAINER -> {
                    return Optional.of(new Trainer(fromTable.getId(), name));
                }
                case POKEMON -> {
                    return Optional.of(new Pokemon(fromTable.getId(), name, trainerId));
                }
            }
        }
    }

    private void searchByName(TableType tableType) {
        System.out.println("Enter name:");
        final String name = InputUtils.readString();
        final List<FromTable> allObjects = this.trainerAndPokemonService.searchByName(tableType, name);
        if (allObjects.isEmpty()) {
            System.out.println("No " + tableType + " found!");
            return;
        }
        System.out.println("Found " + tableType + ":");
        allObjects.forEach(System.out::println);
    }

    private void catchPokemon() {
        System.out.println("Choose trainer who wants to caught Pokémon:\n");
        final List<FromTable> trainers = this.trainerAndPokemonService.readAll(TableType.TRAINER);
        int firstChoice = this.chooseFromOptions(trainers, TableType.TRAINER);

        if (firstChoice != 0) {
            int trainerId = trainers.get(firstChoice - 1).getId();
            System.out.println("Choose Pokémon to catch:\n");
            final List<FromTable> pokemon = this.trainerAndPokemonService.readNotCaughtPokemon();
            int secondChoice = this.chooseFromOptions(pokemon, TableType.POKEMON);

            if (secondChoice != 0) {
                Pokemon chosenPokemon = (Pokemon) pokemon.get(secondChoice - 1);
                chosenPokemon.setTrainer_id(trainerId);
                int resultOption = this.trainerAndPokemonService.editPokemon(chosenPokemon);
                if (resultOption > 0) {
                    System.out.println("Pokémon edited successfully.");
                }
            }
        }
    }

    private int chooseFromOptions(List<FromTable> allObjects, TableType tableType) {
        int choice;
        while (true) {
            PrintUtils.printChooseFromOptions(allObjects, tableType);

            choice = InputUtils.readInt();

            if (choice >= 0 && choice <= allObjects.size()) {
                break;
            }
            PrintUtils.printInvalidChoice();
        }
        return choice;
    }

    private int giveOptions() {
        System.out.println("""
                Enter id of trainer who owns the Pokémon:
                (Write corresponding 'id', 0 means that the Pokémon is not caught)
                """);
        List<FromTable> entities = this.trainerAndPokemonService.readAll(TableType.TRAINER);
        entities.forEach(System.out::println);
        List<Integer> entitiesId = this.trainerAndPokemonService.readAll(TableType.TRAINER).stream()
                .map(entity -> (Trainer) entity)
                .map(FromTable::getId)
                .toList();
        int trainerId = InputUtils.readInt();
        while (!entitiesId.contains(trainerId) && trainerId != 0) {
            PrintUtils.printInvalidChoice();
            trainerId = InputUtils.readInt();
        }
        return trainerId;
    }
}