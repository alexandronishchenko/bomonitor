package ru.x5.bomonitor.Services;

import ru.x5.bomonitor.DBConnection;

import java.sql.SQLException;
import java.util.HashMap;
@ServiceUnit
public class Loyalty implements Service {

    public int get(String directive){
        if(directive.equals("counter")){
            return getStoppedCounters();
        }else if(directive.equals("long")){
            return getLongUnsetTransactions();
        }else if(directive.equals("unsent")){
            return getUnsentTransactions();
        }else{
            return getStoppedCounters()+getUnsentTransactions()+getLongUnsetTransactions();
        }

    }
    public  int get(String directive,String subquery){
        return 0;
    }
    public int getStoppedCounters(){
        String query = "select count(cnt) from (select max(u.document_id) as LastDocId, u.upload_type_code u1Code, " +
                "u1.document_id as Index_docId, u1.upload_type_code uCode from GK_UPLOAD_PROTOCOL u join GK_UPLOAD_PROTOCOL u1 on " +
                "u1.process_type_code = 'P' where u.upload_type_code = u1.upload_type_code || '_F' || 'F' and u.process_type_code = 'F' " +
                "and u1.document_id < u.document_id group by u.upload_type_code, u1.document_id, u1.upload_type_code) as cnt";

        try {
            HashMap<String,String> map = DBConnection.executeSelect(query);
            return Integer.parseInt(map.get("count"));

        } catch (SQLException e) {
            e.printStackTrace();
            return 99999;
        }
    }

    public int getLongUnsetTransactions(){

        String query="select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf" +
                " on bca.bon_seq_id=bf.bon_seq_id full join gk_upload_protocol up on up.document_id=bf.bon_seq_id " +
                " where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
                "and bca.BOOKING_CANCEL_FLAG='N' and date_part('day',age(up.process_timestamp,aktdat))>3 and aktdat between now() - interval '432 hour' and now() - interval '1 hour'";


        try {
            HashMap<String,String> map = DBConnection.executeSelect(query);
           return Integer.parseInt(map.get("count"));


        } catch (SQLException e) {
            e.printStackTrace();
            return 99999;
        }
    }

    public int getUnsentTransactions(){
        try {
            String query1="select count(*) from GK_BON_CUST_ACCOUNT_TRANSACT bca full join gk_bonkopf bf " +
                    "on bca.bon_seq_id=bf.bon_seq_id full join xrg_clm_response_journal jrnl on jrnl.document_id=bf.bon_seq_id " +
                    "where bca.bon_seq_id in (select bon_seq_id from GK_BONKOPF_BINARY_ADDON) and bca.BOOKING_SUCCESSFUL_FLAG='N' " +
                    "and bca.BOOKING_CANCEL_FLAG='N'  and  bca.bon_seq_id not in (select document_id" +
                    " from gk_upload_protocol) and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'" +
                    " or bca.BOOKING_SUCCESSFUL_FLAG='N' and bca.BOOKING_CANCEL_FLAG='N' " +
                    "and jrnl.error_code not in ('907','400','123','112') and aktdat between now() - interval '432 hour' and now() - interval '1 hour' and belegstatus!='1'";
            String query2="select count(*) from XRG_BONPOS_STOLOTTO_TRANS full " +
                    "join xrg_clm_response_journal jrnl on jrnl.document_id=XRG_BONPOS_STOLOTTO_TRANS.bon_seq_id " +
                    "where SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and bon_seq_id not in " +
                    "(select document_id from gk_upload_protocol) and XRG_BONPOS_STOLOTTO_TRANS.timestamp between now() - interval '432 hour' and now() - interval '1 hour' " +
                    "or SUCCESS_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.CANCEL_FLAG='N' and XRG_BONPOS_STOLOTTO_TRANS.timestamp " +
                    "between now() - interval '432 hour' and now() - interval '1 hour' and jrnl.error_code not in('907','400','123','112')";
            HashMap<String,String> map = DBConnection.executeSelect(query1);
            HashMap<String,String> map2 = DBConnection.executeSelect(query2);
            return Integer.parseInt(map.get("count"))+ Integer.parseInt(map2.get("count"));

        } catch (SQLException e) {
            e.printStackTrace();
            return 99999;
        }
    }

}
