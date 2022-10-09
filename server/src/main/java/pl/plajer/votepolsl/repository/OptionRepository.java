package pl.plajer.votepolsl.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;

import org.springframework.stereotype.Repository;

import pl.plajer.votepolsl.data.SelectableOption;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Repository
public interface OptionRepository extends EntityGraphJpaRepository<SelectableOption, Long> {
}
