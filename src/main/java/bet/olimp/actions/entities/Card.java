package bet.olimp.actions.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "card")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    Long cardDataId;
    Double coef;
    Integer eventType, matchId;

    String match;

    String champ;

    String event;

    @Enumerated(EnumType.ORDINAL)
    ResultEnum result;
    String sport;
    Integer sportId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bet_id")
    Bet bet;

}
