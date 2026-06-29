package com.vuk.spring_webapp.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represent's a general, abstract, user of the app.
 *
 * <p>A user can be an employee or a candidate. Both login with their email and password, but they're allowed to
 * perform different actions and different data may be stored for both.</p>
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.user.Employee
 * @see com.vuk.spring_webapp.domain.user.Candidate
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class AppUser {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the type of user.
     *
     * @see com.vuk.spring_webapp.domain.user.Role
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * User's first name.
     */
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    /**
     * User's sex.
     *
     * @see com.vuk.spring_webapp.domain.user.Sex
     */
    @Enumerated(EnumType.STRING)
    private Sex sex;

    /**
     * User's phone number.
     */
    @Column(length = 16, nullable = false)
    private String phone;

    /**
     * User's address.
     */
    @Column(length = 50, nullable = false)
    private String address;

    /**
     * User's email address.
     */
    @Column(length = 50, nullable = false, unique = true)
    private String email;

    /**
     * User's password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Initializes a new user with required fields.
     *
     * @param role      type of user
     * @param firstName first name, maximum 30 characters
     * @param lastName  last name, maximum 30 characters
     * @param sex       sex
     * @param phone     phone number, maximum 16 characters
     * @param address   address, maximum 50 characters
     * @param email     unique email address, maximum 50 characters
     * @param password  password
     * @see com.vuk.spring_webapp.domain.user.Role
     * @see com.vuk.spring_webapp.domain.user.Sex
     */
    protected AppUser(Role role, String firstName, String lastName, Sex sex, String phone, String address, String email, String password) {
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
