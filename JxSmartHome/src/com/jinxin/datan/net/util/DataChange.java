package com.jinxin.datan.net.util;


import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import com.jinxin.jxsmarthome.util.Logger;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class DataChange {
    public static byte[] IntToBytes(int object) {
        byte[] bytes = null;
        ByteArrayOutputStream bas = null;
        DataOutputStream dos = null;
        try {
            bas = new ByteArrayOutputStream();
            dos = new DataOutputStream(bas);
            dos.writeInt(object);

            dos.flush();
            bytes = bas.toByteArray();

        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {

                if (bas != null) {
                    bas.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }
        }
        return bytes;

    }

    public static byte[] ShortToBytes(short object) {
        byte[] bytes = null;
        ByteArrayOutputStream bas = null;
        DataOutputStream dos = null;
        try {
            bas = new ByteArrayOutputStream();
            dos = new DataOutputStream(bas);
            dos.writeShort(object);

            dos.flush();
            bytes = bas.toByteArray();

        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {

                if (bas != null) {
                    bas.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }
        }
        return bytes;

    }

    public static int parseShort(byte[] bytes) {
        short object = 0;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream bis = new DataInputStream(bais);
        try {
            object = bis.readShort();
            bis.close();
            bais.close();
        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }

        }

        return object;
    }

    public static int parseInt(byte[] bytes) {
        int object = 0;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream bis = new DataInputStream(bais);
        try {
            object = bis.readInt();
            bis.close();
            bais.close();
        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }

        }

        return object;
    }

    public static String unicodeBytesToString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        try {
            for (int j = 0; j < bytes.length;) {
                int l = bytes[j++];
                int h = bytes[j++];
                char c = (char) ((l & 0xff) | ((h << 8) & 0xff00));
                buffer.append(c);
            }
        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        }
        return buffer.toString();
    }

    public static byte[] toUTF8Bytes(String str) {
        byte[] bytes = null;
        ByteArrayOutputStream bas = null;
        DataOutputStream dos = null;
        try {
            bas = new ByteArrayOutputStream();
            dos = new DataOutputStream(bas);
            dos.writeUTF(str);
            dos.flush();
            bytes = bas.toByteArray();

        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {

                if (bas != null) {
                    bas.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }
        }
        return bytes;

    }

    public static byte[] toUnicodeBytes(String str) {
        byte[] bytes = null;
        ByteArrayOutputStream bas = null;
        DataOutputStream dos = null;
        try {
            bas = new ByteArrayOutputStream();
            dos = new DataOutputStream(bas);
            dos.writeChars(str);
            dos.flush();
            bytes = bas.toByteArray();

        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            try {

                if (bas != null) {
                    bas.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (Exception ex) {
            	Logger.error("DataChange", ex.toString());
            }
        }
        return bytes;

    }

    /**
     * 把字段编码成web可识别的字符串
     * @param sb
     * @param name
     * @param value
     */
    public static void encodeURL(StringBuffer sb,String name, String value) {
        try {
            sb.append(URLEncoder.encode(encodeUTF8(name), "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode(encodeUTF8(value), "UTF-8"));
     } catch (UnsupportedEncodingException ex) {
           throw new RuntimeException("Broken VM does not support UTF-8");
     }
     }
    /**
     * 字符串转UTF8
     * @return
     */
    public static String encodeUTF8(String str){
        String _str = str;
        BytesEncodingDetector detector = new BytesEncodingDetector();    
        int encode = detector.detectEncoding(_str.getBytes());
//        return new String(data, BytesEncodingDetector.javaname[encode]);
         try {
            _str = new String(_str.getBytes(BytesEncodingDetector.javaname[encode]),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            // TODO Auto-generated catch block
        	Logger.error("DataChange", ex.toString());
            Logger.error("转utf-8 =", "转换失败");
        }
         return _str;
    }
    public static void saveStrDB(Context context, String key, String value) {
            SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
    }

    public static String loadStrFromDB(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
        // String value = sp.getString(key, "0");
        String value = sp.getString(key, "");
        // return Integer.parseInt(value);
        return value;

    }
    
///////////////////加密方式/////////////////////////////////
    private static String HASH_KEY = "xn_ln";  //NOSONAR
    private static final String HASH_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-=+";
    private static Random rdm = new Random();
    public static String encodeRandomHash(String s) {
        int idx = rdm.nextInt(65);
        int indent = idx % 8;
        char ch = HASH_CHARS.charAt(idx);
        String mdkey = md5(HASH_KEY + ch);
        mdkey = mdkey.substring(indent, indent + indent + 8);
        s = Base64.encode(s.getBytes());
        StringBuilder sb = new StringBuilder();
        int i = 0, j = 0, k = 0, cnt = s.length();
        for (i = 0; i < cnt; i++) {
            k = (k == mdkey.length()) ? 0 : k;
            j = (idx + HASH_CHARS.indexOf(s.charAt(i)) + mdkey.codePointAt(k++)) % 64;
            sb.append(HASH_CHARS.charAt(j));
        }
        return ch + sb.toString();
    }
    public static String md5(String s) {
        MD5 digest;
        String md5Info = "";
        try {
            digest = new MD5(null);
            byte[] data = s.getBytes("UTF-8");
            digest.update(data);
            byte[] dgst = digest.doFinal();
            md5Info = MD5.toHex(dgst);
        } catch (Exception ex) {
        	Logger.error("DataChange", ex.toString());
        } finally {
            digest = null;
        }
        return md5Info;

    }
}
