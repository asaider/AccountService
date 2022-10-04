package account.repository;

import account.entity.Salary;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface SalaryRepository extends CrudRepository<Salary, String> {

    Optional<Salary> findFirstByEmailAndPeriod(String email, String period);

    ArrayList<Salary> findAllByEmailOrderByPeriodDesc(String email);
}