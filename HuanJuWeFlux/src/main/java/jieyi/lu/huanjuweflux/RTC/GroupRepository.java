package jieyi.lu.huanjuweflux.RTC;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends ReactiveCrudRepository<Group, Long> {
}
