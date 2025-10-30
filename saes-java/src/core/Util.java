package core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class Util {
    private Util(){}

    // ---------- 基本工具 ----------
    public static int u16(int x){ return x & 0xFFFF; }
    public static int u8 (int x){ return x & 0xFF; }
    public static int u4 (int x){ return x & 0xF; }

    // ---------- HEX（给密钥/IV 使用；保留） ----------
    public static String toHex16(int x){
        return String.format("%04X", u16(x));
    }
    public static int parseHex16(String hex){
        String h = hex.trim().replace("0x","").replace("0X","");
        if(h.length() > 4) throw new IllegalArgumentException("需要16位(4个hex)：" + hex);
        return Integer.parseInt(h, 16) & 0xFFFF;
    }
    public static int[] parseHex16Blocks(String s){
        String[] parts = s.trim().split("\\s+");
        List<Integer> arr = new ArrayList<>();
        for(String p: parts){
            if(p.isEmpty()) continue;
            arr.add(parseHex16(p));
        }
        return arr.stream().mapToInt(Integer::intValue).toArray();
    }
    public static String joinHex16(int[] blocks){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<blocks.length;i++){
            if(i>0) sb.append(' ');
            sb.append(toHex16(blocks[i]));
        }
        return sb.toString();
    }

    // ---------- ASCII <-> 16-bit 分组 ----------
    public static byte[] asciiBytes(String s){
        return s.getBytes(StandardCharsets.US_ASCII);
    }
    public static String asciiFromBytes(byte[] b){
        return new String(b, StandardCharsets.US_ASCII);
    }
    // 两字节一组(大端)；末尾奇数字节补 0x00
    public static int[] packBytesToU16Blocks(byte[] data){
        List<Integer> blocks = new ArrayList<>();
        for(int i=0;i<data.length;i+=2){
            int hi = (data[i] & 0xFF);
            int lo = (i+1 < data.length) ? (data[i+1] & 0xFF) : 0;
            int blk = ((hi<<8) | lo) & 0xFFFF;
            blocks.add(blk);
        }
        return blocks.stream().mapToInt(Integer::intValue).toArray();
    }
    public static byte[] unpackU16BlocksToBytes(int[] blocks){
        byte[] out = new byte[blocks.length*2];
        for(int i=0;i<blocks.length;i++){
            int v = u16(blocks[i]);
            out[2*i  ] = (byte)((v>>8) & 0xFF);
            out[2*i+1] = (byte)( v     & 0xFF);
        }
        return out;
    }

    // ---------- 二进制 (16-bit) ----------
    public static String toBin16(int x){
        String s = Integer.toBinaryString(u16(x));
        return ("0000000000000000" + s).substring(s.length()); // 左补零到16位
    }
    public static int parseBin16(String bin){
        String b = bin.trim().replace("_", "");
        if(b.length() != 16 || !b.matches("[01]{16}")){
            throw new IllegalArgumentException("需要16位二进制(0/1)： " + bin);
        }
        return Integer.parseInt(b, 2) & 0xFFFF;
    }
    public static int[] parseBin16Blocks(String s){
        String[] parts = s.trim().split("\\s+");
        List<Integer> arr = new ArrayList<>();
        for(String p: parts){
            if(p.isEmpty()) continue;
            arr.add(parseBin16(p));
        }
        return arr.stream().mapToInt(Integer::intValue).toArray();
    }
    public static String joinBin16(int[] blocks){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<blocks.length;i++){
            if(i>0) sb.append(' ');
            sb.append(toBin16(blocks[i]));
        }
        return sb.toString();
    }
}
