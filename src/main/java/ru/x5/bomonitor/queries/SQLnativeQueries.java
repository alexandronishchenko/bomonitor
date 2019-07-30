package ru.x5.bomonitor.queries;

import java.util.HashMap;

public class SQLnativeQueries {
    //Полные запросы для мониторинга для перехода от большого количества классов к типам сервисов и автосоздания метрики черезз заббикс.
    private static HashMap<String,String> queries = new HashMap<>();

    static {
        SQLnativeQueries.queries.put( "balancediff","SELECT (balance-safe_amount) as \"count\" FROM   ( SELECT ( SELECT Sum( To_number( text, '999999999D99' ) ) AS BALANCE FROM   GK_ACCOUNTING_PERIOD_APPENDIX WHERE  accounting_period_id =\n" +
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
                "WHERE  belegtyp = 50 ) ) AS SAFE_AMOUNT ) AS b");

        SQLnativeQueries.queries.put("duplicatebon","SELECT count(*) FROM gk_bonkopf t, gk_bonkopf d  WHERE \n" +
                "d.workstation_id = t.workstation_ID AND d.bon_seq_id != t.BON_SEQ_ID AND t.belegtyp = d.belegtyp AND t.belegtyp = '1' AND t.belegstatus = '5' AND d.BELEGSTATUS = '5' and \n" +
                "t.aktdat = d.aktdat and t.aktdat > '%%date%%'");
        SQLnativeQueries.queries.put("incorrectbon","select count(*) from gk_bonkopf where bonnr in ('0','-1','10000')");
        SQLnativeQueries.queries.put("queue","select count(TRANSACTION_SEQ_ID) from gk_transaction_cache t\n" +
                " inner join AS_WS k on k.ID_WS = t.WORKSTATION_ID and TY_WS in ( '0001', '0006', '0007', '0008' )\n" +
                " where import_ok = 'N' and t.ID_BSNGP=1 and receive_timestamp <  (CURRENT_TIMESTAMP - INTERVAL '5 hour')");
        SQLnativeQueries.queries.put("stockandrec1","SELECT count(datka) FROM ( select B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1 else 0  END as CESIK \n" +
                "from ( select T1.DATKA  ,item_description ,sum(SUMY) as PRODAZHI ,T2.datka as datka2,sum(OSTATKI) as OSTATKIS,artnr  from (    select     cast(pos.aktdat as date) as DATKA   ,artnr\n" +
                "   ,sum(menge) as SUMY   ,item_description   from gk_bonkopf kop   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id   where    pos.aktdat > '%%date%%'   and pos.vorzeichen = 1\n" +
                "   and stornopos is null   and belegstatus = 5   group by datka,item_description,artnr    order by DATKA  ) as T1   full outer join (    select    datka,    abs(sum(unit_count)) as OSTATKI    ,item_id\n" +
                "    from (     select     cast(creation_timestamp as date) as datka     ,unit_count     ,item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY      where creation_timestamp > '%%date%%'     and stock_ledger_action_code = '11'    ) as T    group by datka,item_id    order by datka desc   ) as T2 on T1.datka = T2.datka    and artnr = item_id  group by T1.datka,T2.datka,item_description,artnr  order by T1.datka desc\n" +
                " ) as B) as S where CESIK = 1 ");
        SQLnativeQueries.queries.put("stockandrec2","select count(*) from ( select prodazhi, ostatki, artnr, case when PRODAZHI != OSTATKI or ostatki is null then 1   else 0    END as casik from( \n" +
                " select sum(prodazhi) as prodazhi, sum(ostatkis) as ostatki, artnr from( \n" +
                " SELECT datka, prodazhi, datka2, OSTATKIS, artnr FROM \n" +
                " (select   B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1  else 0   END as CESIK  from \n" +
                " (select    T1.DATKA, sum(SUMY) as PRODAZHI, T2.datka as datka2, sum(OSTATKI) as OSTATKIS, artnr\n" +
                "   from(select     cast(pos.aktdat as date) as DATKA, artnr, sum(menge) as SUMY    from gk_bonkopf kop\n" +
                "   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id    where pos.aktdat > '%%date%%'\n" +
                "    and pos.vorzeichen = 1    and stornopos is null    and belegstatus = 5    group by datka, artnr     order by DATKA) as T1 \n" +
                "      full outer join(select      datka, abs(sum(unit_count)) as OSTATKI, item_id     from \n" +
                "  (select      cast(creation_timestamp as date) as datka, unit_count, item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY \n" +
                "   where creation_timestamp > '%%date%%'\n" +
                "    and stock_ledger_action_code = '11') as T \n" +
                "     group by datka, item_id     order by datka desc) as T2 on T1.datka = T2.datka     and artnr = item_id\n" +
                "      group by T1.datka, T2.datka, artnr   order by T1.datka desc) as B) as S where CESIK = 1) as S1 group by artnr) as s2 ) as s3 where casik = 1");
        SQLnativeQueries.queries.put("getpriceerror1","select count (*) from GK_PRICE_CHANGE_IMPORT where status = 'ERROR' and CREATION_TIMESTAMP > '%%date%%'");
        SQLnativeQueries.queries.put("getpriceerror2","select count (*) from GK_PRICE_CHANGE_CONTROL where status = 'ERROR' and CREATION_TIMESTAMP > '%%date%%'");
        SQLnativeQueries.queries.put("pricesunconfirmed","select count (*) item_id from gk_price_change_control where status in ('NEW', 'PRINTED') and price_type_code in ('00', '01') group by item_id having count(*)>1");
        //Loyalty
        SQLnativeQueries.queries.put("stoppedcounters","select count(cnt) from (select max(u.document_id) as LastDocId, u.upload_type_code u1Code," +
                "                u1.document_id as Index_docId, u1.upload_type_code uCode from GK_UPLOAD_PROTOCOL u join GK_UPLOAD_PROTOCOL u1 on" +
                "                u1.process_type_code = 'P' where u.upload_type_code = u1.upload_type_code || '_F' || 'F' and u.process_type_code = 'F'" +
                "                and u1.document_id < u.document_id group by u.upload_type_code, u1.document_id, u1.upload_type_code) as cnt");
        SQLnativeQueries.queries.put("longunsenttransactions","\"select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf" +
                "                 on bca.bon_seq_id=bf.bon_seq_id full join gk_upload_protocol up on up.document_id=bf.bon_seq_id  " +
                "                 where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N'" +
                "                and bca.BOOKING_CANCEL_FLAG='N' and date_part('day',age(up.process_timestamp,aktdat))>3 and aktdat between now() - interval '432 hour' and now() - interval '1 hour'");
        SQLnativeQueries.queries.put("unsent1","select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf " +
                "                    on bca.bon_seq_id=bf.bon_seq_id full join xrg_clm_response_journal jrnl on jrnl.document_id=bf.bon_seq_id " +
                "                    where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
                "                    and bca.BOOKING_CANCEL_FLAG='N'  and  bca.bon_seq_id not in (select document_id" +
                "                     from gk_upload_protocol) and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'" +
                "                     or bca.BOOKING_SUCCESSFUL_FLAG='N' and bca.BOOKING_CANCEL_FLAG='N' " +
                "                    and jrnl.error_code not in ('907','400','123','112') and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'");
    }

    static public String getQuery(String name, String date){

        return SQLnativeQueries.queries.get(name).replaceAll("%%date%%",date);
    }
}
