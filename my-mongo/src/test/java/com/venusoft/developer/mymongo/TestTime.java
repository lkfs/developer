package com.venusoft.developer.mymongo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * TestTime
 *
 * @author : lkfs
 * @date : 2023/9/20 20:38
 */
@Slf4j
public class TestTime {
    long tOneDay = 86400000L;
//    long t1201 = 1701360000000L;
    long t1201 = 1694448000000L;

    @Test
    public void testTime() {
        long start = System.currentTimeMillis();
        log.info("start time:{}", start);
        if (start > t1201) {
            long days = (start - t1201) / tOneDay;
            try {
                Thread.sleep(1000 + 100 * days);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        log.info("end time:{}, span:{}", end, end - start);

    }

}
