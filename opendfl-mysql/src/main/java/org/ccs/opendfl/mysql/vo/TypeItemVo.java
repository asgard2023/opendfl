package org.ccs.opendfl.mysql.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeItemVo implements Serializable {
    private Object id;
    private String name;
    private String color;
}
