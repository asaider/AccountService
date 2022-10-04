package account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
public class UserBlackList {
    @Id
    @NotEmpty
    @Pattern(regexp = ".+@acme.com$")
    @Column(unique = true)
    private String email;

    public UserBlackList()
    {

    }
    public UserBlackList(String email) {
        this.email = email;
    }
}
