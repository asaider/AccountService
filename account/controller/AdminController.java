package account.controller;

import account.Operation;
import account.RoleEnum;
import account.entity.Events;
import account.entity.Role;
import account.entity.User;
import account.repository.EventsRepository;
import account.repository.UserRepository;
import account.request.admin.Access;
import account.request.admin.Action;
import account.response.DefaultResponse;
import account.response.UserInfoResponse;
import account.response.admin.DeleteUser;
import account.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    UserRepository users;

    @Autowired
    AdminService adminService;

    @Autowired
    EventsRepository eventsRepository;

    @PutMapping("/admin/user/role")
    public UserInfoResponse addRole(
            @Valid @RequestBody Action action,
            @AuthenticationPrincipal UserDetails details
    ) {

        Optional<User> user = users.findByEmailIgnoreCase(action.getUser());

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        Operation operation = Operation.valueOf(action.getOperation());
        validateRole(user.get(), action.getRole(), operation);

        adminService.changeRoleForUser(
                RoleEnum.valueOf(action.getRole()),
                operation,
                user.get(),
                details.getUsername()
        );

        return new UserInfoResponse(user.get());
    }

    @PutMapping("/admin/user/access")
    public DefaultResponse access(
            @Valid @RequestBody Access action,
            @AuthenticationPrincipal UserDetails details
    ) {

        Optional<User> user = users.findByEmailIgnoreCase(action.getUser());

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        Operation operation = Operation.valueOf(action.getOperation());
        validateAccess(user.get(), operation);

        adminService.changeAccess(
                operation,
                user.get(),
                details.getUsername()
        );

        String statusResponse = operation.equals(Operation.UNLOCK) ? "unlocked" : "locked";
        return new DefaultResponse("User " + user.get().getEmail() + " " + statusResponse + "!");
    }

    private void validateAccess(User user, Operation operation) {
        if (!(operation.equals(Operation.LOCK) || operation.equals(Operation.UNLOCK))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The operation prohibited");
        }
        if (user.isAdmin() && operation.equals(Operation.LOCK)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
    }

    private void validateRole(User user, String newRole, Operation operation) {
        if (newRole.equals(RoleEnum.ROLE_ADMINISTRATOR.toString()) && operation.equals(Operation.GRANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        if (RoleEnum.ROLE_ADMINISTRATOR.toString().equals(newRole) && operation.equals(Operation.REMOVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        Collection<Role> roles = user.getRoles();

        if (!(user.hasNotRole(newRole)) && operation.equals(Operation.REMOVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }

        if (roles.size() == 1 && operation.equals(Operation.REMOVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }

        if (user.isAdmin() && operation.equals(Operation.GRANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        boolean isExistRole = false;
        for (RoleEnum rule : RoleEnum.values()) {
            if (rule.name().equals(newRole)) {
                isExistRole = true;
                break;
            }
        }

        if (!isExistRole) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
    }

    @GetMapping("/admin/user/")
    public ArrayList<UserInfoResponse> getUsers() {

        List<User> allUsers = users.findAllByOrderById();

        ArrayList<UserInfoResponse> response = new ArrayList<>();
        for (User user : allUsers
        ) {
            response.add(new UserInfoResponse(user));
        }

        return response;
    }

    @DeleteMapping("/admin/user/{email}")
    public DeleteUser removeUser(@PathVariable String email, @AuthenticationPrincipal UserDetails details) {

        Optional<User> user = users.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        adminService.deleteUser(user.get(), details.getUsername());
        return new DeleteUser(email, "Deleted successfully!");
    }

    @GetMapping("/security/events")
    public ArrayList<Events> getEvents() {
        return eventsRepository.findAll();
    }
}
