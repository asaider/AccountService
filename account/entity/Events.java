package account.entity;

import account.EventsType;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String date;

    @Enumerated(EnumType.STRING)
    private EventsType action;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String object;

    @NotEmpty
    private String path;

    public Events() {

    }

    public Events(String date, EventsType action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public EventsType getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }
}
