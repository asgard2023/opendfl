package org.ccs.opendfl.core.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommUtils {
    private CommUtils(){

    }
    public static Object nvl(Object ...objects ){
        for(Object obj:objects){
            if(obj!=null){
                return obj;
            }
        }
        return null;
    }

    /**
     * 取数据前maxLength位
     * @param str
     * @param maxLength
     * @return
     */
    public static String getStringLimit(String str, int maxLength) {
        return str != null && str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
    public static String getStringFirst(String str, String split){
        if(str==null) {
            return null;
        }
        int idx=str.indexOf(split);
        if (idx > 0) {
            return str.substring(0, idx);
        }
        return str;
    }

    public static String concat(Collection list, String splitChar){
        if(list==null){
            return null;
        }
        splitChar=splitChar!=null?splitChar:",";

        String str="";
        for(Object obj:list){
            str+=obj+splitChar;
        }

        str=str.trim();
        if(str.endsWith(splitChar)){
            str=str.substring(0, str.length()-splitChar.length());
        }
        return str;
    }

    public static String concat(String splitChar, Object ... objs){
        if(objs==null){
            return "";
        }

        String str="";
        for(Object obj:objs){
            if(obj==null){
                continue;
            }
            str=str+obj+splitChar;
        }
        if(str.endsWith(splitChar)){
            str=str.substring(0, str.length()-splitChar.length());
        }
        return str;
    }

    public static String concat(Object[] list, String splitChar){
        if(list==null){
            return null;
        }
        List<Object> list2= Arrays.asList(list);
        return concat(list2, splitChar);
    }

    /**
     * 去除尾部逗号
     * @param str
     * @return
     */
    public static String removeEndComma(String str){
        if(str.endsWith(",")){
            str=str.substring(0, str.length()-1);
        }
        return str;
    }

    /**
     * 去除开头逗号
     * @param str
     * @return
     */
    public static String removeStartComma(String str){
        if(str.startsWith(",")){
            str=str.substring(1);
        }
        return str;
    }

    public static String appendComma(String str){
        if(str.endsWith(",")){
            return str;
        }
        return str+",";
    }
}
