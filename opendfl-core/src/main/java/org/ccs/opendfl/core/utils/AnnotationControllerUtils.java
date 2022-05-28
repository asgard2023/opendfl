package org.ccs.opendfl.core.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
import org.ccs.opendfl.core.limitfrequency.Frequency3;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestShowVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 注解扫描工具类
 *
 * @author chenjh
 */
@Slf4j
public class AnnotationControllerUtils {
    private AnnotationControllerUtils() {

    }

    /**
     * 缓存10分钟，以免反复调用
     */
    public static final Cache<String, List<RequestVo>> pkgRequestCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10).build();

    public static List<RequestVo> getControllerRequests(String packageName) {
        List<RequestVo> list=pkgRequestCache.getIfPresent(packageName);
        if(list==null){
            list=getControllerRequestsRead(packageName);
            pkgRequestCache.put(packageName, list);
        }
        return list;
    }
    /**
     * 找出Controller下的所有注解，以及频率限制、分布式锁配置参数
     *
     * @author chenjh
     * 异常对象:@param packageName
     * 异常对象:@return
     */
    public static List<RequestVo> getControllerRequestsRead(String packageName) {
        //第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        //是否循环迭代
        boolean recursive = true;
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的文件
        Enumeration<URL> dirs;
        try {
            //读取指定package下的所有class
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //判断是否以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    AnnotationClassUtils.findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else {
                    findAndAddClassesInJar(packageName, classes, recursive, url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestVo requestVo;
        List<RequestVo> requestList = new ArrayList<>();
        //循环获取所有的类
        for (Class<?> clazz : classes) {
            //获取类的所有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                requestVo = toRequest(method, clazz);
                //未取到接口uri忽略
                if (StringUtils.isBlank(requestVo.getRequestUri())) {
                    continue;
                }
                requestList.add(requestVo);
            }
        }
        return requestList;
    }

    /**
     * 从jar包中找packageName对应的所有controller接口
     *
     * @param packageName 包名
     * @param classes     找到要加入的集合
     * @param recursive   是否递归
     * @param url         URL
     */
    private static void findAndAddClassesInJar(String packageName, List<Class<?>> classes, boolean recursive, URL url) {
        List<Class<?>> list = AnnotationClassUtils.getClassFromJars(new URL[]{url}, packageName, recursive);
        StringBuilder sb = new StringBuilder();
        for (Class<?> clazz : list) {
            if (isController(clazz.getAnnotations(), sb)) {
                classes.add(clazz);
            }
        }
    }

    /**
     * 检查是否是controller类
     *
     * @param classAnnotations 类注解
     * @param basePath         controller的路径
     * @return
     */
    private static boolean isController(Annotation[] classAnnotations, StringBuilder basePath) {
        boolean isController = false;
        for (Annotation annotation : classAnnotations) {
            if (annotation instanceof RequestMapping) {
                RequestMapping getMapping = (RequestMapping) annotation;
                basePath.append(CommUtils.concat(getMapping.value(), ","));
            }
            if (annotation instanceof RestController || annotation instanceof Controller) {
                isController = true;
            }
        }
        return isController;
    }


    /**
     * 获取controller上的方法注解情况
     *
     * @param method controller的接口方法
     * @param c      controller类
     * @return
     */
    private static RequestVo toRequest(Method method, Class<?> c) {
        RequestVo requestVo = new RequestShowVo();
        RequestShowVo requestShowVo = (RequestShowVo) requestVo;
        Annotation[] classAnnotations = c.getAnnotations();
        StringBuilder basePathSb = new StringBuilder();
        boolean isController = isController(classAnnotations, basePathSb);
        if (!isController) {
            return requestVo;
        }
        String basePath = basePathSb.toString();
        Annotation[] methodAnnotations = method.getAnnotations();
        String uri = null;
        String methodType = null;
        StringBuilder limitTypes = new StringBuilder();

        List<Annotation> annotationList = Arrays.stream(methodAnnotations)
                .sorted((o1, o2) -> o1.annotationType().getSimpleName().compareTo(o2.annotationType().getSimpleName())).collect(Collectors.toList());

        List<String> annotations = new ArrayList<>();
        List<FrequencyVo> frequencyVoList = new ArrayList<>();
        List<RequestLockVo> requestLockVos = new ArrayList<>();
        List<Annotation> otherAnnotations = new ArrayList<>();
        StringBuilder methodTypeSb = new StringBuilder();
        StringBuilder attrNameSb = new StringBuilder();

        for (Annotation annotation : annotationList) {
            String annotationUri = getMethodUri(annotation, methodTypeSb);
            methodType = methodTypeSb.toString();
            if (annotationUri == null) {
                FrequencyVo frequencyVo = toLimitFrequency(annotation, frequencyVoList, limitTypes, attrNameSb);
                RequestLockVo requestLockVo = null;
                if (frequencyVo == null) {
                    requestLockVo = toLimitLock(annotation, requestLockVos, limitTypes, attrNameSb);
                }
                if (frequencyVo == null && requestLockVo == null) {
                    otherAnnotations.add(annotation);
                }
            }

            if (annotationUri != null) {
                uri = annotationUri;
            }
            annotations.add(annotation.annotationType().getSimpleName());
        }
        String attrName = attrNameSb.toString();
        requestVo.setMethod(methodType);
        requestVo.setPkg(c.getPackage().getName());
        requestVo.setMethodName(method.getName());
        requestVo.setBeanName(c.getSimpleName());
        requestVo.setAnnotations(CommUtils.concat(annotations, ","));

        requestShowVo.setLimitFrequencys(frequencyVoList);
        requestShowVo.setLocks(requestLockVos);
        String limitTypeStr = CommUtils.removeEndComma(limitTypes.toString());
        requestShowVo.setLimitTypes(limitTypeStr);
        requestShowVo.setAttrName(attrName);
        requestShowVo.setOtherInfo(getAnnotationJson(otherAnnotations));
        if (uri != null) {
            requestVo.setRequestUri(CommUtils.appendUrl(basePath, uri));
        }
        return requestVo;
    }

    /**
     * 支持从注解Annotation获取所有的属性值
     *
     * @param annotations
     * @return String
     * @author chenjh
     */
    public static String getAnnotationJson(List<Annotation> annotations) {
        if (CollectionUtils.isEmpty(annotations)) {
            return null;
        }

        List<AnnotationInfoVo> infoVos = new ArrayList<>();
        for (Annotation annotation : annotations) {
            infoVos.add(toAnnotationInfoVo(annotation));
        }
        return JSON.toJSONString(infoVos);
    }

    /**
     * 支持从注解Annotation获取所有的属性值
     *
     * @param annotation
     * @return AnnotationInfoVo
     * @author chenjh
     */
    public static AnnotationInfoVo toAnnotationInfoVo(Annotation annotation) {
        AnnotationInfoVo info = new AnnotationInfoVo();
        info.setType(annotation.annotationType().getSimpleName());
        Method[] methods = annotation.getClass().getDeclaredMethods();
        Map<String, Object> paramMap = new HashMap<>();
        String ignoreMethods = ",hashCode,equals,toString,annotationType,";
        for (Method method : methods) {
            String name = method.getName();
            if (ignoreMethods.contains("," + name + ",")) {
                continue;
            }
            Object value = null;
            try {
                value = method.invoke(annotation);
            } catch (Exception e) {
                value = e.getMessage();
            }
            paramMap.put(name, value);
        }
        info.setParams(paramMap);
        return info;
    }

    /**
     * 获取接口注解RequestMapping,GetMapping等
     *
     * @param annotation   Annotation
     * @param methodTypeSb StringBuilder
     * @return 方法uri
     */
    private static String getMethodUri(Annotation annotation, StringBuilder methodTypeSb) {
        String annotationUri = null;
        if (annotation instanceof GetMapping) {
            GetMapping getMapping = (GetMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
        } else if (annotation instanceof RequestMapping) {
            RequestMapping getMapping = (RequestMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
            for (RequestMethod reqMethod : getMapping.method()) {
                methodTypeSb.append(reqMethod.name()).append(",");
            }
        } else if (annotation instanceof PostMapping) {
            PostMapping getMapping = (PostMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
        } else if (annotation instanceof PutMapping) {
            PutMapping getMapping = (PutMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
        } else if (annotation instanceof DeleteMapping) {
            DeleteMapping getMapping = (DeleteMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
        } else if (annotation instanceof PatchMapping) {
            PatchMapping getMapping = (PatchMapping) annotation;
            annotationUri = CommUtils.concat(getMapping.value(), ",");
        }
        if (methodTypeSb.length() == 0 && annotationUri != null) {
            methodTypeSb.append(annotation.annotationType().getSimpleName().replace("Mapping", "").toUpperCase());
        }
        return annotationUri;
    }

    /**
     * 方法的Frequency注解处理
     *
     * @param annotation   Annotation
     * @param frequencyVos List<FrequencyVo>
     * @param limitTypes   StringBuilder
     * @return
     */
    private static FrequencyVo toLimitFrequency(Annotation annotation, List<FrequencyVo> frequencyVos, StringBuilder limitTypes, StringBuilder attrNameSb) {
        FrequencyVo frequencyVo = null;
        if (annotation instanceof Frequency) {
            frequencyVo = FrequencyVo.toFrequencyVo((Frequency) annotation, FrequencyVo.newInstance());
        } else if (annotation instanceof Frequency2) {
            frequencyVo = FrequencyVo.toFrequencyVo((Frequency2) annotation, FrequencyVo.newInstance());
        } else if (annotation instanceof Frequency3) {
            frequencyVo = FrequencyVo.toFrequencyVo((Frequency3) annotation, FrequencyVo.newInstance());
        }
        if (frequencyVo != null) {
            frequencyVos.add(frequencyVo);
            limitTypes.append(frequencyVo.getLimitType()).append(",");
            if (frequencyVo.getAttrName() != null && !attrNameSb.toString().contains(frequencyVo.getAttrName())) {
                attrNameSb.append(frequencyVo.getAttrName());
            }
        }
        return frequencyVo;
    }

    /**
     * 方法的RequestLock注解处理
     *
     * @param annotation     Annotation
     * @param requestLockVos List<RequestLockVo>
     * @param limitTypes     StringBuilder
     * @return
     */
    private static RequestLockVo toLimitLock(Annotation annotation, List<RequestLockVo> requestLockVos, StringBuilder limitTypes, StringBuilder attrNameSb) {
        if (annotation instanceof RequestLock) {
            RequestLock requestLock = (RequestLock) annotation;
            RequestLockVo requestLockVo = RequestLockVo.toLockVo(requestLock);
            requestLockVos.add(requestLockVo);
            if (requestLockVo.getAttrName() != null && !attrNameSb.toString().contains(requestLockVo.getAttrName())) {
                attrNameSb.append(requestLockVo.getAttrName());
            }
            limitTypes.append("lock");
            return requestLockVo;
        }
        return null;
    }

}


@Data
class AnnotationInfoVo {
    private String type;
    private Map<String, Object> params;
}