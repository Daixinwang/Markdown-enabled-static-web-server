package org.example.utils;

import java.util.Arrays;

public class BytesUtilTest {


    public void IndexOfTest() {

    }

    public static void main(String[] args) {
        byte[] data = new byte[]{1, 2, 2, 3, 4, 5, 6, 6, 7, 2, 3};
        byte[] target = new byte[]{2, 3};
        System.out.println(BytesUtil.indexOf(data, target));
        System.out.println(BytesUtil.indexOf(data, target, 1));
        System.out.println(BytesUtil.indexOf(data, target, 3));
        byte[][] split = BytesUtil.split(data, target);
        for (byte[] cell : split) {
            System.out.println(Arrays.toString(cell));
        }

        target = new byte[]{6, 6, 7};
        System.out.println(BytesUtil.indexOf(data, target));
        System.out.println(BytesUtil.indexOf(data, target, 1));
        System.out.println(BytesUtil.indexOf(data, target, 3));
        split = BytesUtil.split(data, target);
        for (byte[] cell : split) {
            System.out.println(Arrays.toString(cell));
        }
    }
}
