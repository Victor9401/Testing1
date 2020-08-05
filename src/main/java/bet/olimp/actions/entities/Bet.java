package bet.olimp.actions.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "bet")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String login;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bet")
    @JsonProperty("card_data")
    @JsonIgnore
    private List<Card> cardData;
    Double betSum, sumCoef, paySum;
    Long betDate, calcDate;
    Integer cashoutIs, betNum;
    Integer typeBet;
    //@Column(unique = true)
    Long cardId;

    @Enumerated(EnumType.ORDINAL)
    BetPlatfromEnum betPlatform;
    @ColumnDefault("false")
    Boolean kz;//null or false for ru


    @JsonIgnore
    public List<Card> getCardData() {
        return cardData;
    }
}