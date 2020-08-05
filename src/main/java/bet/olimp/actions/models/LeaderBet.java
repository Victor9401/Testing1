package bet.olimp.actions.models;

import lombok.Data;

@Data
public class LeaderBet {
    Long id;
    Integer number;
    String login;
    String koef;
    String prize;
}
