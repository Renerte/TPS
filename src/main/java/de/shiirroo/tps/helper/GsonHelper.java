package de.shiirroo.tps.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();


}
