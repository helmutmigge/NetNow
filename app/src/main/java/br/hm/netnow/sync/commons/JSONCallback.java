package br.hm.netnow.sync.commons;

import org.json.JSONException;

public interface JSONCallback {

    void processRow(Object json, int rowNum) throws JSONException;


}
