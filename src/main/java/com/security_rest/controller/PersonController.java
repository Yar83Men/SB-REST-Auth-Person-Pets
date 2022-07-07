package com.security_rest.controller;

import com.security_rest.dao.PetDAO;
import com.security_rest.entity.Person;
import com.security_rest.entity.Pet;
import com.security_rest.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/user")
public class PersonController {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest servletRequest;

    private final PetDAO petDAO;


    @Autowired
    public PersonController(PersonRepository personRepository,
                            PasswordEncoder passwordEncoder,
                            HttpServletRequest servletRequest,
                            PetDAO petDAO) {

        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.servletRequest = servletRequest;
        this.petDAO = petDAO;
    }


    // Добавление пользователя /user/add
    // RequestBody POST json {"username": "alex", "password": "qwerty"}
    @PostMapping("/add")
    public ResponseEntity<?> savePerson(@RequestBody Person person) {
        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            return new ResponseEntity<>("User is already present", HttpStatus.BAD_REQUEST);
        }
        Person newPerson = new Person();
        newPerson.setUsername(person.getUsername());
        newPerson.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(newPerson);

        authenticateUserAfterCreations(servletRequest, person.getUsername(), person.getPassword());

        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    // Разлогирование пользователя /user/logout
    // POST
    @PostMapping("/logout")
    public String logoutUser() {
        try {
            servletRequest.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return "logout success";
    }

    // Аутентификация /user/login
    // POST Base Auth login и password
    @PostMapping("/login")
    public String homePage() {
        if (!isPersonAuthenticated(servletRequest)) {
            return "No user logged";
        }
        return "User name = " + servletRequest.getUserPrincipal().getName();
    }

    // Получение списка животных аутентифицированного пользователя
    // GET /user/pets
    @GetMapping("/pets")
    public List<Pet> getAllPersonPets() {
        return petDAO.getPetsByOwnerId(getPersonAuthId());
    }

    // Добавление животного пользователю
    // POST /user/add/pet  json {
    //        "name": "Sharik",
    //        "kind_of_pet": "DOG",
    //        "date_of_birth": 2015,
    //        "sex": "male"
    //    }
    @PostMapping("/add/pet")
    public ResponseEntity<?> addPetToPerson(@RequestBody Pet pet) {
        petDAO.createPet(pet, getPersonAuthId());
        return new ResponseEntity<>("Pet " + pet.getName() + " added", HttpStatus.CREATED);
    }

    // Получение данных о животном
    // GET /user/pet/id
    @GetMapping("/pet/{id}")
    public ResponseEntity<?> getPetById(@PathVariable int id) {
        Pet pet = petDAO.getPetById(id);
        return new ResponseEntity<>(pet, HttpStatus.FOUND);
    }


    // Обновленеи данных о животном по id
    // PUT /user/pet/id
    // json {
    //        "name": "Sharik",
    //        "kind_of_pet": "DOG",
    //        "date_of_birth": 2015,
    //        "sex": "male",
    //        "fk_peron_id":2
    //    }
    @PutMapping("/pet/{id}")
    public ResponseEntity<?> updatePet(@PathVariable int id, @RequestBody Pet pet) {
        petDAO.update(pet, id);
        return new ResponseEntity<>(pet, HttpStatus.FOUND);
    }


    // Удаление животного по id
    // DELETE /user/pet/delete/id
    @DeleteMapping("/pet/delete/{id}")
    public void deletePet(@PathVariable int id) {
        petDAO.deletePet(id);
    }

    // Аутефикация пользователя после регистрации
    private void authenticateUserAfterCreations(HttpServletRequest http,
                                                String username, String password) {
        try {
            http.login(username, password);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private boolean isPersonAuthenticated(HttpServletRequest http) {
        Principal principal = http.getUserPrincipal();
        return principal != null;
    }


    // Получение id пользователя
    private int getPersonAuthId() {
        return personRepository
                .findByUsername(servletRequest.getUserPrincipal().getName()).get()
                .getPerson_id();
    }
}
