package account.authorizationСonfiguration;

import account.entity.Role;
import account.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    private final String email;
    private final String password;
    private final boolean isNonLock;

    private final Collection<Role> roles;

    public UserDetailsImpl(User user) {
        this.email = user.getEmail().toLowerCase();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.isNonLock = user.isNonLock();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNonLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}