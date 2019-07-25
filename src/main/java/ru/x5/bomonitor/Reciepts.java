package ru.x5.bomonitor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reciepts implements Service {
    @Override
    public int get(String directive) {
        int res=0;
        try{
            switch (directive){
                case "balancediff":
                    res= getBalanceDiff();
                    break;
                case "duplicatebon":
                    res= getDuplicatesBon();
                    break;
                case "incorrectbon":
                    res= getIncorrectBonnr();
                    break;
                case "queue":
                    res= getQueue();
                    break;
                case "stockandreciept":
                    res= getStockAndReciept();
                    break;
            }
        }catch (SQLException e){
            e.printStackTrace();

        }
        return res;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }

    public int getBalanceDiff() throws SQLException {
        String query = "SELECT (balance-safe_amount) as \"count\" FROM   ( SELECT ( SELECT Sum( To_number( text, '999999999D99' ) ) AS BALANCE FROM   GK_ACCOUNTING_PERIOD_APPENDIX WHERE  accounting_period_id =\n" +
                "( SELECT accounting_period_id\n" +
                "FROM   GK_ACCOUNTING_PERIOD\n" +
                "WHERE  period_type_code = 'SA' AND\n" +
                "state_code = 'O' ) AND\n" +
                "NAME IN ( 'BALANCE_CASH_E5', 'BALANCE_CASH_X5' ) ) AS BALANCE,\n" +
                "( SELECT totalbonbruttoges\n" +
                "FROM   GK_BONKOPF\n" +
                "WHERE  belegtyp = 50 AND\n" +
                "bon_seq_id =\n" +
                "( SELECT Max( bon_seq_id )\n" +
                "FROM   GK_BONKOPF\n" +
                "WHERE  belegtyp = 50 ) ) AS SAFE_AMOUNT ) AS b";
        return Integer.parseInt(DBConnection.executeSelect(query).get("count"));
    }
    public int getDuplicatesBon() throws SQLException {
        long dt = new Date().getTime()-(3*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String query = "SELECT count(*) FROM gk_bonkopf t, gk_bonkopf d  WHERE \n" +
                "d.workstation_id = t.workstation_ID AND d.bon_seq_id != t.BON_SEQ_ID AND t.belegtyp = d.belegtyp AND t.belegtyp = '1' AND t.belegstatus = '5' AND d.BELEGSTATUS = '5' and \n" +
                "t.aktdat = d.aktdat and t.aktdat > '"+date+"'";
        return Integer.parseInt(DBConnection.executeSelect(query).get("count"));
    }
    public int getIncorrectBonnr() throws SQLException {
        String s = "select count(*) from gk_bonkopf where bonnr in ('0','-1','10000')";
        return Integer.parseInt(DBConnection.executeSelect(s).get("count"));
    }
    public int getQueue() throws SQLException {
        String s = "select count(TRANSACTION_SEQ_ID) from gk_transaction_cache t\n" +
                " inner join AS_WS k on k.ID_WS = t.WORKSTATION_ID and TY_WS in ( '0001', '0006', '0007', '0008' )\n" +
                " where import_ok = 'N' and t.ID_BSNGP=1 and receive_timestamp <  (CURRENT_TIMESTAMP - INTERVAL '5 hour')";
        return Integer.parseInt(DBConnection.executeSelect(s).get("count"));
    }
    public int getStockAndReciept() throws SQLException {
        long dt = new Date().getTime()-(10*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String s1="SELECT count(datka) FROM ( select B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1 else 0  END as CESIK \n" +
                "from ( select T1.DATKA  ,item_description ,sum(SUMY) as PRODAZHI ,T2.datka as datka2,sum(OSTATKI) as OSTATKIS,artnr  from (    select     cast(pos.aktdat as date) as DATKA   ,artnr\n" +
                "   ,sum(menge) as SUMY   ,item_description   from gk_bonkopf kop   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id   where    pos.aktdat > '"+date+"'   and pos.vorzeichen = 1\n" +
                "   and stornopos is null   and belegstatus = 5   group by datka,item_description,artnr    order by DATKA  ) as T1   full outer join (    select    datka,    abs(sum(unit_count)) as OSTATKI    ,item_id\n" +
                "    from (     select     cast(creation_timestamp as date) as datka     ,unit_count     ,item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY      where creation_timestamp > '"+date+"'     and stock_ledger_action_code = '11'    ) as T    group by datka,item_id    order by datka desc   ) as T2 on T1.datka = T2.datka    and artnr = item_id  group by T1.datka,T2.datka,item_description,artnr  order by T1.datka desc\n" +
                " ) as B) as S where CESIK = 1 ";
        String s2="select count(*) from ( select prodazhi, ostatki, artnr, case when PRODAZHI != OSTATKI or ostatki is null then 1   else 0    END as casik from( \n" +
                " select sum(prodazhi) as prodazhi, sum(ostatkis) as ostatki, artnr from( \n" +
                " SELECT datka, prodazhi, datka2, OSTATKIS, artnr FROM \n" +
                " (select   B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1  else 0   END as CESIK  from \n" +
                " (select    T1.DATKA, sum(SUMY) as PRODAZHI, T2.datka as datka2, sum(OSTATKI) as OSTATKIS, artnr\n" +
                "   from(select     cast(pos.aktdat as date) as DATKA, artnr, sum(menge) as SUMY    from gk_bonkopf kop\n" +
                "   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id    where pos.aktdat > '"+date+"'\n" +
                "    and pos.vorzeichen = 1    and stornopos is null    and belegstatus = 5    group by datka, artnr     order by DATKA) as T1 \n" +
                "      full outer join(select      datka, abs(sum(unit_count)) as OSTATKI, item_id     from \n" +
                "  (select      cast(creation_timestamp as date) as datka, unit_count, item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY \n" +
                "   where creation_timestamp > '"+date+"'\n" +
                "    and stock_ledger_action_code = '11') as T \n" +
                "     group by datka, item_id     order by datka desc) as T2 on T1.datka = T2.datka     and artnr = item_id\n" +
                "      group by T1.datka, T2.datka, artnr   order by T1.datka desc) as B) as S where CESIK = 1) as S1 group by artnr) as s2 ) as s3 where casik = 1";

        return Integer.parseInt(DBConnection.executeSelect(s1).get("count"))+Integer.parseInt(DBConnection.executeSelect(s2).get("count"));

    }
}
