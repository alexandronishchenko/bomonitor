package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;

@ServiceNative("Корректность плю в БД")
public class PluCorrectData extends  ParrentNativeService{

    public PluCorrectData() {
        this.name="plucorrectdata";
        this.value="";
    }

    @Override
    public String get(String directive)  {
        String result="";
        try {
            switch (directive) {
                case "strincorrect":
                    result = getIncorrectPlu();
                    break;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @StringMetric(value = "Неправильные данные ПЛЮ",directive = "native.plucorrectdata.strincorrect")
    public String getIncorrectPlu() throws SQLException {
        //Отсутствие UOM_CODE
        String res="";
        String s1 = PostgresConnection.getNote(PostgresSQLqueries.INCORRECT_PLU_UOM).get("");
        if(!s1.equals("null")&&s1!=null)res+=s1;
        return s1;
    }
}
