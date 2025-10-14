package com.vuk.spring_webapp.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(length = 16, nullable = false)
    private String phone;

    @Column(length = 50, nullable = false)
    private String address;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    AppUser(Role role, String firstName, String lastName, Sex sex, String phone, String address, String email, String password) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
    }
}
