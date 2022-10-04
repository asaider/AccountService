package account.authorizationСonfiguration;

import account.entity.User;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String emailLower = email.toLowerCase();
        Optional<User> userOptional = users.findByEmailIgnoreCase(emailLower);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(emailLower);
            return new UserDetailsImpl(user);
        }

        throw new UsernameNotFoundException("Not found: " + email);
    }
}