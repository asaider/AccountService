package account.response;

import account.entity.Role;
import account.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class UserInfoResponse {
    private final long id;
    private final String name;
    private final String lastname;
    private final String email;

    private final Collection<Role> roles;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getRoles() {
        ArrayList<String> response = new ArrayList<>();

        for (Role role : roles
        ) {
            response.add(role.getName());

        }
        Collections.sort(response);
        return response;

    }
}
