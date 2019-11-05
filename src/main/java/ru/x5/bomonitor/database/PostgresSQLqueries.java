package ru.x5.bomonitor.database;

public class PostgresSQLqueries {

    //reciepts
    public static String BALANCE_DIFF = "SELECT (balance-safe_amount) as \"count\" FROM   ( SELECT ( SELECT Sum( To_number( text, '999999999D99' ) ) AS BALANCE FROM   GK_ACCOUNTING_PERIOD_APPENDIX WHERE  accounting_period_id =\n" +
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

    public static  String DUPLICATE_BONNR = "SELECT t.bonnr FROM gk_bonkopf t, gk_bonkopf d  WHERE \n" +
            "d.workstation_id = t.workstation_ID AND d.bon_seq_id != t.BON_SEQ_ID AND t.belegtyp = d.belegtyp AND t.belegtyp = '1' AND t.belegstatus = '5' AND d.BELEGSTATUS = '5' and \n" +
            "t.aktdat = d.aktdat and t.aktdat > cast(? as date)";

    public static  String DUPLICATE_BONNR_COUNT = "SELECT count(*) FROM gk_bonkopf t, gk_bonkopf d  WHERE \n" +
            "d.workstation_id = t.workstation_ID AND d.bon_seq_id != t.BON_SEQ_ID AND t.belegtyp = d.belegtyp AND t.belegtyp = '1' AND t.belegstatus = '5' AND d.BELEGSTATUS = '5' and \n" +
            "t.aktdat = d.aktdat and t.aktdat > cast(? as date)";

    public static String INCORRECT_BONNR = "select count(*) from gk_bonkopf where bonnr in ('0','-1')";
    public static String INCORRECT_BONNR_STR = "select bonnr from gk_bonkopf where bonnr in ('0','-1')";

    public static String QUEUE_RECIEPTS = "select count(TRANSACTION_SEQ_ID) from gk_transaction_cache t\n" +
            " inner join AS_WS k on k.ID_WS = t.WORKSTATION_ID and TY_WS in ( '0001', '0006', '0007', '0008' )\n" +
            " where import_ok = 'N' and t.ID_BSNGP=1 and receive_timestamp <  (CURRENT_TIMESTAMP - INTERVAL '5 hour')";
    public static String QUEUE_RECIEPTS_STR = "select TRANSACTION_SEQ_ID from gk_transaction_cache t\n" +
            " inner join AS_WS k on k.ID_WS = t.WORKSTATION_ID and TY_WS in ( '0001', '0006', '0007', '0008' )\n" +
            " where import_ok = 'N' and t.ID_BSNGP=1 and receive_timestamp <  (CURRENT_TIMESTAMP - INTERVAL '5 hour')";

    public static String STOCK_RECIEPT1="SELECT count(datka) FROM ( select B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1 else 0  END as CESIK \n" +
            "from ( select T1.DATKA  ,item_description ,sum(SUMY) as PRODAZHI ,T2.datka as datka2,sum(OSTATKI) as OSTATKIS,artnr  from (    select     cast(pos.aktdat as date) as DATKA   ,artnr\n" +
            "   ,sum(menge) as SUMY   ,item_description   from gk_bonkopf kop   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id   where    pos.aktdat > cast(? as date)   and pos.vorzeichen = 1\n" +
            "   and stornopos is null   and belegstatus = 5   group by datka,item_description,artnr    order by DATKA  ) as T1   full outer join (    select    datka,    abs(sum(unit_count)) as OSTATKI    ,item_id\n" +
            "    from (     select     cast(creation_timestamp as date) as datka     ,unit_count     ,item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY      where creation_timestamp > cast(? as date)    and stock_ledger_action_code = '11'    ) as T    group by datka,item_id    order by datka desc   ) as T2 on T1.datka = T2.datka    and artnr = item_id  group by T1.datka,T2.datka,item_description,artnr  order by T1.datka desc\n" +
            " ) as B) as S where CESIK = 1 ";
    public static String STOCK_RECIEPT2="select count(*) from ( select prodazhi, ostatki, artnr, case when PRODAZHI != OSTATKI or ostatki is null then 1   else 0    END as casik from( \n" +
            " select sum(prodazhi) as prodazhi, sum(ostatkis) as ostatki, artnr from( \n" +
            " SELECT datka, prodazhi, datka2, OSTATKIS, artnr FROM \n" +
            " (select   B.*,  case when PRODAZHI != OSTATKIS or ostatkis is null then 1  else 0   END as CESIK  from \n" +
            " (select    T1.DATKA, sum(SUMY) as PRODAZHI, T2.datka as datka2, sum(OSTATKI) as OSTATKIS, artnr\n" +
            "   from(select     cast(pos.aktdat as date) as DATKA, artnr, sum(menge) as SUMY    from gk_bonkopf kop\n" +
            "   join gk_bonposition pos on kop.bon_seq_id = pos.bon_seq_id    where pos.aktdat > cast(? as date)\n" +
            "    and pos.vorzeichen = 1    and stornopos is null    and belegstatus = 5    group by datka, artnr     order by DATKA) as T1 \n" +
            "      full outer join(select      datka, abs(sum(unit_count)) as OSTATKI, item_id     from \n" +
            "  (select      cast(creation_timestamp as date) as datka, unit_count, item_id      from GK_INVENT_TRANS_JOURNAL_ENTRY \n" +
            "   where creation_timestamp > cast(? as date)\n" +
            "    and stock_ledger_action_code = '11') as T \n" +
            "     group by datka, item_id     order by datka desc) as T2 on T1.datka = T2.datka     and artnr = item_id\n" +
            "      group by T1.datka, T2.datka, artnr   order by T1.datka desc) as B) as S where CESIK = 1) as S1 group by artnr) as s2 ) as s3 where casik = 1";


    //DBmonitoring

    public static String COUNT_ACTIVE_REQUESTS="select count(*) from pg_stat_activity";
    public static String ACTIVE_REQUESTS="select query from pg_stat_activity";
    public static String COUNT_AUTOVACUUM="SELECT count(*) FROM pg_stat_user_tables where schemaname = 'gkretail' and autovacuum_count > 0 and cast(last_autovacuum as date)=?";
    public static String COUNT_FROZEN_QUERIES = "SELECT count(*) FROM pg_stat_activity WHERE xact_start < (CURRENT_TIMESTAMP - INTERVAL '1 hour')";
    public static String FROZEN_QUERIES = "SELECT query FROM pg_stat_activity WHERE xact_start < (CURRENT_TIMESTAMP - INTERVAL '1 hour')";
    public static String COUNT_SAP_ERRORS_TX="select count(errorcode) from XRG_SAP_PI_TX where status != 'OK'";
    public static String COUNT_SAP_ERRORS_RX="select count(errorcode) from XRG_SAP_PI_RX where status != 'OK'";
    public static String SAP_ERRORS_TX="select msgtype from XRG_SAP_PI_TX where status != 'OK'";
    public static String SAP_ERRORS_RX="select mestype from XRG_SAP_PI_RX where status != 'OK'";


    //EGAIS
    public static String EGAIS_CREATION_TSTP = "select CREATION_TIMESTAMP from XRG_EGAIS_EXCISE_STAMPS_TMP where CREATION_TIMESTAMP < now()";
    public static String EGAIS_COUNT_TMP = "select count(CREATION_TIMESTAMP) from XRG_EGAIS_EXCISE_STAMPS_TMP where CREATION_TIMESTAMP < now()";


    //Items
    public static String COUNT_ITEMS_DIFF = "select count(ID_ITM) from as_itm t left join XRG_ITEM k on t.id_itm = k.item_id where k.item_id is null";
    public static String ITEMS_DIFF = "select ID_ITM from as_itm t left join XRG_ITEM k on t.id_itm = k.item_id where k.item_id is null";

    //Loyalty
    public static String COUNT_STOPPED_COUNTERS = "select count(cnt) from (select max(u.document_id) as LastDocId, u.upload_type_code u1Code, " +
            "u1.document_id as Index_docId, u1.upload_type_code uCode from GK_UPLOAD_PROTOCOL u join GK_UPLOAD_PROTOCOL u1 on " +
            "u1.process_type_code = 'P' where u.upload_type_code = u1.upload_type_code || '_F' || 'F' and u.process_type_code = 'F' " +
            "and u1.document_id < u.document_id group by u.upload_type_code, u1.document_id, u1.upload_type_code) as cnt";
    public static String STOPPED_COUNTERS = "select uCode from (select max(u.document_id) as LastDocId, u.upload_type_code u1Code, " +
            "u1.document_id as Index_docId, u1.upload_type_code uCode from GK_UPLOAD_PROTOCOL u join GK_UPLOAD_PROTOCOL u1 on " +
            "u1.process_type_code = 'P' where u.upload_type_code = u1.upload_type_code || '_F' || 'F' and u.process_type_code = 'F' " +
            "and u1.document_id < u.document_id group by u.upload_type_code, u1.document_id, u1.upload_type_code) as cnt";
    public static String COUNT_OLD_UNSENT_TRANSACTIONS="select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf" +
            " on bca.bon_seq_id=bf.bon_seq_id full join gk_upload_protocol up on up.document_id=bf.bon_seq_id " +
            " where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
            "and bca.BOOKING_CANCEL_FLAG='N' and date_part('day',age(up.process_timestamp,aktdat))>3 and aktdat between now() - interval '432 hour' and now() - interval '1 hour'";

    public static String OLD_UNSENT_TRANSACTIONS="select bf.bon_seq_id from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf" +
            " on bca.bon_seq_id=bf.bon_seq_id full join gk_upload_protocol up on up.document_id=bf.bon_seq_id " +
            " where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
            "and bca.BOOKING_CANCEL_FLAG='N' and date_part('day',age(up.process_timestamp,aktdat))>3 and aktdat between now() - interval '432 hour' and now() - interval '1 hour'";

    public static  String COUNT_UNSENT1="select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf " +
            "on bca.bon_seq_id=bf.bon_seq_id full join xrg_clm_response_journal jrnl on jrnl.document_id=bf.bon_seq_id " +
            "where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
            "and bca.BOOKING_CANCEL_FLAG='N'  and  bca.bon_seq_id not in (select document_id" +
            " from gk_upload_protocol) and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'" +
            " or bca.BOOKING_SUCCESSFUL_FLAG='N' and bca.BOOKING_CANCEL_FLAG='N' " +
            "and jrnl.error_code not in ('907','400','123','112','111','180','185','211','212','213','152','155','190','904') and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'";
    public static String COUNT_UNSENT2="select count(*) from XRG_BONPOS_STOLOTTO_TRANS full " +
            "join xrg_clm_response_journal jrnl on jrnl.document_id=XRG_BONPOS_STOLOTTO_TRANS.bon_seq_id " +
            "where SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and bon_seq_id not in " +
            "(select document_id from gk_upload_protocol) and XRG_BONPOS_STOLOTTO_TRANS.timestamp between now() - interval '432 hour' and now() - interval '1 hour' " +
            "or SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.timestamp " +
            "between now() - interval '432 hour' and now() - interval '1 hour' and jrnl.error_code not in('907','400','123','112','111','180','185','211','212','213','152','155','190','904')";
    public static  String UNSENT1="select bf.bon_seq_id from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf " +
            "on bca.bon_seq_id=bf.bon_seq_id full join xrg_clm_response_journal jrnl on jrnl.document_id=bf.bon_seq_id " +
            "where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
            "and bca.BOOKING_CANCEL_FLAG='N'  and  bca.bon_seq_id not in (select document_id" +
            " from gk_upload_protocol) and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'" +
            " or bca.BOOKING_SUCCESSFUL_FLAG='N' and bca.BOOKING_CANCEL_FLAG='N' " +
            "and jrnl.error_code not in ('907','400','123','112','111','180','185','211','212','213','152','155','190','904') and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'";
    public static String UNSENT2="select bon_seq_id from XRG_BONPOS_STOLOTTO_TRANS full " +
            "join xrg_clm_response_journal jrnl on jrnl.document_id=XRG_BONPOS_STOLOTTO_TRANS.bon_seq_id " +
            "where SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and bon_seq_id not in " +
            "(select document_id from gk_upload_protocol) and XRG_BONPOS_STOLOTTO_TRANS.timestamp between now() - interval '432 hour' and now() - interval '1 hour' " +
            "or SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.timestamp " +
            "between now() - interval '432 hour' and now() - interval '1 hour' and jrnl.error_code not in('907','400','123','112','111','180','185','211','212','213','152','155','190','904')";


    //Prices
    public static String COUNT_PRICE_GK="select count (*) from GK_PRICE_CHANGE_IMPORT where status = 'ERROR' and CREATION_TIMESTAMP > cast(? as date)";
    public static String COUNT_PRICE_CHANGE="select count (*) from GK_PRICE_CHANGE_CONTROL where status = 'ERROR' and CREATION_TIMESTAMP > cast(? as date)";
    public static String COUNT_UNPRINTED="select count (*) item_id from gk_price_change_control where status in ('NEW', 'PRINTED') and price_type_code in ('00', '01') group by item_id having count(*)>1";
    public static String PRICE_GK_ERR="select item_id from GK_PRICE_CHANGE_IMPORT where status = 'ERROR' and CREATION_TIMESTAMP > cast(? as date)";
    public static String PRICE_CHANGE_ERR="select item_id from GK_PRICE_CHANGE_CONTROL where status = 'ERROR' and CREATION_TIMESTAMP > cast(? as date)";
    public static String UNPRINTED_PRICES="select item_id item_id from gk_price_change_control where status in ('NEW', 'PRINTED') and price_type_code in ('00', '01') group by item_id having count(*)>1";
    public static String ITEM_SELLING_PRICES="select item_id,price_type_code,price_amount from gk_item_selling_prices where now() between price_effective_date and price_expiration_date";

    //Printers
    public static String COUNT_QUEUE_PRINTER = "select count(*) from gk_raw_change_event WHERE STATUS!='PROCESSED' and CREATED_TIMESTAMP < cast(? as date) and type_code in ('CHANGE_EVENT', 'PARAM.ITEM')";
    public static String QUEUE_PRINTER = "select task_id from gk_raw_change_event WHERE STATUS!='PROCESSED' and CREATED_TIMESTAMP < cast(? as date) and type_code in ('CHANGE_EVENT', 'PARAM.ITEM')";

    //Stock
    public static String COUNT_STOCK_ERR="select count(*) from GK_STOCK_LEDGER_ACCOUNT where cast(CURRENT_UNIT_COUNT as TEXT) like '%.____%'";
    public static String STOCK_ERR="select item_id from GK_STOCK_LEDGER_ACCOUNT where cast(CURRENT_UNIT_COUNT as TEXT) like '%.____%'";

    //TaskManager
    public static String COUNT_TASKS_NOT_TOP="SELECT count (*) FROM XRG_TASK_MGMT_GA_DETAILS \n" +
            "  WHERE PLU in (SELECT PLU FROM XRG_SALES_PROFILE WHERE PLU in \n" +
            "  (SELECT ITEM_ID FROM XRG_ITEM WHERE TOP_TYPECODE is null)\n" +
            "  and ACTIVE ='J')";
    public static String TASKS_NOT_TOP="SELECT task_mgmt_ga_id FROM XRG_TASK_MGMT_GA_DETAILS \n" +
            "  WHERE PLU in (SELECT PLU FROM XRG_SALES_PROFILE WHERE PLU in \n" +
            "  (SELECT ITEM_ID FROM XRG_ITEM WHERE TOP_TYPECODE is null)\n" +
            "  and ACTIVE ='J')";

    //TransportModule
    public static String COUNT_TRANSPORT_ERRORS="select count(*) from (select bon_seq_id from XRG_TRANSPORT_MODULE group by bon_seq_id having count(bon_seq_id)=1) r,\n" +
            " xrg_transport_module d where d.ERROR_DESCRIPTION is not null and d.TIMESTAMP > cast(? as date) and r.bon_seq_id = d.bon_seq_id";
    public static String TRANSPORT_ERRORS="select d.bon_seq_id from (select bon_seq_id from XRG_TRANSPORT_MODULE group by bon_seq_id having count(bon_seq_id)=1) r,\n" +
            " xrg_transport_module d where d.ERROR_DESCRIPTION is not null and d.TIMESTAMP > cast(? as date) and r.bon_seq_id = d.bon_seq_id";

}
