package org.ccs.opendfl.mysql.basedata;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteItemBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhiteItemPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "test")
@Slf4j
class IDflBlackWhiteItemBizTest {
    @Autowired
    private IDflBlackWhiteItemBiz dflBlackWhiteItemBiz;

    @Test
    void updateDflBlackWhiteItem() {
        DflBlackWhiteItemPo update = new DflBlackWhiteItemPo();
        update.setId(5);
        update.setData("172.0.0.100");
        this.dflBlackWhiteItemBiz.updateDflBlackWhiteItem(update);
    }
}
