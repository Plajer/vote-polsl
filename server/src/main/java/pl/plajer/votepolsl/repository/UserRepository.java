package pl.plajer.votepolsl.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;

import org.springframework.stereotype.Repository;

import pl.plajer.votepolsl.data.User;

import java.util.Optional;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Repository
public interface UserRepository extends EntityGraphJpaRepository<User, Long> {

  Optional<User> findByIndexId(long indexId);

}
