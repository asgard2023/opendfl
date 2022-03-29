package org.ccs.opendfl.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum LangType {
    ZH(LangCodes.ZH, "中文", true),
    TW(LangCodes.TW, "繁体", false),
    JA(LangCodes.JA, "日语", false),
    EN(LangCodes.EN, "英语", false);
    public static final List<String> LANG_TYPES= Arrays.asList(LangType.values()).stream().map(t->t.code).collect(Collectors.toList());
    public static final String NONE_LANG="noneLang";//表示整个数据不做国际化，即不显示
    static Logger LOGGER = LoggerFactory.getLogger(LangType .class);
    public String code;
    public String name;
    public boolean isDefault;

    LangType(String code, String name, boolean isDefault) {
        this.code =code;
        this.name = name;
        this.isDefault=isDefault;
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
        LOGGER.warn("----setLang lang={} invalid, allow(zh,en,tw)", code);
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
        Locale locale=null;
        if(ZH==langType){
            locale= new Locale("zh", "CN");
        }
        else if(TW==langType){
            locale=new Locale("zh", "TW");
        }
        else if(EN==langType){
            locale=new Locale("en", "US");
        }
        else if(JA==langType){
            locale=Locale.JAPAN;
        }
        else{
            locale= new Locale("zh", "CN");
        }
        return locale;
    }
}
