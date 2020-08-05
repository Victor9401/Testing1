package bet.olimp.actions.controllers;

import bet.olimp.actions.components.BetService;
import bet.olimp.actions.entities.Bet;
import bet.olimp.actions.entities.Card;
import bet.olimp.actions.models.LeaderBet;
import bet.olimp.actions.models.MeResponce;
import bet.olimp.actions.models.SessionResponce;
import bet.olimp.actions.repo.BetRepo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@Slf4j
public class Controller {
    @Value("${secret.token}")
    private String secretToken;

    @Value("${session.url}")
    private String sessionUrl;
    @Autowired
    BetRepo betRepo;
    @Autowired
    BetService betService;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @GetMapping("/leaders")
    public List<LeaderBet> leaders(String date, @RequestParam(required = false, defaultValue = "false") boolean kz) {
        return betService.leaders(date, kz, moneyLimit(kz));
    }

    @GetMapping("/bets")
    public List<Card> cards(Long id) {
        return betService.bet(id);
    }

    @GetMapping("/admin")
    String admin(String date,
                 @RequestParam(required = true) String token,
                 @RequestParam(required = false, defaultValue = "false") Boolean kz) {
        if (!token.equals(secretToken)) {
            return null;
        }
        Long[] period = betService.period(date, kz);
        List<Bet> leaders = betRepo.getLeaders(period[0], period[1], 20, kz, moneyLimit(kz));


        StringBuilder result = new StringBuilder();
        int i = 0;
        result.append("<h1>");
        if (kz) {
            result.append(" КАЗАХСТАН.");
        }
        result.append(" ПОБЕДИТЕЛИ ЗА  ");
        result.append(new Date(period[0]));
        result.append("-");
        result.append(new Date(period[1]));
        result.append("</h1><br>");
        for (Bet bet : leaders) {
            i++;
            result.append("№" + i);
            result.append("  ");
            result.append(bet.getLogin());
            result.append(" -> кoэффициент: ");
            result.append(bet.getSumCoef());
            result.append(" сумма ставки: ");
            result.append(bet.getBetSum());
            result.append(" id ставки: ");
            result.append(bet.getCardId());
            result.append(" номер ставки: ");
            result.append(bet.getBetNum());
            result.append("<br>");
        }
        return result.toString();
    }

    @GetMapping("/getMyActionExpress")
    public MeResponce getMyExpress(String session,
                                   @RequestParam(required = false, defaultValue = "false") Boolean kz) {
        Long[] period = betService.period(null, kz);
        String login = getLogin(session, kz);
        if (login != null) {
            List<Bet> me = betRepo.getMe(period[0], period[1], login, moneyLimit(kz));
            if (!me.isEmpty()) {
                return new MeResponce(me, betRepo.getNumber(period[0], period[1], login, me.get(0).getSumCoef(), kz, moneyLimit(kz)) + 1);
            } else return new MeResponce(me, -1);
        } else return new MeResponce(new ArrayList<Bet>(), -2);
    }

    private String getLogin(String session, boolean kz) {
        HttpHeaders headers = new HttpHeaders();
        String tok = createToken(session);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Token", tok);
        if (kz) {
            headers.add("X-Cupis", "0");
        } else {
            headers.add("X-Cupis", "1");
        }
        String json = "{\"lang_id\":\"0\", \"platforma\":\"SITE_CUPIS\",\"session\":\"" + session + "\"}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            SessionResponce sessionResponce = this.restTemplate.postForObject(sessionUrl, request, SessionResponce.class);
            if (sessionResponce != null && sessionResponce.getData() != null && sessionResponce.getData().getL() != null) {
                return sessionResponce.getData().getL();
            } else {
                log.warn("cant find login for session " + session);
                return null;
            }
        } catch (RestClientException e) {
            log.warn("restException for session " + session + " ");
            return null;
        }
    }

    private String createToken(String session) {
        String json = "0;SITE_CUPIS;" + session + ";" + "olimp_secret_token";
        return calculate(json);
    }

    Double moneyLimit(Boolean kz) {
        if (kz) {
            return 1000.0;
        } else {
            return 200.0;
        }
    }


    private String calculate(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(s.getBytes(Charset.defaultCharset()));
            String ht = new BigInteger(1, md5.digest()).toString(16);
            val length = ht.length();
            if (length < 32) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 32 - length; i++) {
                    sb.insert(0, i);
                }
                return sb.toString() + ht;

            } else return ht;
        } catch (Exception e) {
            return null;
        }
    }
}