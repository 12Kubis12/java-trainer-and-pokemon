package ParentPackage.db;

import org.slf4j.Logger;

import java.sql.*;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class DBTrainerAndPokemonService {
    private static final String READ_ALL = "SELECT * FROM %s ORDER BY id;";

    private static final String CREATE_TRAINER = "INSERT INTO trainer (name) VALUES (?)";
    private static final String CREATE_POKEMON = "INSERT INTO pokemon (name, trainer_id) VALUES (?, ?)";

    private static final String DELETE = "DELETE FROM %s WHERE id = ?";

    private static final String EDIT_TRAINER = "UPDATE trainer SET name = ? WHERE id = ?";
    private static final String EDIT_POKEMON = "UPDATE pokemon SET name = ?, trainer_id = ? WHERE id = ?";

    private static final String SEARCH_BY_NAME = "SELECT * FROM %s WHERE name LIKE ?";

    private static final String SEARCH_POKEMON_BY_TRAINER_ID = "SELECT * FROM pokemon WHERE trainer_id = ?";

    private static final String READ_TRAINERS_ORDERED_BY_NUMBER_OF_POKEMON =
            """
                    SELECT t.*, COUNT(*) AS number_of_pokemon
                    FROM trainer t
                    JOIN pokemon p ON p.trainer_id = t.id
                    GROUP BY t.id
                    ORDER BY number_of_pokemon DESC;""";

    private static final String READ_UNCAUGHT_POKEMON = "SELECT * FROM pokemon WHERE trainer_id IS NULL";

    private static final Logger logger = getLogger(DBTrainerAndPokemonService.class);

    public List<FromTable> readAll(TableType tableType) {
        final String query = this.setQuery(READ_ALL, tableType);

        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            return this.createList(resultSet, tableType);
        } catch (SQLException e) {
            logger.error("Error while reading every {}!", tableType, e);
            return null;
        }
    }

    public int createTrainer(String name) {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TRAINER,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            return statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Trainer with this name already exists!");
            return 0;
        } catch (SQLException e) {
            logger.error("Error while creating trainer!", e);
            return 0;
        }
    }

    public int createPokemon(String name, int trainer_id) {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_POKEMON,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            if (trainer_id == 0) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, trainer_id);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while creating Pokémon!", e);
            return 0;
        }
    }

    public int delete(TableType tableType, int id) {
        final String query = this.setQuery(DELETE, tableType);

        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.error("The {} you want to delete is used somewhere. You cannot delete it!", tableType, e);
            return 0;
        } catch (SQLException e) {
            logger.error("Error while deleting {}!", tableType, e);
            return 0;
        }
    }

    public int editTrainer(Trainer trainer) {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(EDIT_TRAINER)) {

            statement.setString(1, trainer.getName());
            statement.setInt(2, trainer.getId());
            return statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Trainer with this name already exists!");
            return 0;
        } catch (SQLException e) {
            logger.error("Error while editing trainer!", e);
            return 0;
        }
    }

    public int editPokemon(Pokemon pokemon) {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(EDIT_POKEMON)) {

            statement.setString(1, pokemon.getName());
            if (pokemon.getTrainerId() == 0) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, pokemon.getTrainerId());
            }
            statement.setInt(3, pokemon.getId());
            return statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Trainer with this id does not exist!");
            return 0;
        } catch (SQLException e) {
            logger.error("Error while editing Pokémon!", e);
            return 0;
        }
    }

    public List<FromTable> searchByName(TableType tableType, String subString) {
        final String query = this.setQuery(SEARCH_BY_NAME, tableType);

        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + subString + "%");
            ResultSet resultSet = statement.executeQuery();
            return this.createList(resultSet, tableType);
        } catch (SQLException e) {
            logger.error("Error while searching {} by name!", tableType, e);
            return null;
        }
    }

    public List<FromTable> searchPokemonByTrainerId(int id) {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_POKEMON_BY_TRAINER_ID)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return this.createList(resultSet, TableType.POKEMON);
        } catch (SQLException e) {
            logger.error("Error while searching {} by trainer_id!", TableType.POKEMON, e);
            return null;
        }
    }

    public Map<FromTable, Integer> readTrainerByNumberOfPokemon() {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_TRAINERS_ORDERED_BY_NUMBER_OF_POKEMON)) {

            ResultSet resultSet = statement.executeQuery();
            Map<FromTable, Integer> trainerByNumberOfPokemon = new HashMap<>();
            while (resultSet.next()) {
                trainerByNumberOfPokemon.put(new Trainer(resultSet.getInt("id"),
                                resultSet.getString("name")),
                        resultSet.getInt("number_of_pokemon")
                );
            }
            return trainerByNumberOfPokemon;
        } catch (SQLException e) {
            logger.error("Error while reading {} ordered by number of Pokémon!", TableType.TRAINER, e);
            return null;
        }
    }

    public List<FromTable> readUncaughtPokemon() {
        try (Connection connection = HikariCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_UNCAUGHT_POKEMON)) {

            ResultSet resultSet = statement.executeQuery();
            return this.createList(resultSet, TableType.POKEMON);
        } catch (SQLException e) {
            logger.error("Error while reading {} Pokémon, which are not caught!", TableType.POKEMON, e);
            return null;
        }
    }

    private List<FromTable> createList(ResultSet resultSet, TableType tableType) throws SQLException {
        List<FromTable> allObjects = new ArrayList<>();
        while (resultSet.next()) {
            switch (tableType) {
                case TRAINER -> allObjects.add(new Trainer(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                ));
                case POKEMON -> allObjects.add(new Pokemon(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("trainer_id")
                ));
            }
        }
        return allObjects;
    }

    private String setQuery(String query, TableType tableType) {
        return (String.format(query, tableType.toString().toLowerCase()));
    }
}
