package com.demo.fintrack.Domain.Entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private String name;

    @Column(unique = true , nullable = false)
    private String email;

}
