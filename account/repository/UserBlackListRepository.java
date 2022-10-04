package account.repository;

import account.entity.UserBlackList;
import org.springframework.data.repository.CrudRepository;

public interface UserBlackListRepository extends CrudRepository<UserBlackList, String> {

}
