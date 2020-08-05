package bet.olimp.actions.repo;

import bet.olimp.actions.entities.Bet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepo extends CrudRepository<Bet, Long> {


    @Query(value = " SELECT b.*\n" +
            " FROM (SELECT *, ROW_NUMBER() OVER (PARTITION BY login ORDER BY sum_coef DESC) as r \n" +
            " FROM bet  \n" +
            " WHERE (bet_date BETWEEN ?1 AND ?2)\n" +
            " AND (calc_date BETWEEN ?1 AND ?2)\n" +
            " AND (pay_sum > ?5)\n" +
            "  AND (bet_sum >= ?5)\n" +
            "  AND (cashout_is = 0)\n" +
            "  AND (type_bet = 2)\n" +
            "  AND kz=?4) as b" +
            " WHERE b.r=1 \n" +
            " order by sum_coef desc\n" +
            " limit ?3", nativeQuery = true)
    List<Bet> getLeaders(long begin, long end, Integer limit, boolean kz, Double paysum);


    public Bet findOneByCardId(Long cardId);


    @Query(value = "SELECT *\n" +
            " FROM bet\n" +
            " WHERE (bet_date BETWEEN ?1 AND ?2)\n" +
            "  AND (calc_date BETWEEN ?1 AND ?2)\n" +
            "  AND (pay_sum >?4)\n" +
            "  AND (bet_sum >=?4)\n" +
            "  AND (cashout_is =0)\n" +
            "  AND (type_bet =2)\n" +
            "  AND (login = ?3)\n" +
            " ORDER BY sum_coef DESC", nativeQuery = true)
    List<Bet> getMe(long begin, long end, String login, Double moneyLimit);

    @Query(value = "SELECT count(distinct(login))\n" +
            "FROM bet\n" +
            "WHERE (bet_date BETWEEN ?1 AND ?2)\n" +
            "  AND (calc_date BETWEEN ?1 AND ?2)\n" +
            "  AND (pay_sum >?6)\n" +
            "  AND (bet_sum >=?6)\n" +
            "  AND (cashout_is =0)\n" +
            "  AND (type_bet =2)\n" +
            "  AND (kz = ?5)\n" +
            "  AND (login != ?3)\n" +
            "  AND (sum_coef > ?4)\n", nativeQuery = true)
    Integer getNumber(long begin, long end, String login, Double sum_coef, Boolean kz, Double moneyLimit);
}
