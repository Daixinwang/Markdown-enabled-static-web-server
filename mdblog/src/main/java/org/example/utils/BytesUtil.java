package org.example.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BytesUtil {

    public static int indexOf(byte[] haystack, byte[] needle) {
        return indexOf(haystack, needle, 0);
    }

    public static int indexOf(byte[] haystack, byte[] needle, int start) {
        if (needle == null || needle.length == 0)
            return 0;

        if (haystack == null || needle.length > haystack.length - start)
            return -1;

        int[] failure = new int[needle.length];
        int n = needle.length;
        failure[0] = -1;
        for (int j = 1; j < n; j++) {
            int i = failure[j - 1];
            while ((needle[j] != needle[i + 1]) && i >= 0)
                i = failure[i];
            if (needle[j] == needle[i + 1])
                failure[j] = i + 1;
            else
                failure[j] = -1;
        }

        int i = start, j = 0;
        int haystackLen = haystack.length;
        int needleLen = needle.length;
        while (i < haystackLen && j < needleLen) {
            if (haystack[i] == needle[j]) {
                i++;
                j++;
            }
            else if (j == 0)
                i++;
            else
                j = failure[j - 1] + 1;
        }
        return ((j == needleLen) ? (i - needleLen) : -1);
    }

    public static boolean contains(byte[] data, byte[] target) {
        return indexOf(data, target) > -1;
    }

    public static byte[][] split(byte[] data, byte[] boundary) {
        List<byte[]> res = new ArrayList<>();

        int start = 0, end = indexOf(data, boundary);
        while (end != -1) {
            if (end > start) {
                res.add(Arrays.copyOfRange(data, start, end));
            }
            start = end + boundary.length;
            if (start >= data.length) break;
            end = indexOf(data, boundary, start);
        }
        if (start < data.length) {
            res.add(Arrays.copyOfRange(data, start, data.length));
        }
        int size = res.size();
        byte[][] arrRes = new byte[size][];
        for (int i = 0; i < size; i++) {
            arrRes[i] = res.get(i);
        }
        return arrRes;
    }
}
