package account.service;

import account.EventsType;
import account.Operation;
import account.RoleEnum;
import account.entity.Events;
import account.entity.Role;
import account.entity.User;
import account.repository.EventsRepository;
import account.repository.RoleRepository;
import account.repository.UserBlackListRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Component
public class AdminService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    UserBlackListRepository userBlackListRepository;

    public void changeRoleForUser(RoleEnum roleEnum, Operation operation, User user, String adminName) {

        Collection<Role> currentRoles = user.getRoles();
        if (operation.equals(Operation.GRANT)) {

            Optional<Role> role = roleRepository.findByName(roleEnum.toString());

            if (role.isEmpty()) {
                Role newRole = new Role(roleEnum.toString());
                roleRepository.save(newRole);
                currentRoles.add(newRole);
            } else {
                currentRoles.add(role.get());
            }
            Events event = new Events(
                    LocalDateTime.now().toString(),
                    EventsType.GRANT_ROLE,
                    adminName,
                    "Grant role " + roleEnum.toString().replace("ROLE_", "") + " to " + user.getEmail() + "",
                    "/api/admin/user/role"
            );
            eventsRepository.save(event);
        } else if (operation.equals(Operation.REMOVE)) {
            currentRoles.removeIf(role -> role.getName().equals(roleEnum.toString()));
            Events event = new Events(
                    LocalDateTime.now().toString(),
                    EventsType.REMOVE_ROLE,
                    adminName,
                    "Remove role " + roleEnum.toString().replace("ROLE_", "") + " from " + user.getEmail() + "",
                    "/api/admin/user/role"
            );
            eventsRepository.save(event);
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user, String adminName) {
        Collection<Role> roles = user.getRoles();
        for (Role role : roles
        ) {
            if (role.getName().equals(RoleEnum.ROLE_ADMINISTRATOR.toString())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }
        }

        userRepository.deleteUserByEmail(user.getEmail());
        Events event = new Events(
                LocalDateTime.now().toString(),
                EventsType.DELETE_USER,
                adminName,
                user.getEmail(),
                "/api/admin/user"
        );
        eventsRepository.save(event);
    }

    public void changeAccess(Operation operation, User user, String adminName) {
        boolean isLock = operation.equals(Operation.LOCK);
        user.setIsLock(isLock ? 1 : 0);
        userRepository.save(user);
        Events event = new Events(
                LocalDateTime.now().toString(),
                isLock ? EventsType.LOCK_USER : EventsType.UNLOCK_USER,
                adminName,
                isLock ? "Lock" : "Unlock" + " user " + user.getEmail() + "",
                "/api/admin/user/access"
        );
        eventsRepository.save(event);

        if (!isLock) {
            userBlackListRepository.deleteById(user.getEmail());
        }
    }
}
