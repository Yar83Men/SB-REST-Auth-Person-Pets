package com.security_rest.dao;

import com.security_rest.entity.Pet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetDAO {
    private final JdbcTemplate jdbcTemplate;

    public PetDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    RowMapper<Pet> rowMapper = (resultSet, row) -> {
        Pet pet = new Pet();
        pet.setPet_id(resultSet.getInt("pet_id"));
        pet.setName(resultSet.getString("name"));
        pet.setKind_of_pet(resultSet.getString("kind_of_pet"));
        pet.setDate_of_birth(resultSet.getInt("date_of_birth"));
        pet.setSex(resultSet.getString("sex"));
        pet.setFk_person_id(resultSet.getInt("fk_person_id"));
        return pet;
    };

    // Метод создает в БД запись о животном Pet, int fk - id владельца Person
    public void createPet(Pet pet, int fk) {
        String sql = "INSERT INTO pets (name, kind_of_pet, date_of_birth, sex, fk_person_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, pet.getName(), pet.getKind_of_pet(), pet.getDate_of_birth(), pet.getSex(), fk);
    }


    // Получение сущности Pet по его id
    public Pet getPetById(int id) {
        String sql = "SELECT * FROM pets WHERE pet_id = ?";
        Pet pet;
        try {
            pet = jdbcTemplate.query(sql, new Object[]{id}, rowMapper).get(0);
        } catch (IndexOutOfBoundsException e) {
            pet = new Pet();
            pet.setName("No such Pet");
            return pet;
        }
        return pet;
    }

    // Обновление записи сущности Pet по id
    public void update(Pet pet, int id) {
        String sql = "UPDATE pets SET name = ?, kind_of_pet = ?, date_of_birth = ?, sex = ?, fk_person_id = ?" +
                "WHERE pet_id = ?";
        jdbcTemplate.update(sql,
                pet.getName(),
                pet.getKind_of_pet(),
                pet.getDate_of_birth(),
                pet.getSex(),
                pet.getFk_person_id(),
                id);

    }


    // Удаление записи Pet по id
    public void deletePet(int id) {
        String sql = "DELETE FROM pets WHERE pet_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Получение списка сущностей Pet (животных) по id владельца Person
    public List<Pet> getPetsByOwnerId(int id) {
        String sql = "SELECT * FROM pets WHERE fk_person_id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rowMapper);
    }
}
