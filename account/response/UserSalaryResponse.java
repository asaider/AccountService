package account.response;

public class UserSalaryResponse {
    private final String name;
    private final String lastname;
    private final String period;
    private final String salary;

    public UserSalaryResponse(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPeriod() {
        return period;
    }

    public String getSalary() {
        return salary;
    }
}
