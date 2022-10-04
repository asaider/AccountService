package account.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @NotEmpty
    @Pattern(regexp = ".+@acme.com$")
    @Column()
    private String email;
    @NotEmpty
    private String period;
    @Min(0)
    private Long salary;

    public Salary() {
    }

    public Salary(String email, String period, Long salary) {
        this.email = email;
        this.period = period;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getPeriod() {
        return period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
