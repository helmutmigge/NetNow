package br.hm.netnow.sync.remote;

import org.json.JSONException;

import java.io.IOException;

import br.hm.netnow.sync.commons.JSONCallback;
import br.hm.netnow.sync.commons.TemplateUrl;

public class RemoteChannel {

    private static String CHANNEL_DEFAULT_URL = "http://programacao.netcombo.com.br/gatekeeper/canal/select?q=id_cidade:%d&wt=json";

    private static String CHANNEL_IMAGE_URL = "http://www.netcombo.com.br/imagens/logo/%s-%s_95x39.png";

    public static final String FIELD_CATOGORY_ID = "id_categoria";
    public static final String FIELD_CATOGORY_NAME = "categoria";

    public static final String FIELD_CITY_ID = "id_cidade";

    public static final String FIELD_CHANNEL_ID = "id_canal";
    public static final String FIELD_CHANNEL_ST = "st_canal";
    public static final String FIELD_CHANNEL_NUMBER = "cn_canal";
    public static final String FIELD_CHANNEL_NAME = "nome";


    public void query(int idCity, int maxRows, JSONCallback callback) throws IOException, JSONException {
        String urlString = urlBuilder(idCity, maxRows, null);
        TemplateUrl.query(urlString, callback);
    }

    public void queryById(int idCity, int idChannel, JSONCallback callback) throws IOException, JSONException {
        String urlString = urlBuilder(idCity, 1, idChannel);
        TemplateUrl.query(urlString, callback);
    }

    public byte[] image(String st, String id) {
        String imageUrl = String.format(CHANNEL_IMAGE_URL, st, id);
        return TemplateUrl.urlToByteArray(imageUrl);
    }

    private String urlBuilder(int idCity, int maxRows, Integer idChannel) {
        StringBuilder urlBuilder = new StringBuilder(String.format(CHANNEL_DEFAULT_URL,
                idCity));
        if (maxRows > 0) {
            urlBuilder.append("&rows=");
            urlBuilder.append(maxRows);
        }
        urlBuilder.append("&sort=")
                .append(FIELD_CHANNEL_NUMBER)
                .append("+asc");

        urlBuilder.append("&fl=").append(FIELD_CATOGORY_ID).append('+')
                .append(FIELD_CATOGORY_NAME).append('+')
                .append(FIELD_CITY_ID).append('+')
                .append(FIELD_CHANNEL_ID).append('+')
                .append(FIELD_CHANNEL_ST).append('+')
                .append(FIELD_CHANNEL_NUMBER).append('+')
                .append(FIELD_CHANNEL_NAME);

        if (idChannel != null) {
            urlBuilder.append("&fq=id_canal%3A");
            urlBuilder.append(idChannel);
        }
        return urlBuilder.toString();
    }

}
