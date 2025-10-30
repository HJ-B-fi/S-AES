package gui;

import core.SAES;
import core.SAESCBC;
import core.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SAESFrame extends JFrame {
    // 占位提示文案
    private static final String INPUT_PLACEHOLDER  = "请输入明文/密文";
    private static final String OUTPUT_PLACEHOLDER = "您的密文/明文结果为";

    // 顶部输入/底部输出
    private final JTextArea inputArea  = new JTextArea(5, 50);
    private final JTextArea outputArea = new JTextArea(6, 50);

    // 占位显示状态
    private boolean inputShowingPlaceholder  = true;
    private boolean outputShowingPlaceholder = true;

    // 密钥与 IV 文本框（支持 16b BIN/HEX，标签已提示）
    private final JTextField key1Field = new JTextField("0000000000000000", 8);
    private final JTextField key2Field = new JTextField("0000000000000000", 8);
    private final JTextField key3Field = new JTextField("0000000000000000", 8);
    private final JTextField ivField   = new JTextField("0000000000000000", 8);

    // 输入/输出解析方式
    private final JRadioButton asciiMode = new JRadioButton("ASCII", true);
    private final JRadioButton binMode   = new JRadioButton("二进制");

    // 工作模式
    private final JComboBox<String> modeCombo = new JComboBox<>(new String[]{
            "单块-ECB", "双重-ECB", "三重-EDE(2Key)", "三重-EEE(3Key)", "CBC(单钥)"
    });

    public SAESFrame(){
        super("S-AES算法（16-bit）");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(2,2));

        // ===== 顶部：输入 + 右侧选项 =====
        JPanel top = new JPanel(new BorderLayout(8,8));
        inputArea.setLineWrap(true);
        // 占位：输入框
        setInputPlaceholder();
        // 焦点监听：自动清空/恢复占位
        inputArea.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (inputShowingPlaceholder) {
                    inputArea.setText("");
                    inputArea.setForeground(Color.BLACK);
                    inputShowingPlaceholder = false;
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (inputArea.getText().trim().isEmpty()) {
                    setInputPlaceholder();
                }
            }
        });
        top.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        ButtonGroup g = new ButtonGroup();
        g.add(asciiMode); g.add(binMode);
        right.add(asciiMode);
        right.add(Box.createVerticalStrut(6));
        right.add(binMode);
        right.add(Box.createVerticalStrut(20));
        JPanel modeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel modeLbl = new JLabel("加密类型：");
        modeRow.add(modeLbl);

        // 设定下拉框不被拉伸
        modeCombo.setPreferredSize(new Dimension(140, modeCombo.getPreferredSize().height));
        modeCombo.setMaximumSize(modeCombo.getPreferredSize());

        modeRow.add(modeCombo);
        modeRow.setAlignmentX(Component.LEFT_ALIGNMENT);  // 整行也靠左
        right.add(modeRow);

        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ===== 中部：表单式（K1/K2/K3/IV 各占一行） =====
        JPanel mid = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4); // 内边距
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;  // 标签列不扩展
        c.gridy = 0;    // 当前行

        Dimension labelW = new Dimension(160, 24); // 标签等宽
        JLabel l1 = new JLabel("K1(16b BIN/HEX):"); l1.setPreferredSize(labelW);
        addFormRow(mid, c, l1, key1Field);

        JLabel l2 = new JLabel("K2(16b BIN/HEX):"); l2.setPreferredSize(labelW);
        addFormRow(mid, c, l2, key2Field);

        JLabel l3 = new JLabel("K3(16b BIN/HEX):"); l3.setPreferredSize(labelW);
        addFormRow(mid, c, l3, key3Field);

        JLabel l4 = new JLabel("IV(16b BIN/HEX, CBC用):"); l4.setPreferredSize(labelW);
        addFormRow(mid, c, l4, ivField);

        add(mid, BorderLayout.CENTER);

        // ===== 底部：按钮 + 输出 =====
        JPanel bottom = new JPanel(new BorderLayout(6,6));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(new JButton(new AbstractAction("加密 ▶"){
            @Override public void actionPerformed(ActionEvent e){ doEncrypt(); }
        }));
        btns.add(new JButton(new AbstractAction("解密 ◀"){
            @Override public void actionPerformed(ActionEvent e){ doDecrypt(); }
        }));
        btns.add(new JButton(new AbstractAction("清空"){
            @Override public void actionPerformed(ActionEvent e){
                setInputPlaceholder();
                setOutputPlaceholder();
            }
        }));
        bottom.add(btns, BorderLayout.NORTH);

        // 输出框：初始显示占位
        outputArea.setEditable(false);
        setOutputPlaceholder();
        bottom.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // 统一设置文本框的首选宽度
        Dimension fieldW = new Dimension(260, 26);
        key1Field.setPreferredSize(fieldW);
        key2Field.setPreferredSize(fieldW);
        key3Field.setPreferredSize(fieldW);
        ivField.setPreferredSize(fieldW);

        setSize(550, 450);
        setLocationRelativeTo(null);
    }

    /** 表单行辅助：一行 = 标签 + 输入框 */
    private static void addFormRow(JPanel panel, GridBagConstraints c, JComponent label, JComponent field){
        // 标签在第0列
        c.gridx = 0; c.weightx = 0;
        panel.add(label, c);
        // 输入框在第1列，水平可拉伸
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
        // 换到下一行
        c.gridy++;
    }

    // ===== 占位控制 =====
    private void setInputPlaceholder(){
        inputArea.setForeground(Color.GRAY);
        inputArea.setText(INPUT_PLACEHOLDER);
        inputShowingPlaceholder = true;
    }
    private void setOutputPlaceholder(){
        outputArea.setForeground(Color.GRAY);
        outputArea.setText(OUTPUT_PLACEHOLDER);
        outputShowingPlaceholder = true;
    }

    // === 密钥/IV：自适应 16b BIN / 4Hex ===
    private int k(String s){
        String t = s.trim();
        // 1) 二进制（允许空格/下划线）
        String b = t.replaceAll("[ _]", "");
        if (b.matches("[01]{16}")) {
            return Integer.parseInt(b, 2) & 0xFFFF;
        }
        // 2) 十六进制（允许空格）
        String h = t.replaceAll("\\s+", "");
        if (h.matches("(?i)[0-9a-f]{4}")) {
            return Integer.parseInt(h, 16) & 0xFFFF;
        }
        throw new IllegalArgumentException("密钥/IV 需要 16 位二进制(0/1) 或 4 个十六进制数字，例如 0000111100010101 或 0F15");
    }

    // === 读取输入块：ASCII 打包 / 二进制 16bit 块 ===
    private int[] readInputAsBlocks(){
        String raw = inputArea.getText();
        if (inputShowingPlaceholder || raw.trim().isEmpty()) {
            return new int[0];
        }
        if(asciiMode.isSelected()){
            byte[] b = Util.asciiBytes(raw);
            return Util.packBytesToU16Blocks(b);
        }else{
            return Util.parseBin16Blocks(raw);
        }
    }

    // === 写输出：解密时在 ASCII 模式可显示文本，其余显示二进制块 ===
    private void writeOutputFromBlocks(int[] blocks, boolean asAscii){
        outputArea.setForeground(Color.BLACK);
        outputShowingPlaceholder = false;
        if(asciiMode.isSelected() && asAscii){
            byte[] b = Util.unpackU16BlocksToBytes(blocks);
            outputArea.setText(Util.asciiFromBytes(b));
        }else{
            outputArea.setText(Util.joinBin16(blocks));
        }
    }

    // 输入是否为空的快速校验
    private boolean ensureInputNotEmpty(){
        if (inputShowingPlaceholder || inputArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入明文/密文后再操作。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    private void doEncrypt(){
        try{
            if (!ensureInputNotEmpty()) return;

            String mode = (String) modeCombo.getSelectedItem();
            int[] in = readInputAsBlocks();
            int k1 = k(key1Field.getText());
            int[] out;

            switch (mode){
                case "单块-ECB":
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.encryptBlock(in[i], k1);
                    writeOutputFromBlocks(out, false);
                    break;
                case "双重-ECB":
                    int k2 = k(key2Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.encryptBlock2Key(in[i], k1, k2);
                    writeOutputFromBlocks(out, false);
                    break;
                case "三重-EDE(2Key)":
                    k2 = k(key2Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.encryptBlock3EDE(in[i], k1, k2);
                    writeOutputFromBlocks(out, false);
                    break;
                case "三重-EEE(3Key)":
                    k2 = k(key2Field.getText());
                    int k3 = k(key3Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.encryptBlock3EEE(in[i], k1, k2, k3);
                    writeOutputFromBlocks(out, false);
                    break;
                case "CBC(单钥)":
                    int iv = k(ivField.getText());
                    out = SAESCBC.encryptCBC(in, k1, iv);
                    writeOutputFromBlocks(out, false);
                    break;
                default:
                    throw new IllegalStateException("未知模式: " + mode);
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDecrypt(){
        try{
            if (!ensureInputNotEmpty()) return;

            String mode = (String) modeCombo.getSelectedItem();
            int[] in = readInputAsBlocks();
            int k1 = k(key1Field.getText());
            int[] out;

            switch (mode){
                case "单块-ECB":
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.decryptBlock(in[i], k1);
                    writeOutputFromBlocks(out, asciiMode.isSelected());
                    break;
                case "双重-ECB":
                    int k2 = k(key2Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.decryptBlock2Key(in[i], k1, k2);
                    writeOutputFromBlocks(out, asciiMode.isSelected());
                    break;
                case "三重-EDE(2Key)":
                    k2 = k(key2Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.decryptBlock3EDE(in[i], k1, k2);
                    writeOutputFromBlocks(out, asciiMode.isSelected());
                    break;
                case "三重-EEE(3Key)":
                    k2 = k(key2Field.getText());
                    int k3 = k(key3Field.getText());
                    out = new int[in.length];
                    for(int i=0;i<in.length;i++) out[i] = SAES.decryptBlock3EEE(in[i], k1, k2, k3);
                    writeOutputFromBlocks(out, asciiMode.isSelected());
                    break;
                case "CBC(单钥)":
                    int iv = k(ivField.getText());
                    out = SAESCBC.decryptCBC(in, k1, iv);
                    writeOutputFromBlocks(out, asciiMode.isSelected());
                    break;
                default:
                    throw new IllegalStateException("未知模式: " + mode);
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
