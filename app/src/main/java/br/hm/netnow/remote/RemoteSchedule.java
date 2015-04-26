package br.hm.netnow.remote;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import br.hm.netnow.commons.JSONCallback;
import br.hm.netnow.commons.TemplateUrl;
import br.hm.netnow.utils.Moment;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class RemoteSchedule {


    private static String SCHEDULE_DEFAULT_URL = "http://programacao.netcombo.com.br/gatekeeper/exibicao/select?q=id_cidade:%d&wt=json";

    private static final String TIME_SERVER_URL = "http://www.netcombo.com.br/cs/Satellite/NET/tools/GetTimeServer";

    public static final String FIELD_END_DATE = "dh_fim";
    public static final String FIELD_START_DATE = "dh_inicio";
    public static final String FIELD_CITY_ID = "id_cidade";
    public static final String FIELD_TITLE_ST = "st_titulo";
    public static final String FIELD_TITLE = "titulo";
    public static final String FIELD_GENRE = "genero";
    public static final String FIELD_SCHEDULE_ID = "id_exibicao";
    public static final String FIELD_SHOW_ID = "id_programa";
    public static final String FIELD_CHANNEL_ID = "id_canal";

    public void query(int idCity, int maxRows, Moment moment, JSONCallback callback) throws IOException, JSONException {
        System.currentTimeMillis();
        String urlString = urlBuilder(idCity, maxRows, moment);
        TemplateUrl.query(urlString, callback);

    }

    public long currentTimeMillisOfTimeServer() {
        long result;
        try {
            byte[] bytes = TemplateUrl.urlToByteArray(TIME_SERVER_URL);
            String timeServerString = new String(bytes, "UTF-8").trim();
            result = Long.parseLong(timeServerString);
        } catch (UnsupportedEncodingException e) {
            result = System.currentTimeMillis();
        }
        return result;
    }


    private String urlBuilder(int idCity, int maxRows, Moment moment) {


        StringBuilder urlBuilder = new StringBuilder(String.format(SCHEDULE_DEFAULT_URL,
                idCity));
        if (maxRows > 0) {
            urlBuilder.append("&rows=");
            urlBuilder.append(maxRows);
        }
        urlBuilder.append("&sort=")
                .append(FIELD_START_DATE)
                .append("+asc")
                .append("&fl=").append(FIELD_END_DATE).append('+')
                .append(FIELD_START_DATE).append('+')
                .append(FIELD_CITY_ID).append('+')
                .append(FIELD_TITLE_ST).append('+')
                .append(FIELD_TITLE).append('+')
                .append(FIELD_SCHEDULE_ID).append('+')
                .append(FIELD_SHOW_ID).append('+')
                .append(FIELD_CHANNEL_ID).append('+')
                .append(FIELD_GENRE)
                .append("&fq=")
                .append(FIELD_START_DATE)
                .append(":")
                .append(buildFieldRangeDate(moment))
                .append("+dh_fim:")
                .append(buildFieldRangeDate(moment));

        return urlBuilder.toString();
    }

    private String buildFieldRangeDate(Moment moment) {
        StringBuilder builder = new StringBuilder();
        return builder.append('[')
                .append(moment.formatStartAsISO())
                .append("+TO+")
                .append(moment.formatEndAsISO())
                .append(']').toString();

    }
}
