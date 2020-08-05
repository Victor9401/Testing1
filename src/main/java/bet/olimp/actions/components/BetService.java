package bet.olimp.actions.components;

import bet.olimp.actions.entities.Bet;
import bet.olimp.actions.entities.Card;
import bet.olimp.actions.models.LeaderBet;
import bet.olimp.actions.repo.BetRepo;
import bet.olimp.actions.repo.CardRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BetService {

    final BetRepo betRepo;
    final CardRepo cardRepo;
    private final String[] prizes = new String[]{"30 000", "20 000", "15 000", "10 000", "8 000", "6 000", "4 000", "3 000", "2 000", "2 000"};
    private final String[] prizesKz = new String[]{"150 000", "100 000", "75 000", "50 000", "40 000", "30 000", "20 000", "15 000", "10 000", "10 000"};

    public BetService(BetRepo betRepo, CardRepo cardRepo) {
        this.betRepo = betRepo;
        this.cardRepo = cardRepo;
    }

    List<LeaderBet> convertToLeadersBet(List<Bet> leaders, boolean kz) {
        ArrayList<LeaderBet> bets = new ArrayList<>();
        int i = 0;
        for (Bet leader : leaders) {
            LeaderBet bet = new LeaderBet();
            Double newDouble = new BigDecimal(leader.getSumCoef()).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            Integer length = newDouble.toString().split("\\.")[1].length();
            if (length == 2) {
                bet.setKoef(newDouble.toString());
            } else if (length == 1) {
                bet.setKoef(newDouble.toString() + "0");
            }
            bet.setId(leader.getId());
            bet.setNumber(i);
            if (kz) {
                bet.setPrize(prizesKz[i] + " тг");
            } else {
                bet.setPrize(prizes[i] + " руб.");
            }
            String substring = leader.getLogin().substring(2, leader.getLogin().length() - 2);
            bet.setLogin(leader.getLogin().replace(substring, new String(new char[substring.length()]).replace('\0', '*')));
            i++;
            bet.setNumber(i);
            bets.add(bet);
        }
        return bets;
    }

    @Cacheable("leaders")
    public List<LeaderBet> leaders(String date, boolean kz, Double moneyLimit) {
        Long[] period = period(date, kz);
        List<Bet> leaders = betRepo.getLeaders(period[0], period[1], 10, kz, moneyLimit);
        return convertToLeadersBet(leaders, kz);
    }

    public Long[] period(String date, Boolean kz) {
        String timeZone = kz ? "Asia/Almaty" : "Europe/Moscow";
        long dayStartMillis;
        if (date != null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");//todo ЧАСОВОЙ ПОЯС КАЗАХСТАНА
            LocalDateTime ldt = LocalDateTime.parse(date + " 00:00", format);
            dayStartMillis = ldt.atZone(ZoneId.of(timeZone)).toInstant().toEpochMilli();
        } else {
            dayStartMillis = LocalDate.now(ZoneId.of(timeZone)).atStartOfDay(ZoneId.of(timeZone)).toInstant().toEpochMilli();
        }
        long begin = dayStartMillis;
        long end = begin + 24 * 60 * 60 * 1000 - 1;
        return new Long[]{begin, end};
    }

    public List<Card> bet(Long id) {
        List<Card> allByBetId = cardRepo.findAllByBetId(id);
        allByBetId.forEach(card -> {
            card.setBet(null);
        });
        return allByBetId;
    }
}
