package bet.olimp.actions.models;

import bet.olimp.actions.entities.Bet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MeResponce {
    List<Bet> bets;
    Integer place;
}
