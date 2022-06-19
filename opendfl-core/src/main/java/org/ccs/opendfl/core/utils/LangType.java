package org.ccs.opendfl.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum LangType {
    ZH(LangCodes.ZH, "中文", true, Locale.SIMPLIFIED_CHINESE),
    TW(LangCodes.TW, "繁体", false, Locale.TRADITIONAL_CHINESE),
    JA(LangCodes.JA, "日语", false, Locale.JAPAN),
    EN(LangCodes.EN, "英语", false, Locale.ENGLISH);
    protected static final List<String> LANG_TYPES= Arrays.asList(LangType.values()).stream().map(t->t.code).collect(Collectors.toList());
    public static final String NONE_LANG="noneLang";//表示整个数据不做国际化，即不显示
    static final Logger logger = LoggerFactory.getLogger(LangType .class);
    public final String code;
    public final String descs;
    public final Locale locale;
    public final boolean isDefault;

    LangType(String code, String descs, boolean isDefault, Locale locale) {
        this.code =code;
        this.descs = descs;
        this.isDefault=isDefault;
        this.locale=locale;
    }

    public static LangType parse(String code) {
        if (code == null||code.length()<2) {
            return null;
        }
        LangType[] codes = LangType.values();
        for (LangType cur : codes) {
            if (cur.code.equalsIgnoreCase(code)||cur.code.equalsIgnoreCase(code.substring(0,2))) {
                return cur;
            }
        }
        logger.warn("----setLang lang={} invalid, allow(zh,en,tw)", code);
        return null;
    }

    private static LangType defaultLang=null;
    public static LangType getDefault(){
        if(defaultLang==null){
            LangType[] codes = LangType.values();
            for(LangType langType:codes){
                if(langType.isDefault){
                    defaultLang=langType;
                    break;
                }
            }
        }
        return defaultLang;
    }

    public static Locale getLocale(String lang){
        LangType langType=LangType.parse(lang);
        if(langType==null){
            langType=getDefault();
        }
        return langType.locale;
    }
}
