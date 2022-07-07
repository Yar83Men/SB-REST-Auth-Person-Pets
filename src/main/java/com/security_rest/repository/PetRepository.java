package com.security_rest.repository;

import com.security_rest.entity.Person;
import com.security_rest.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

}
