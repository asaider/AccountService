package account.controller;

import account.EventsType;
import account.entity.Events;
import account.entity.Role;
import account.repository.EventsRepository;
import account.repository.RoleRepository;
import account.response.ChangePasswordResponse;
import account.response.UserInfoResponse;
import account.service.SalaryService;
import account.entity.User;
import account.repository.UserRepository;
import account.request.ChangePassword;
import account.request.SalaryInfo;
import account.response.DefaultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    UserRepository users;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    SalaryService salaryService;

    @Autowired
    EventsRepository eventsRepository;

    @PostMapping("/auth/signup")
    public UserInfoResponse signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
        }
        if (users.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));

        Role role = getInitRoleForUser(user);
        user.setRoles(List.of(role));
        users.save(user);
        Events event = new Events(
                LocalDateTime.now().toString(),
                EventsType.CREATE_USER,
                "Anonymous",
                user.getEmail(),
                "/api/auth/signup"
        );
        eventsRepository.save(event);
        return new UserInfoResponse(user);
    }

    @GetMapping("/empl/payment")
    public ResponseEntity get(@RequestParam(value = "period", required = false) String period, @AuthenticationPrincipal UserDetails details) {
        try {
            String email = details.getUsername();
            User user = users.findByEmailIgnoreCase(email).get();
            if (period != null) {
                return new ResponseEntity<>(salaryService.getSalaryInformationByUserAndPeriod(user, period), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(salaryService.getSalaryInformationByUser(user), HttpStatus.OK);
            }
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/auth/changepass")
    public ChangePasswordResponse changePass(
            @AuthenticationPrincipal UserDetails details,
            @Valid @RequestBody ChangePassword data,
            BindingResult bindingResult
    ) {
        if (details == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This api only for authenticated user\n");
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
        }
        Optional<User> user = users.findByEmailIgnoreCase(details.getUsername());
        if (user.isPresent()) {
            if (encoder.matches(data.getPassword(), user.get().getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
            }
            user.get().setPassword(encoder.encode(data.getPassword()));
            users.save(user.get());
            Events event = new Events(
                    LocalDateTime.now().toString(),
                    EventsType.CHANGE_PASSWORD,
                    user.get().getEmail(),
                    user.get().getEmail(),
                    "/api/auth/changepass"
            );
            eventsRepository.save(event);
            return new ChangePasswordResponse(user.get());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't exist!");
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<DefaultResponse> payments(@Valid @RequestBody SalaryInfo[] salaryInfo) {
        try {
            salaryService.add(salaryInfo);
            return new ResponseEntity<>(new DefaultResponse("Added successfully!"), HttpStatus.OK);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<DefaultResponse> putSalary(@Valid @RequestBody SalaryInfo salaryInfo) {
        try {
            salaryService.updateSalary(salaryInfo);
            return new ResponseEntity<>(new DefaultResponse("Updated successfully!"), HttpStatus.OK);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private Role getInitRoleForUser(User user) {
        if (users.count() > 0) {
            Optional<Role> role = roleRepository.findByName("ROLE_USER");
            return role.orElseGet(() -> creteRole("ROLE_USER", user));
        }
        return creteRole("ROLE_ADMINISTRATOR", user);
    }

    private Role creteRole(String roleName, User user) {
        Role role = new Role(roleName);
        roleRepository.save(role);
        return role;
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        return formatter.format(date);
    }
}
