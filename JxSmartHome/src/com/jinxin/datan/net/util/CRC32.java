package com.jinxin.datan.net.util;

public class CRC32 {
	private static int[] CRC_32_Tbl = new int[256];
    static {
        int CRC;
        for (int i = 0; i < 256; ++i) {
            CRC = i;
            for (int j = 0; j < 8; ++j) {
                if (0 != (CRC & 1)) {
                    CRC = (CRC >>> 1) ^ 0xEDB88320; // 0xEDB88320就是CRC-32多项表达式的值
                } else {
                    CRC >>>= 1;
                }
            }
            CRC_32_Tbl[i] = CRC;
        }
    }

    public static final int crc32(byte[] data) {
        int crc32 = 0xffffffff;

        int len = data.length;
        for (int i = 0; i < len; ++i) {
            crc32 = (int) (CRC_32_Tbl[(int) ((crc32 ^ data[i]) & 0xff)] ^ (crc32 >>> 8));
        }

        return (int) (crc32 ^ 0xffffffff);
    }
}
