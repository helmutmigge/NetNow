package br.hm.netnow.remote;

import org.json.JSONException;

import java.io.IOException;

import br.hm.netnow.commons.JSONCallback;
import br.hm.netnow.commons.TemplateUrl;

/**
 * Created by helmutmigge on 25/04/2015.
 */
public class RemoteShow {

    private static String SHOW_DEFAULT_URL = "http://programacao.netcombo.com.br/gatekeeper/prog/select?q=id_programa:%d&wt=json&rows=1";

    public static String FIELD_DIRECTOR = "diretor";
    public static String FIELD_RATING = "estrela";
    public static String FIELD_TITLE_ST = "st_titulo";
    public static String FIELD_ORIGINAL_TITLE = "titulo_original";
    public static String FIELD_TITLE = "titulo";
    public static String FIELD_DESCRIPTION = "descricao";
    public static String FIELD_SUBGENUS = "subgenero";
    public static String FIELD_GENRE = "genero";
    public static String FIELD_DURATION_MINUTES = "duracao";
    public static String FIELD_SHOW_ID = "id_programa";
    public static String FIELD_CONTENT_RATING = "censura";
    public static String FIELD_CAST = "elenco";


    public void query(int showId, JSONCallback callback) throws IOException, JSONException {
        String url = urlBuilder(showId);
        TemplateUrl.query(url,callback);


    }

    private String urlBuilder(int showId) {
        return new StringBuilder(String.format(SHOW_DEFAULT_URL,showId))
        .append("&fl=")
                .append(FIELD_DIRECTOR).append('+')
                .append(FIELD_RATING).append('+')
                .append(FIELD_TITLE_ST).append('+')
                .append(FIELD_ORIGINAL_TITLE).append('+')
                .append(FIELD_TITLE).append('+')
                .append(FIELD_DESCRIPTION).append('+')
                .append(FIELD_SUBGENUS).append('+')
                .append(FIELD_CAST).append('+')
                .append(FIELD_CONTENT_RATING).append('+')
                .append(FIELD_SHOW_ID).append('+')
                .append(FIELD_DURATION_MINUTES).append('+')
                .append(FIELD_GENRE).append('+').toString();


    }

    }
