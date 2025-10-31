# 🔐《信息安全导论》课程作业2——S-AES 算法实现

> 小组：越学越铺张 — 卜慧娟、龚雪、王欣悦、张钰婕

## 📖 项目简介

本项目实现了S-AES算法，算法标准设定如下：

- 分组长度：16-bit
- 密钥长度：16-bit
- 算法描述：

    1.加密算法：
$$A_{K_2} ∘ SR ∘ NS ∘ A_{K_1} ∘ MC ∘ SR ∘ NS ∘ A_{K_0}$$

    2.解密算法：
$$A_{K_0} ∘ INS ∘ ISR ∘ IMC ∘ A_{K_1} ∘ INS ∘ ISR ∘ A_{K_2}$$

>密钥加($$A_K$$)、半字节代替(NS)、行移位(SR)、列混淆(MC)
>
>逆半字节代替(INS)、逆行移位(ISR)、逆列混淆(IMC)


## 🧱 代码结构

```text
├── core/                          # 核心算法层
│   ├── SAES.java                  # S-AES 核心算法实现（加密、解密、密钥扩展等）
│   ├── SAESCBC.java               # CBC 工作模式实现（加密、解密）
│   └── Util.java                  # 工具类（数据转换、格式处理等）
│
├── gui/                           # 图形界面层（Swing）
│   └── SAESFrame.java             # 主界面（模式切换、加解密操作、用户交互）
│
└── Main.java                      # 程序入口（启动 GUI）
└── README.md 
```

## 🧪 关卡测试结果

### 第1关：基本测试 ✅
开发了完整的S-AES算法实现，并提供了图形用户界面，支持用户交互式操作，支持16位二进制数据和16位二进制密钥的加解密。

输入：16-bit明文(密文) + 16-bit密钥 → 输出：16-bit密文(明文)

核心算法模块包括：密钥生成器、加密模块、解密模块

界面支持二进制模式和ASCII模式切换

#### 加密解密
输入16位二进制明/密文，16位二进制密钥，点击加/解密按钮后得到16位二进制密/明文

<img width="536" height="440" alt="屏幕截图 2025-10-31 113034" src="https://github.com/user-attachments/assets/494aa0fc-d55d-4d4c-a2f8-6872a7a9a3b7" />



### 第2关：交叉测试 ✅
严格遵循标准S-AES算法流程，使用相同算法流程和转换单元(替换盒、列混淆矩阵等)，以保证算法和程序在异构的系统或平台上都可以正常运行

算法组件标准化：

S盒和逆S盒采用标准定义

测试结果：接收到加密的密文C，进行解密可得到相同的P

#### 约定密钥为：0110000000001111

> 已知密文：0110001110010011
> 
> 解密得到原明文：1010101010101010

<img width="536" height="438" alt="屏幕截图 2025-10-31 184755" src="https://github.com/user-attachments/assets/fa0f2b9b-4760-4d05-984c-2c8caf3e7a93" />




### 第3关：扩展功能 ✅
扩展支持ASCII编码字符串(分组为2 Bytes)的加解密功能

输入处理：将ASCII字符串按2字节分组

填充机制：对不足16位的分组进行标准填充



<img width="538" height="443" alt="屏幕截图 2025-10-31 190000" src="https://github.com/user-attachments/assets/6c3dcc19-e2d7-45c3-939c-ba9a2afcdae2" />



### 第4关：多重加密 ✅
#### 双重加密

采用两次 S-AES 加密级联，总密钥长度扩展为32位（K1和K2各16位）

加密过程为 $$C = E_K2(E_K1(P))$$ ，解密过程为 $$P = E_K1(E_K2(C))$$

<img width="537" height="443" alt="屏幕截图 2025-10-31 193747" src="https://github.com/user-attachments/assets/ff2a4a18-013c-4e9a-9be3-7a38038a9800" />



#### 三重加密
##### EEE模式
加密-加密-加密模式：
$$C = E_K2(E_K1(E_K1(P)))$$


<img width="537" height="444" alt="屏幕截图 2025-10-31 194050" src="https://github.com/user-attachments/assets/2351326b-a0e7-42a9-9c68-00cae49037bf" />


##### EDE模式
加密-解密-加密模式：
$$C = E_K1(D_K2(E_K1(P)))$$


<img width="532" height="440" alt="屏幕截图 2025-10-31 194320" src="https://github.com/user-attachments/assets/2d32b906-e235-4e0f-b44f-a7a9fb7e0857" />


### 第5关：工作模式 ✅
#### 较长明文信息加密

<img width="556" height="459" alt="屏幕截图 2025-10-31 194853" src="https://github.com/user-attachments/assets/a65e021e-72a4-40e4-81c6-84630cf965e2" />


#### 初始向量测试
使用相同明文和密钥，但不同IV

相同明文+相同密钥+不同IV → 完全不同密文

<img width="536" height="444" alt="屏幕截图 2025-10-31 195513" src="https://github.com/user-attachments/assets/c9254b76-131a-4adc-a093-e77276848a9e" />




## 🖥️ 运行环境

- **操作系统**：Windows / macOS / Linux  
- **Java 版本**：JDK 11 或更高  
- **开发工具**：VS Code
- **依赖**：无第三方依赖，仅使用标准库 `javax.swing`

## 🚀 使用方法

### 编译与运行（命令行方式）

## 📖 相关文档
- **用户指南**：[用户指南.docx](https://github.com/user-attachments/files/23261621/default.docx)


- **开发手册**：[开发手册.docx](https://github.com/user-attachments/files/23261619/default.docx)




