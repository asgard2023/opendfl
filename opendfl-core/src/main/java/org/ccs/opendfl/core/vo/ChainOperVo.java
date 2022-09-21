package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.strategy.black.BlackStrategy;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;

/**
 * 频率限制责任链操作
 *
 * @author chenjh
 */
@Data
public class ChainOperVo {
    private Integer pos = 0;
    private BlackStrategy blackStrategy;
    private WhiteStrategy whiteStrategy;
    private boolean isFail=false;

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public void clearChain() {
        this.pos = 0;
    }
}
