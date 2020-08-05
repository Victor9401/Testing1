package bet.olimp.actions.repo;

import bet.olimp.actions.entities.Card;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepo extends CrudRepository<Card, Long> {
    Card findOneByCardDataId(Long cardDataId);

    List<Card> findAllByBetId(Long betId);

    @Modifying
    void deleteAllByBetId(Long id);
}

