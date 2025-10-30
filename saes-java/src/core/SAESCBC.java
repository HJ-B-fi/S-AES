package core;

public final class SAESCBC {
    private SAESCBC(){}

    public static int[] encryptCBC(int[] plainBlocks, int key16, int iv16){
        int[] out = new int[plainBlocks.length];
        int prev = iv16 & 0xFFFF;
        for (int i = 0; i < plainBlocks.length; i++) {
            int x = (plainBlocks[i] ^ prev) & 0xFFFF;
            int c = SAES.encryptBlock(x, key16);
            out[i] = c;
            prev = c;
        }
        return out;
    }

    public static int[] decryptCBC(int[] cipherBlocks, int key16, int iv16){
        int[] out = new int[cipherBlocks.length];
        int prev = iv16 & 0xFFFF;
        for (int i = 0; i < cipherBlocks.length; i++) {
            int p = SAES.decryptBlock(cipherBlocks[i], key16) ^ prev;
            out[i] = p & 0xFFFF;
            prev = cipherBlocks[i];
        }
        return out;
    }
}
