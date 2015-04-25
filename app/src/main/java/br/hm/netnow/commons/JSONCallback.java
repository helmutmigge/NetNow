package br.hm.netnow.commons;

import org.json.JSONException;

import java.util.List;

public interface JSONCallback<E> {

    void processRow(Object json, int rowNum) throws JSONException;


}
