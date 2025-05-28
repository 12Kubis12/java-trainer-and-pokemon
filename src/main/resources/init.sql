CREATE database trainer_and_pokemon;
USE trainer_and_pokemon;

CREATE TABLE trainer(
	trainer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(35) UNIQUE NOT NULL
    );

CREATE TABLE pokemon(
	pokemon_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(35) NOT NULL,
    trainer_id INT,
    FOREIGN KEY (trainer_id) REFERENCES trainer(trainer_id)
    );

INSERT INTO trainer
	(full_name)
VALUES
	('Ash Ketchum'),
	('Brock'),
	('Misty'),
	('Jessie'),
	('James');

INSERT INTO pokemon
	(name, trainer_id)
VALUES
	('Pikachu', 1),
	('Pidgeot', 1),
	('Bulbasaur', 1),
	('Squirtle', 1),
	('Charmander', 1),
	('Onix', 2),
	('Vulpix', 2),
	('Blissey', 2),
	('Staryu', 3),
	('Psyduck', 3),
	('Politoed', 3),
	('Azurill', 3),
	('Charmander', 4),
	('Politoed', 4),
	('Lickitung', 4),
	('Weezing', 4),
	('Squirtle', 5),
	('Victreebel', 5),
	('Weezing', 5),
	('Carnivine', 5);

INSERT INTO pokemon
	(name)
VALUES
	('Diglett'),
	('Ponyta'),
	('Bulbasaur'),
	('Squirtle'),
	('Charmander'),
	('Ponyta'),
	('Venonat'),
	('Blissey'),
	('Staryu'),
	('Vulpix'),
	('Drowzee'),
	('Azurill'),
	('Bellossom'),
	('Ekans'),
	('Voltorb'),
	('Weezing'),
	('Ekans'),
	('Voltorb'),
	('Metapod'),
	('Cyndaquil');