package account.handler;

import account.EventsType;
import account.authorizationСonfiguration.UserDetailsImpl;
import account.entity.Events;
import account.entity.User;
import account.entity.UserBlackList;
import account.repository.EventsRepository;
import account.repository.UserBlackListRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

public class CustomAuthenticationEventListener implements
        AuthenticationEventPublisher {

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserBlackListRepository userBlackListRepository;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        if (user.isPresent()) {
            user.get().resetCountLoginError();
            userRepository.save(user.get());
        }
        if (userBlackListRepository.findById(userDetails.getUsername()).isPresent()) {
            userBlackListRepository.deleteById(userDetails.getUsername());
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();
        String username = authentication.getPrincipal().toString();

        if (userBlackListRepository.findById(username).isPresent()) {
            return;
        }

        Optional<User> user = userRepository.findByEmailIgnoreCase(username);

        Events event = new Events(
                LocalDateTime.now().toString(),
                EventsType.LOGIN_FAILED,
                username,
                request.getRequestURI(),
                request.getRequestURI()
        );
        eventsRepository.save(event);

        ArrayList<Events> events = eventsRepository.findAllBySubjectOrderByDateDesc(username);
        int errorsCount = 0;
        if (user.isPresent()) {
            user.get().addLoginError();
            if (user.get().getCountLoginError() == 5 && !user.get().isAdmin()) {
                blockUser(username, request.getRequestURI());
                user.get().setIsLock(1);
            }
            userRepository.save(user.get());
        } else {
            for (Events log : events
            ) {
                if ((!log.getAction().equals(EventsType.LOGIN_FAILED))) {
                    break;
                } else {
                    {
                        errorsCount++;
                        if (errorsCount == 5) {
                            blockUser(username, request.getRequestURI());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void blockUser(String name, String url) {
        Events bruteEvent = new Events(
                LocalDateTime.now().toString(),
                EventsType.BRUTE_FORCE,
                name,
                url,
                url
        );
        eventsRepository.save(bruteEvent);

        Events LockEvent = new Events(
                LocalDateTime.now().toString(),
                EventsType.LOCK_USER,
                name,
                "Lock user " + name + "",
                url
        );
        eventsRepository.save(LockEvent);

        userBlackListRepository.save(new UserBlackList(name));
    }
}
