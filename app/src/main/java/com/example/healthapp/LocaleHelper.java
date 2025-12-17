package com.example.healthapp;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;
public class LocaleHelper {


    public static void applyLanguage(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

}
