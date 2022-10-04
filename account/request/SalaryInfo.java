package account.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SalaryInfo {
    @NotEmpty
    @Pattern(regexp = ".+@acme.com$")
    private String employee;
    @NotEmpty
    private String period;

    @Min(0)
    private Long salary;

    public SalaryInfo() {

    }

    public SalaryInfo(String employee, String period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
