package core;

public final class SAES {
    private SAES(){}

    // 4-bit S-Box（教材常见版本）
    private static final int[] S = {
            0x9,0x4,0xA,0xB, 0xD,0x1,0x8,0x5, 0x6,0x2,0x0,0x3, 0xC,0xE,0xF,0x7
    };
    private static final int[] INV_S = new int[16];
    static { for(int i=0;i<16;i++) INV_S[S[i]] = i; }

    // GF(2^4) 乘法，模 x^4 + x + 1
    private static int gf4Mul(int a, int b){
        a &= 0xF; b &= 0xF;
        int p = 0;
        for(int i=0;i<4;i++) if(((b>>i)&1)==1) p ^= (a<<i);
        int mod = 0x13;
        for(int i=7;i>=4;i--) if(((p>>i)&1)==1) p ^= (mod<<(i-4));
        return p & 0xF;
    }

    private static int subNib(int x){
        return (S[(x>>12)&0xF]<<12) | (S[(x>>8)&0xF]<<8) | (S[(x>>4)&0xF]<<4) | S[x&0xF];
    }
    private static int invSubNib(int x){
        return (INV_S[(x>>12)&0xF]<<12) | (INV_S[(x>>8)&0xF]<<8) | (INV_S[(x>>4)&0xF]<<4) | INV_S[x&0xF];
    }

    private static int shiftRows(int x){
        int s0=(x>>12)&0xF, s1=(x>>8)&0xF, s2=(x>>4)&0xF, s3=x&0xF;
        return (s0<<12)|(s3<<8)|(s2<<4)|s1;
    }
    private static int invShiftRows(int x){
        int s0=(x>>12)&0xF, s1=(x>>8)&0xF, s2=(x>>4)&0xF, s3=x&0xF;
        return (s0<<12)|(s1<<8)|(s2<<4)|s3;
    }

    // MixColumns 乘 [[1,4],[4,1]]
    private static int mixColumns(int x){
        int s0=(x>>12)&0xF, s1=(x>>8)&0xF, s2=(x>>4)&0xF, s3=x&0xF;
        int c0r0 = s0 ^ gf4Mul(4,s1);
        int c0r1 = gf4Mul(4,s0) ^ s1;
        int c1r0 = s2 ^ gf4Mul(4,s3);
        int c1r1 = gf4Mul(4,s2) ^ s3;
        return (c0r0<<12)|(c0r1<<8)|(c1r0<<4)|c1r1;
    }
    // InvMixColumns 乘 [[9,2],[2,9]]
    private static int invMixColumns(int x){
        int s0=(x>>12)&0xF, s1=(x>>8)&0xF, s2=(x>>4)&0xF, s3=x&0xF;
        int c0r0 = gf4Mul(9,s0) ^ gf4Mul(2,s1);
        int c0r1 = gf4Mul(2,s0) ^ gf4Mul(9,s1);
        int c1r0 = gf4Mul(9,s2) ^ gf4Mul(2,s3);
        int c1r1 = gf4Mul(2,s2) ^ gf4Mul(9,s3);
        return (c0r0<<12)|(c0r1<<8)|(c1r0<<4)|c1r1;
    }

    // KeySchedule（Rcon 常用取值）
    private static final int RCON1 = 0x80, RCON2 = 0x30;
    private static int rotNib8(int w){ return ((w<<4)&0xF0) | ((w>>4)&0x0F); }
    private static int subNib8(int w){ return (S[(w>>4)&0xF]<<4) | S[w&0xF]; }

    // 返回 {K0, K1, K2}
    public static int[] expandKey(int key16){
        key16 &= 0xFFFF;
        int w0=(key16>>8)&0xFF, w1=key16&0xFF;
        int w2 = w0 ^ subNib8(rotNib8(w1)) ^ RCON1;
        int w3 = w2 ^ w1;
        int w4 = w2 ^ subNib8(rotNib8(w3)) ^ RCON2;
        int w5 = w4 ^ w3;
        int K0=(w0<<8)|w1, K1=(w2<<8)|w3, K2=(w4<<8)|w5;
        return new int[]{K0&0xFFFF, K1&0xFFFF, K2&0xFFFF};
    }

    public static int encryptBlock(int p, int k){
        int[] K = expandKey(k);
        int s = p ^ K[0];
        s = subNib(s);
        s = shiftRows(s);
        s = mixColumns(s);
        s ^= K[1];
        s = subNib(s);
        s = shiftRows(s);
        s ^= K[2];
        return s & 0xFFFF;
    }

    public static int decryptBlock(int c, int k){
        int[] K = expandKey(k);
        int s = c ^ K[2];
        s = invShiftRows(s);
        s = invSubNib(s);
        s ^= K[1];
        s = invMixColumns(s);
        s = invShiftRows(s);
        s = invSubNib(s);
        s ^= K[0];
        return s & 0xFFFF;
    }

    // double
    public static int encryptBlock2Key(int p, int k1, int k2){ return encryptBlock(encryptBlock(p,k1),k2); }
    public static int decryptBlock2Key(int c, int k1, int k2){ return decryptBlock(decryptBlock(c,k2),k1); }

    // 2Key-EDE
    public static int encryptBlock3EDE(int p, int k1, int k2){ return encryptBlock(decryptBlock(encryptBlock(p,k1),k2),k1); }
    public static int decryptBlock3EDE(int c, int k1, int k2){ return decryptBlock(encryptBlock(decryptBlock(c,k1),k2),k1); }

    // 3Key-EEE
    public static int encryptBlock3EEE(int p, int k1, int k2, int k3){
        return encryptBlock(encryptBlock(encryptBlock(p,k1),k2),k3);
    }
    public static int decryptBlock3EEE(int c, int k1, int k2, int k3){
        return decryptBlock(decryptBlock(decryptBlock(c,k3),k2),k1);
    }
}
