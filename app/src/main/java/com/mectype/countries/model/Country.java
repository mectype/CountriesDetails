package com.mectype.countries.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class Country implements Serializable {

    final static String NAME_KEY = "name";
    final static String OFFICIAL_NAME_KEY = "official";
    final static String CAPITAL_KEY = "capital";
    final static String POPULATION_KEY = "population";
    final static String FLAG_KEY = "flags";
    final static String IMAGE_FORMAT = "png";

    final static String REGION_KEY = "region";
    final static String SUB_REGION_KEY = "subregion";
    final static String LANGUAGES_KEY = "languages";
    final static String MAP_KEY = "maps";


    String name;
    String capital;
    int population;
    String flag;
    Bitmap flagBitmap;

    String region;
    String subRegion;
    String[] languages;
    String map;

    public Country(JSONObject jsonObject) {
        try {
            name = Objects.requireNonNull(jsonObject.optJSONObject(NAME_KEY)).optString(OFFICIAL_NAME_KEY);
            JSONArray arr = jsonObject.optJSONArray(CAPITAL_KEY);
            capital = arr != null ? arr.optString(0) : "no capital was stated";
            population = jsonObject.optInt(POPULATION_KEY);
            JSONObject flags = jsonObject.optJSONObject(FLAG_KEY);
            flag = flags != null ? flags.optString(IMAGE_FORMAT) : null;
            region = jsonObject.optString(REGION_KEY);
            subRegion = jsonObject.optString(SUB_REGION_KEY);
            JSONObject lanObj = jsonObject.optJSONObject(LANGUAGES_KEY);
            JSONArray names = lanObj != null ? lanObj.names() : null;
            int len = names != null ? names.length() : 0;
            languages = new String[len];
            for (int i = 0; i < len; i++) {
                languages[i] = lanObj.optString(names.optString(i));
            }
            JSONObject maps = jsonObject.optJSONObject(MAP_KEY);
            map = maps != null ? maps.optString("googleMaps") : null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public int getPopulation() {
        return population;
    }

    public String getFlag() {
        return flag;
    }

    public Bitmap getFlagBitmap() {
        return flagBitmap;
    }

    public String getRegion() {
        return region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public String[] getLanguages() {
        return languages;
    }

    public String getMap() {
        return map;
    }

    public void setFlagBitmap(Bitmap flagBitmap) {
        this.flagBitmap = flagBitmap;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(NAME_KEY, new JSONObject().put(OFFICIAL_NAME_KEY, name));
            jsonObject.put(POPULATION_KEY, population);
            jsonObject.put(CAPITAL_KEY, new JSONArray().put(capital));
            jsonObject.put(FLAG_KEY, new JSONObject().put(IMAGE_FORMAT, flag));
            jsonObject.put(REGION_KEY, region);
            jsonObject.put(SUB_REGION_KEY, subRegion);
            JSONObject languagesArray = new JSONObject();
            for (int i = 0; i < languages.length; i++) {
                languagesArray.put(String.valueOf(i), languages[i]);    // not "loyal" to original json, but we don't really care about the keys of this jsonObject
            }
            jsonObject.put(LANGUAGES_KEY, languagesArray);
            jsonObject.put(MAP_KEY, new JSONObject().put("googleMaps", map));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
