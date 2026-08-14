package com.vuk.spring_webapp.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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
     * Must not be null.
     *
     * @see com.vuk.spring_webapp.domain.user.Role
     */
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * User's first name.
     * Must not be blank and must not exceed 30 characters.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name must be at most 30 characters")
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    /**
     * User's last name.
     * Must not be blank and must not exceed 30 characters.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name must be at most 30 characters")
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    /**
     * User's sex.
     * Must not be null.
     *
     * @see com.vuk.spring_webapp.domain.user.Sex
     */
    @NotNull(message = "Sex is required")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    /**
     * User's phone number.
     * Must not be blank and must be between 8 and 16 digits.
     */
    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 16, message = "Phone number must be between 8 and 16 characters")
    @Pattern(
            regexp = "^[0-9]+$",
            message = "Phone number must contain only digits"
    )
    @Column(length = 16, nullable = false)
    private String phone;

    /**
     * User's address.
     * Must not be blank and must not exceed 50 characters.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 50, message = "Address must be at most 50 characters")
    @Column(length = 50, nullable = false)
    private String address;

    /**
     * User's email address.
     * Must be formatted correctly.
     *
     * @implNote Validation based on Jakarta Bean Validation providers.
     */
    @Email(message = "Email must be well formatted")
    @Size(max = 50, message = "Email must be at most 50 characters")
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

    @Override
    public String toString() {
        return "id=" + id +
                ", role=" + role +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'';
    }

    /**
     * Checks equality between two AppUser instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both app users have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(id, appUser.id);
    }

    /**
     * Calculates a hash code based on the app user's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
