package account.handler;

import account.EventsType;
import account.entity.Events;
import account.repository.EventsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessFailureHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EventsRepository eventsRepository;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        Map<String, Object> data = new HashMap<>();
        data.put(
                "timestamp",
                LocalDateTime.now().toString());
        data.put(
                "status",
                response.getStatus());
        data.put(
                "error",
                "Forbidden");
        data.put(
                "message",
                "Access Denied!");
        data.put(
                "path",
                request.getRequestURI());

        Events event = new Events(
                LocalDateTime.now().toString(),
                EventsType.ACCESS_DENIED,
                request.getRemoteUser(),
                request.getRequestURI(),
                request.getRequestURI()
        );
        eventsRepository.save(event);
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
}

