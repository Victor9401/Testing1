package bet.olimp.actions.components;

import bet.olimp.actions.entities.Bet;
import bet.olimp.actions.entities.Card;
import bet.olimp.actions.entities.ResultEnum;
import bet.olimp.actions.repo.BetRepo;
import bet.olimp.actions.repo.CardRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class KafkaConsumer {
    @Autowired
    BetRepo betRepo;
    @Autowired
    CardRepo cardRepo;

    @KafkaListener(id = "action_freebet_consumernew0", topics = {"BET_CUPIS"}, containerFactory = "singleFactory")
    public void consume(Bet bet) {
        bet.setKz(false);
        saveBetIfNeed(bet, 200);
    }

    @KafkaListener(id = "action_freebet_consumer_kz", topics = {"BET_KZ"}, containerFactory = "singleFactory")
    public void consumeKz(Bet bet) {
        bet.setKz(true);
        saveBetIfNeed(bet, 1000);
    }

    private void saveBetIfNeed(Bet bet, Integer limit) {
        if (bet == null) return;
        if (bet.getTypeBet() == 2 &&
                bet.getCashoutIs() == 0 &&
                bet.getBetSum() != null &&
                bet.getCardData() != null &&
                bet.getPaySum() != null &&
                bet.getPaySum() > bet.getBetSum() &&
                bet.getBetSum() >= limit &&
                bet.getPaySum() > limit) {
            deleteOldAndClearNullBytes(bet);
            bet.setSumCoef(getSum(bet.getCardData()));
            try {
                if (allWin(bet)) {
                    betRepo.save(bet);
                    log.info(new Date(bet.getCalcDate()) + ";" + bet.getLogin() + ";" + bet.getBetSum() + ";" + bet.getSumCoef() + ";" + bet.getBetNum() + " bet added");
                }
            } catch (Exception e) {
                log.error(bet.getLogin() + e.getMessage(), e);
            }
        } else if (
                bet.getTypeBet() == 2 &&
                        bet.getCashoutIs() == 0 &&
                        bet.getBetSum() != null &&
                        bet.getCardData() != null &&
                        bet.getBetSum() >= limit &&
                        failRecount(bet)
        ) {
            Bet failedBet = betRepo.findOneByCardId(bet.getCardId());
            if (failedBet != null) {
                betRepo.delete(failedBet);
                cardRepo.deleteAllByBetId(bet.getId());
                log.info(new Date(bet.getCalcDate()) + ";" + bet.getLogin() + ";" + bet.getBetSum() + ";" + bet.getSumCoef() + ";" + "fail recounted");
            }
        }
    }

    private boolean failRecount(Bet bet) {
        for (Card cardDatum : bet.getCardData()) {
            if (cardDatum.getResult() == ResultEnum.RECOUNT_FAIL || cardDatum.getResult() == ResultEnum.FAIL)
                return true;
        }
        return false;
    }

    private void deleteOldAndClearNullBytes(Bet bet) {
        Bet oldBet = betRepo.findOneByCardId(bet.getCardId());
        if (oldBet != null) {
            cardRepo.deleteAllByBetId(bet.getId());
            betRepo.delete(oldBet);
        }
        bet.getCardData().forEach(card -> {
            card.setMatch(card.getMatch().replaceAll("\u0000",""));
            card.setChamp(card.getChamp().replaceAll("\u0000",""));
            card.setSport(card.getSport().replaceAll("\u0000",""));
            card.setEvent(card.getEvent().replaceAll("\u0000",""));
            card.setBet(bet);
        });
    }

    private boolean allWin(Bet bet) {
        for (Card cardDatum : bet.getCardData()) {
            if (cardDatum.getResult() == ResultEnum.NO_COUNT || cardDatum.getResult() == ResultEnum.FAIL) return false;
        }
        return true;
    }


    private Double getSum(List<Card> cardData) {
        Double result = 1.0;
        for (Card cardDatum : cardData) {
            if (cardDatum.getResult() == ResultEnum.WIN && !cardDatum.getSport().equals("Бонус")) {
                result *= cardDatum.getCoef();
            } else if (cardDatum.getResult() == ResultEnum.RECOUNT_FAIL) {
                result *= cardDatum.getCoef() * 0.5;
            }
        }
        return result;
    }
}
