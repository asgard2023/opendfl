package org.ccs.opendfl.core.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class CommUtils {
    private CommUtils(){

    }
    /**
     * 以ch字符开头
     * @return true/false
     */
    public static boolean startWithChar(String str, char ch){
        if(str==null||str.length()<1){
            return false;
        }
        return str.charAt(0)==ch;
    }

    /**
     * 以ch字符结尾
     * @return true/false
     */
    public static boolean endWithChar(String str, char ch){
        if(str==null||str.length()<1){
            return false;
        }
        return str.charAt(str.length()-1)==ch;
    }
    public static String appendUrl(String url, String path){
        if(url==null){
            return null;
        }
        if(path==null){
            path="";
        }
        if(CommUtils.endWithChar(url, '/') && CommUtils.startWithChar(path, '/')){//相当于startsWith
            return url+path.substring(1);
        }
        else if(CommUtils.endWithChar(url, '/') || CommUtils.startWithChar(path, '/')){//相当于startsWith
            return url+path;
        }

        return url+"/"+path;
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

        StringBuilder sb = new StringBuilder();
        for(Object obj:list){
            sb.append(obj+splitChar);
        }

        String str=sb.toString();
        if(str.endsWith(splitChar)){
            str=str.substring(0, str.length()-splitChar.length());
        }
        return str;
    }

    public static String concat(String splitChar, Object ... objs){
        if(objs==null){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(Object obj:objs){
            if(obj==null){
                continue;
            }
            sb.append(obj+splitChar);
        }
        String str=sb.toString();
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
