package account.service;

import account.entity.Salary;
import account.entity.User;
import account.repository.UserRepository;
import account.repository.SalaryRepository;
import account.request.SalaryInfo;
import account.response.UserSalaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class SalaryService {

    @Autowired
    SalaryRepository salaryRepository;
    @Autowired
    UserRepository users;

    @Transactional
    public void add(SalaryInfo[] salaryInfo) throws Exception {

        for (SalaryInfo info : salaryInfo
        ) {
            String period = validationPeriod(info.getPeriod()).toString();
            if (salaryRepository.findFirstByEmailAndPeriod(info.getEmployee(), period).isPresent()) {
                throw new Exception("The employee-period pair must be unique");
            }

            Salary salary = new Salary(
                    info.getEmployee(),
                    period,
                    info.getSalary()
            );
            salaryRepository.save(salary);
        }
    }

    public void updateSalary(SalaryInfo salaryInfo) throws Exception {
        Optional<User> user = users.findByEmailIgnoreCase(salaryInfo.getEmployee());

        if (user.isPresent()) {
            String period = validationPeriod(salaryInfo.getPeriod()).toString();
            Optional<Salary> salaryData = salaryRepository.findFirstByEmailAndPeriod(salaryInfo.getEmployee(), period);
            if (salaryData.isPresent()) {
                Salary salary = salaryData.get();
                salary.setSalary(salaryInfo.getSalary());
                salaryRepository.save(salary);
                return;
            }
            throw new Exception("Salary information was not found for user - %s".formatted(salaryInfo.getEmployee()));
        }
        throw new Exception("User doesn't exist");
    }

    public UserSalaryResponse getSalaryInformationByUserAndPeriod(User user, String periodParam) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-yyyy");

        String period = validationPeriod(periodParam).toString();
        Optional<Salary> salaryData = salaryRepository.findFirstByEmailAndPeriod(user.getEmail(), period);
        if (salaryData.isPresent()) {
            ArrayList<UserSalaryResponse> response = new ArrayList<UserSalaryResponse>();
            String date = simpleDateFormat.format(new SimpleDateFormat("yyyy-MM-dd").parse(salaryData.get().getPeriod()));
            return new UserSalaryResponse(
                    user.getName(),
                    user.getLastname(),
                    date,
                    renderSalary(salaryData.get().getSalary())
            );
        }
        throw new Exception("Salary information was not found for user - %s".formatted(user.getEmail()));
    }

    public ArrayList<UserSalaryResponse> getSalaryInformationByUser(User user) throws Exception {
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-yyyy");

            ArrayList<Salary> allSalaryInfo = salaryRepository.findAllByEmailOrderByPeriodDesc(user.getEmail());
            ArrayList<UserSalaryResponse> response = new ArrayList<UserSalaryResponse>();

            for (Salary data : allSalaryInfo
            ) {
                String date = simpleDateFormat.format(new SimpleDateFormat("yyyy-MM-dd").parse(data.getPeriod()));

                response.add(new UserSalaryResponse(
                        user.getName(),
                        user.getLastname(),
                        date,
                        renderSalary(data.getSalary())
                ));
            }
            return response;
        }
    }

    private String renderSalary(Long salary) {

        Integer dollars = (int) (salary / 100);
        int cents = (int) (salary % 100);
        return "%d dollar(s) %d cent(s)".formatted(dollars, cents);
    }

    private LocalDate validationPeriod(String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        YearMonth ym = YearMonth.parse(period, formatter);
        return ym.atEndOfMonth();
    }
}
