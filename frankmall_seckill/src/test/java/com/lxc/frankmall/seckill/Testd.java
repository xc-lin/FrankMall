package com.lxc.frankmall.seckill;

import org.junit.jupiter.api.Test;

/**
 * @author Frank_lin
 * @date 2022/7/11
 */
public class Testd {
    @Test
     public void a(){
        Object k = true?new Integer(1):new Double(2.0);
        System.out.println(k.getClass());
        System.out.println(k);
    }
}
