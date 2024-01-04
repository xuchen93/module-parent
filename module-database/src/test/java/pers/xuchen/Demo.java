package pers.xuchen;

import java.math.BigInteger;

/**
 * @author Edwin
 * @date 2020/6/23
 */
public class Demo {
    public static void main(String[] args) {
        BigInteger auth = new BigInteger("0");
        auth = auth.setBit(1);
        auth = auth.setBit(3);
        auth = auth.setBit(3);
        auth = auth.setBit(5);
        auth = auth.setBit(7);
        auth = auth.setBit(8);
        auth = auth.setBit(98765);
        System.out.println(auth);
        for (int i = 0; i < 10; i++) {
            boolean r = auth.testBit(i);
            System.out.println(i + "---是否有权限---" + r);
        }
        System.out.println(98777 + "---是否有权限---" + auth.testBit(98777));
        System.out.println(98765 + "---是否有权限---" + auth.testBit(98765));

    }
}
