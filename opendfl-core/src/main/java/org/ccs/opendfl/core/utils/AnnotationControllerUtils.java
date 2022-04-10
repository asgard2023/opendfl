package org.ccs.opendfl.core.utils;

import com.alibaba.fastjson.JSON;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
import org.ccs.opendfl.core.limitfrequency.Frequency3;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestShowVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注解扫描工具类
 *
 * @author chenjh
 */
public class AnnotationControllerUtils {
    private AnnotationControllerUtils() {

    }

    /**
     * 找出Controller下的所有注解，以及频率限制、分布式锁配置参数
     *
     * @author chenjh
     * 异常对象:@param packageName
     * 异常对象:@return
     */
    public static List<RequestVo> getControllerRequests(String packageName) {
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
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    AnnotationClassUtils.findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
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
     * 检查是否是controller类
     *
     * @param classAnnotations
     * @param basePath
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

        List<Annotation> annotationList = Arrays.stream(methodAnnotations).sorted(new Comparator<Annotation>() {
            @Override
            public int compare(Annotation o1, Annotation o2) {
                return o1.annotationType().getSimpleName().compareTo(o2.annotationType().getSimpleName());
            }
        }).collect(Collectors.toList());

        List<String> annotations = new ArrayList<>();
        List<FrequencyVo> frequencyVoList = new ArrayList<>();
        List<RequestLockVo> requestLockVos = new ArrayList<>();
        List<Annotation> otherAnnotations = new ArrayList<>();
        StringBuilder methodTypeSb = new StringBuilder();
        String attrName = null;
        for (Annotation annotation : annotationList) {
            String annotationUri = getMethodUri(annotation, methodTypeSb);
            methodType = methodTypeSb.toString();
            if (annotationUri == null) {
                FrequencyVo frequencyVo = toLimitFrequency(annotation, frequencyVoList, limitTypes);
                RequestLockVo requestLockVo = null;
                if (frequencyVo == null) {
                    requestLockVo = toLimitLock(annotation, requestLockVos, limitTypes);
                } else if (attrName == null) {
                    attrName = frequencyVo.getAttrName();
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
        requestVo.setMethod(methodType);
        requestVo.setMethodName(method.getName());
        requestVo.setBeanName(c.getSimpleName());
        requestVo.setAnnotations(CommUtils.concat(annotations, ","));
        RequestShowVo requestShowVo = (RequestShowVo) requestVo;
        requestShowVo.setLimitFrequencys(frequencyVoList);
        requestShowVo.setLocks(requestLockVos);
        String limitTypeStr = CommUtils.removeEndComma(limitTypes.toString());
        requestShowVo.setLimitTypes(limitTypeStr);
        requestShowVo.setAttrName(attrName);
        requestShowVo.setOtherInfo(JSON.toJSONString(otherAnnotations));
        if (uri != null) {
            requestVo.setRequestUri(CommUtils.appendUrl(basePath, uri));
        }
        return requestVo;
    }

    /**
     * 获取接口注解RequestMapping,GetMapping等
     *
     * @param annotation   Annotation
     * @param methodTypeSb StringBuilder
     * @return
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
                methodTypeSb.append(reqMethod.name() + ",");
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
    private static FrequencyVo toLimitFrequency(Annotation annotation, List<FrequencyVo> frequencyVos, StringBuilder limitTypes) {
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
    private static RequestLockVo toLimitLock(Annotation annotation, List<RequestLockVo> requestLockVos, StringBuilder limitTypes) {
        if (annotation instanceof RequestLock) {
            RequestLock requestLock = (RequestLock) annotation;
            RequestLockVo requestLockVo = RequestLockVo.toLockVo(requestLock);
            requestLockVos.add(requestLockVo);
            limitTypes.append("lock");
            return requestLockVo;
        }
        return null;
    }


}
