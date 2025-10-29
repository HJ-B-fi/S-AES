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
│   ├── Decryption.java            # 解密流程（K2→K1 两轮、IP/IP^-1）
│   ├── Encryption.java            # 加密流程（K1→K2 两轮、IP/IP^-1）
│   ├── KeyGenerator.java          # 密钥调度：P10→LS→P8 生成 K1/K2
│   └── SDESCore.java              # 统一出入口：Binary/ASCII 加/解密封装
│
├── ui/                            # 图形界面层（Swing）
│   ├── BruteForceSDES.java        # 暴力破解窗口（多线程、进度/统计）
│   └── SDESGUI.java               # 主界面（模式切换、输入校验、演示）
│
├── utils/                         # 通用工具/组件
│   ├── CommonUtils.java           # 置换、异或、循环左移、编码转换、校验
│   └── SBoxManager.java           # S0/S1 S-Box 与替换逻辑
│
├── Main.java                      # 程序入口（启动 GUI）
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

<img width="552" height="331" alt="联想截图_20251008193506" src="https://github.com/user-attachments/assets/44c935fa-98bd-452f-a65a-35114dbd829d" />

### 第2关：交叉测试 ✅
严格遵循标准S-AES算法流程，使用相同算法流程和转换单元(替换盒、列混淆矩阵等)，以保证算法和程序在异构的系统或平台上都可以正常运行

算法组件标准化：

S盒和逆S盒采用标准定义

测试结果：B组同学接收到A组程序加密的密文C，使用B组程序进行解密可得到与A相同的P

#### 约定密钥为：0000000000000000

> A组使用明文：1010101010101010
> 
> A组得到密文：00010001

![0fd11eff5c465992f2475b41b94f0c21](https://github.com/user-attachments/assets/683311fe-f2c3-47fc-91b8-acf644ef5493)

> B组已知密文：00010001
> 
> B组解密得到：1010101010101010

<img width="552" height="331" alt="联想截图_20251008193317" src="https://github.com/user-attachments/assets/4d27a209-9833-4c8c-b902-a7b0f74e1102" />


### 第3关：扩展功能 ✅
扩展支持ASCII编码字符串(分组为2 Bytes)的加解密功能

输入处理：将ASCII字符串按2字节分组

填充机制：对不足16位的分组进行标准填充

输出处理：将二进制数据转换回ASCII字符


<img width="552" height="331" alt="联想截图_20251008202610" src="https://github.com/user-attachments/assets/d0eb5213-9759-4036-bd2b-ef8febd9c572" />


### 第4关：多重加密 ✅
#### 双重加密
- 分组长度：16-bit
- 密钥长度：32-bit

#### 中间相遇攻击
密钥空间：2¹⁰ = 42.9亿个可能密钥

采用多线程技术，默认使用4个线程并行破解

每个线程处理256个密钥（1024/4）

##### 单对明密文攻击：
已知: 明文P → 密文C

攻击时间: 约XX秒

找到候选密钥数量: X个

正确密钥包含在候选集中: 是

<img width="589" height="367" alt="联想截图_20251008202732" src="https://github.com/user-attachments/assets/789d3fc2-a731-451f-9273-a63ed1af8ab5" />

##### 多对明密文攻击：
已知: 2对明密文(P1→C1, P2→C2)  

攻击时间: 约XX秒

找到唯一密钥: 是

密钥验证正确: 是
#### 三重加密
##### 加密模式
采用加密-加密-加密(EEE)模式：
$$C = E_K2(E_K1(E_K1(P)))$$

### 第5关：工作模式 ✅
#### 较长明文信息加密
<img width="589" height="367" alt="联想截图_20251008202947" src="https://github.com/user-attachments/assets/9e0fa02b-1a8d-4199-8f8a-bab463687e43" />

#### 初始向量测试
使用相同明文和密钥，但不同IV

相同明文+相同密钥+不同IV → 完全不同密文

#### 错误传播特性测试
验证CBC模式的错误传播特性：单个密文分组错误对解密结果的影响


<img width="589" height="368" alt="联想截图_20251008203402" src="https://github.com/user-attachments/assets/08663fc5-871b-4372-8b93-98e65cc84acb" />



## 🖥️ 运行环境

- **操作系统**：Windows / macOS / Linux  
- **Java 版本**：JDK 11 或更高  
- **开发工具**：VS Code
- **依赖**：无第三方依赖，仅使用标准库 `javax.swing`

## 🚀 使用方法

### 编译与运行（命令行方式）

## 📖 相关文档
- **用户指南**：[用户指南.docx](https://github.com/user-attachments/files/22778164/default.docx)

- **开发手册**：[开发手册.docx](https://github.com/user-attachments/files/22778212/default.docx)



