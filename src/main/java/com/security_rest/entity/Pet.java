package com.security_rest.entity;



import lombok.*;

import javax.persistence.*;
import java.util.Date;


//Сущность животное
@Entity
@Table(name = "pets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pet_id;

    private String name;

    private String kind_of_pet;

    private int date_of_birth;

    private String sex;

    // Foreign key
    private int fk_person_id;

}
