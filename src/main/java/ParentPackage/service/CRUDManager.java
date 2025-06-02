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

        while (true) {
            PrintUtils.printDivider();
            PrintUtils.printChooseOptions();
            System.out.println("0 -> Exit.");
            System.out.println("1 -> Work with the list of trainers.");
            System.out.println("2 -> Work with the list of Pokémon.");
            System.out.println("3 -> List the Pokémon that belong to a given trainer.");
            System.out.println("4 -> List the trainers sorted by number of owned Pokémon.");
            System.out.println("5 -> List the Pokémon that are not caught (do not belong to anyone).");
            System.out.println("6 -> Catch a Pokémon (" +
                    "you choose a trainer who catches and then a Pokémon that doesn't belong to anyone).");
            PrintUtils.printDivider();

            final int choice = InputUtils.readInt();
            switch (choice) {
                case 0 -> {
                    System.out.println("Good Bye!");
                    return;
                }
                case 1 -> this.printTableContent(TableType.TRAINER);
                case 2 -> this.printTableContent(TableType.POKEMON);
                case 3 -> this.printPokemonByTrainerId();
                case 4 -> this.printTrainerByNumberOfPokemon();
                case 5 -> this.printUncaughtPokemon();
                case 6 -> this.catchPokemon();
                default -> PrintUtils.printInvalidChoice();
            }
        }
    }

    private void printTableContent(TableType tableType) {
        while (true) {
            PrintUtils.printDivider();
            PrintUtils.printChooseOptions();
            System.out.println("0 -> Go back.");
            System.out.println("1 -> Get every " + tableType + ".");
            System.out.println("2 -> Edit a " + tableType + ".");
            System.out.println("3 -> Add a " + tableType + ".");
            System.out.println("4 -> Delete a " + tableType + ".");
            System.out.println("5 -> Search a " + tableType + " by name.");
            PrintUtils.printDivider();

            final int choice = InputUtils.readInt();
            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> this.printAll(tableType);
                case 2 -> this.edit(tableType);
                case 3 -> this.create(tableType);
                case 4 -> this.delete(tableType);
                case 5 -> this.searchByName(tableType);
                default -> PrintUtils.printInvalidChoice();
            }
        }
    }

    private void printAll(TableType tableType) {
        final List<FromTable> entities = this.trainerAndPokemonService.readAll(tableType);
        entities.forEach(System.out::println);
    }

    private void printPokemonByTrainerId() {
        final List<FromTable> trainers = this.trainerAndPokemonService.readAll(TableType.TRAINER);
        final int choice = this.chooseFromOptions(trainers, false);
        List<FromTable> entities;
        if (choice == 0) {
            entities = this.trainerAndPokemonService.readUncaughtPokemon();
        } else {
            final int trainerId = trainers.get(choice - 1).getId();
            entities = this.trainerAndPokemonService.searchPokemonByTrainerId(trainerId);
        }
        entities.forEach(System.out::println);
    }

    private void printTrainerByNumberOfPokemon() {
        final Map<FromTable, Integer> trainerByPokemon = this.trainerAndPokemonService.readTrainerByNumberOfPokemon();
        trainerByPokemon.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(System.out::println);
    }

    private void printUncaughtPokemon() {
        final List<FromTable> entities = this.trainerAndPokemonService.readUncaughtPokemon();
        entities.forEach(System.out::println);
    }

    private void create(TableType tableType) {
        switch (tableType) {
            case TRAINER -> {
                System.out.println("Enter full name:");
                final String name = InputUtils.readString();
                if (this.trainerAndPokemonService.createTrainer(name) > 0) {
                    System.out.println("Trainer created successfully.");
                }
            }
            case POKEMON -> {
                System.out.println("Enter name:");
                final String name = InputUtils.readString();
                final List<FromTable> trainers = this.trainerAndPokemonService.readAll(TableType.TRAINER);
                final int choice = this.chooseFromOptions(trainers, false);
                int result;
                if (choice == 0) {
                    result = this.trainerAndPokemonService.createPokemon(name, 0);
                } else {
                    final int trainerId = trainers.get(choice - 1).getId();
                    result = this.trainerAndPokemonService.createPokemon(name, trainerId);
                }
                if (result > 0) {
                    System.out.println("Pokémon created successfully.");
                }
            }
        }
    }

    private void delete(TableType tableType) {
        final List<FromTable> entities = this.trainerAndPokemonService.readAll(tableType);

        final int choice = this.chooseFromOptions(entities, true);

        if (choice != 0) {
            if (this.trainerAndPokemonService.delete(tableType, entities.get(choice - 1).getId()) > 0) {
                System.out.println(tableType + " deleted successfully.");
            }
        }
    }

    private void edit(TableType tableType) {
        final List<FromTable> entities = this.trainerAndPokemonService.readAll(tableType);

        int choice = this.chooseFromOptions(entities, true);

        if (choice != 0) {
            final Optional<FromTable> entityToEdit = this.editFromInput(tableType, entities.get(choice - 1));
            if (entityToEdit.isPresent()) {
                switch (tableType) {
                    case TRAINER -> {
                        if (this.trainerAndPokemonService.editTrainer((Trainer) entityToEdit.get()) > 0) {
                            System.out.println("Trainer edited successfully.");
                        }
                    }
                    case POKEMON -> {
                        if (this.trainerAndPokemonService.editPokemon((Pokemon) entityToEdit.get()) > 0) {
                            System.out.println("Pokémon edited successfully.");
                        }
                    }
                }
            }
        }
    }

    private Optional<FromTable> editFromInput(TableType tableType, FromTable entityToEdit) {
        String name = entityToEdit.getName();
        int trainerId = 0;
        if (tableType == TableType.POKEMON) {
            trainerId = ((Pokemon) entityToEdit).getTrainerId();
        }

        while (true) {
            PrintUtils.printChooseOptions();
            System.out.println("0 -> Back");
            System.out.println("1 -> Edit name (" + name + ")");
            if (tableType == TableType.POKEMON) {
                System.out.println("2 -> Edit trainer_id (" + trainerId + ")");
            }
            final int firstChoice = InputUtils.readInt();
            switch (firstChoice) {
                case 0 -> {
                    return Optional.empty();
                }
                case 1 -> {
                    System.out.println("Enter new name:");
                    name = InputUtils.readString();
                }
                case 2 -> {
                    if (tableType == TableType.POKEMON) {
                        final List<FromTable> trainers = this.trainerAndPokemonService.readAll(TableType.TRAINER);
                        final int secondChoice = this.chooseFromOptions(trainers, false);
                        if (secondChoice == 0) {
                            trainerId = 0;
                        } else {
                            trainerId = trainers.get(secondChoice - 1).getId();
                        }
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
                    return Optional.of(new Trainer(entityToEdit.getId(), name));
                }
                case POKEMON -> {
                    return Optional.of(new Pokemon(entityToEdit.getId(), name, trainerId));
                }
            }
        }
    }

    private void searchByName(TableType tableType) {
        System.out.println("Enter name:");
        final String name = InputUtils.readString();
        final List<FromTable> entities = this.trainerAndPokemonService.searchByName(tableType, name);
        if (entities.isEmpty()) {
            System.out.println("No " + tableType + " found!");
            return;
        }
        System.out.println("Found " + tableType + ":");
        entities.forEach(System.out::println);
    }

    private void catchPokemon() {
        System.out.println("Choose a trainer who wants to caught a Pokémon:\n");
        final List<FromTable> trainers = this.trainerAndPokemonService.readAll(TableType.TRAINER);
        final int firstChoice = this.chooseFromOptions(trainers, true);

        if (firstChoice != 0) {
            final int trainerId = trainers.get(firstChoice - 1).getId();
            System.out.println("Choose a Pokémon to catch:\n");
            final List<FromTable> pokemon = this.trainerAndPokemonService.readUncaughtPokemon();
            final int secondChoice = this.chooseFromOptions(pokemon, true);

            if (secondChoice != 0) {
                final Pokemon chosenPokemon = (Pokemon) pokemon.get(secondChoice - 1);
                chosenPokemon.setTrainer_id(trainerId);
                if (this.trainerAndPokemonService.editPokemon(chosenPokemon) > 0) {
                    System.out.println("Pokémon edited successfully.");
                }
            }
        }
    }

    private int chooseFromOptions(List<FromTable> entities, boolean cancellationPossible) {
        while (true) {
            PrintUtils.printChooseFromOptions(entities, cancellationPossible);

            final int choice = InputUtils.readInt();
            if (choice >= 0 && choice <= entities.size()) {
                return choice;
            }
            PrintUtils.printInvalidChoice();
        }
    }
}