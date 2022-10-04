package account.entity;

import account.Operation;
import account.RoleEnum;
import account.validator.PasswordChecker;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastname;
    @NotEmpty
    @Pattern(regexp = ".+@acme.com$")
    @Column(unique = true)
    private String email;
    @NotEmpty
    @PasswordChecker
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    private Integer isLock = 0;

    private Integer countLoginError = 0;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
    }

    public User(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.toLowerCase();
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public boolean isAdmin() {
        for (Role role : roles
        ) {
            if (role.getName().equals(RoleEnum.ROLE_ADMINISTRATOR.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNonLock() {
        return this.isLock != 1;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public void addLoginError() {
        countLoginError++;
    }

    public Integer getCountLoginError() {
        return countLoginError;
    }

    public void resetCountLoginError() {
        this.countLoginError = 0;
    }

    public boolean hasNotRole(String newRole) {
        for (Role role : roles
        ) {
            if (role.getName().equals(newRole)) {
                return true;
            }
        }
        return false;
    }
}