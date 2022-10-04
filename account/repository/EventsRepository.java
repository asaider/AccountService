package account.repository;

import account.entity.Events;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface EventsRepository extends CrudRepository<Events, String> {
    ArrayList<Events> findAll();

    ArrayList<Events> findAllBySubjectOrderByDateDesc(String email);
}
