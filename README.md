---

## Verdictum — Felony Gravity Assessment System

[English](#english) | [中文](#中文)

---

## English

### Overview

**Verdictum** is an open-source, multi‑platform AI‑powered sentencing analysis tool designed for legal professionals, researchers, and educators. It transforms natural‑language criminal case descriptions into a normalized **Gravity Score (R)** ranging from 0 (acquittal) to 1 (death penalty) by decomposing the text into structured legal factors: crimes, aggravating/mitigating actions, and sentencing interferences. The entire calculation and correction logic runs locally in Java, ensuring full reproducibility, auditability, and transparency.

### Features

- **🧠 AI‑Driven Semantic Extraction** – Uses large language models to extract crimes, action coefficients, and interference factors from case narratives.
- **⚖️ Multi‑Platform AI Support** – Works with OpenAI, Anthropic, OpenRouter, and local Ollama models. Configured via a simple JSON file.
- **📊 Deterministic Local Engine** – All scoring and correction rules run offline in Java, guaranteeing the same input always produces the same output.
- **🔧 Modular & Editable Rules** – Crime base values, action coefficients, interference factors, and penalty mappings are stored as plain‑text modules that can be modified without touching the source code.
- **🖥️ Modern Flat GUI** – A clean, flat Swing interface with rounded containers, self‑drawn checkboxes/combo boxes, and a terminal‑style animated progress view.
- **📁 Built‑in Case Library** – Save, filter, review, and delete past analyses. Data is stored locally in human‑readable JSON files.
- **🛡️ Correction Chain** – Automatic post‑calculation adjustments: death penalty cap, light‑crime safeguard, surrender decay for extreme malice, and more.
- **🔑 Secure API Key Storage** – Keys are encrypted and stored locally; first‑run setup dialog with custom flat UI.

### Quick Start

**Prerequisites**
- Java 17 or higher
- Maven 3.6+
- API key for at least one supported AI provider, or a running Ollama instance (for offline use)

**Build & Run**
```bash
git clone https://github.com/yourusername/Verdictum.git
cd Verdictum
mvn clean compile exec:java -Dexec.mainClass="verdictum.VerdictumApp"
```

On the first launch, you will be asked to enter your API key. It will be saved securely in your user home directory.

**Using Ollama (offline)**
1. Install [Ollama](https://ollama.com/) and pull a model (e.g., `ollama pull llama3`).
2. Add an entry to `src/main/resources/models.json`:
```json
{
  "displayName": "Llama 3 (local)",
  "apiModel": "llama3:latest",
  "platform": "ollama",
  "baseUrl": "http://localhost:11434/api/generate",
  "responseFormat": "ollama"
}
```
3. Launch Verdictum, select the model, and start analyzing.

### How It Works

1. **Crime Base Value** – Each crime (e.g., intentional homicide = 0.90) carries an intrinsic severity weight.
2. **Action Coefficients** – Aggravating and mitigating details (use of weapon, number of victims, cruelty, premeditation, voluntary surrender, etc.) adjust the base value.
3. **Interference Factors** – Post‑crime circumstances (surrender, compensation, mental illness, extreme malice…) multiply the combined score.
4. **Correction Chain** – A series of rules (death penalty cap, light‑crime safeguard, surrender decay for extreme malice) are applied before final clamping.
5. **Penalty Mapping** – The final R value (0–1) is mapped to a real‑world sentencing range:

| R Interval | Penalty |
|------------|---------|
| 0.00 | Acquittal |
| 0.01 – 0.19 | Exempt from punishment |
| 0.20 – 0.39 | Probation / suspended sentence |
| 0.40 – 0.59 | Short‑term imprisonment (≤3 years) |
| 0.60 – 0.79 | Medium‑to‑long imprisonment (3–15 years) |
| 0.80 – 0.99 | Long‑term / life imprisonment |
| 1.00 | Death penalty (sub‑thresholds for immediate vs. suspended) |

### Project Structure

```
verdictum/
├── pom.xml
└── src/main/
    ├── resources/
    │   ├── models.json                # AI model configurations
    │   └── modules/                    # Editable rule modules
    │       ├── config.yaml
    │       ├── base_values.txt
    │       ├── action_coefficients.txt
    │       └── interference_factors.txt
    └── java/verdictum/
        ├── VerdictumApp.java
        ├── client/                     # Multi‑platform AI client
        ├── config/                     # Constants & API key manager
        ├── engine/                     # Calculation engine & correction chain
        ├── loader/                     # Dynamic rule module loader
        ├── model/                      # Data models
        ├── parser/                     # AI response parser
        ├── storage/                    # Local case library storage
        └── ui/                         # GUI panels & custom components
            └── components/             # FlatButtonUI, RoundedContainer, etc.
```

### Configuration

**AI Models** (`models.json`)
Add or remove models freely. Each entry supports `displayName`, `apiModel`, `platform`, `baseUrl`, and `responseFormat`.

**Rule Modules**
The text files in `src/main/resources/modules/` control all legal parameters. They follow a simple Markdown‑table format and can be edited without recompiling.

### Contributing

Contributions are welcome! Areas where you can help:
- Expanding support for more crime types (property, economic, cyber)
- Adding more sophisticated correction rules
- Improving AI prompts for better extraction accuracy
- Enhancing the GUI (themes, charts, report generation)
- Providing official Docker images

Please open an issue or submit a pull request.

### License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

### Disclaimer

Verdictum is a research and educational tool. It is **not** intended to replace professional legal judgment. Always consult qualified legal professionals for actual case decisions.

---

## 中文

### 概览

**Verdictum** 是一款开源、跨平台、基于人工智能的量刑推演工具，面向法律专业人士、研究人员及教育工作者。它能将自然语言描述的刑事案件自动拆解为罪名、加重/减轻动作系数及量刑干扰素等结构化要素，并计算出归一化的**重罪度 (R)**（0 无罪，1 死刑）。所有计算与修正逻辑均由本地 Java 引擎完成，确保过程可复现、可审计、可解释。

### 主要特性

- **🧠 AI 语义解构** – 利用大语言模型从案情文本中提取罪名、动作系数和干扰素。
- **⚖️ 多平台 AI 支持** – 兼容 OpenAI、Anthropic、OpenRouter 及本地 Ollama 模型，通过 JSON 文件灵活配置。
- **📊 确定性本地引擎** – 全部计分与修正规则均在 Java 中离线运行，相同输入必然产生相同输出。
- **🔧 模块化可编辑规则** – 罪名基础值、动作系数、干扰素表、刑罚映射等均以纯文本模块存储，无需修改源代码即可调整。
- **🖥️ 现代扁平 GUI** – 基于 Swing 的干净界面，配备圆角容器、自绘复选框/下拉框，以及终端风格动画进度视图。
- **📁 内置案件库** – 支持保存、筛选、回顾和删除历史分析，数据以易读的 JSON 文件本地存储。
- **🛡️ 修正链** – 自动执行死刑上限限制、轻罪从宽保护、极端恶意自首衰减等后处理规则。
- **🔑 安全密钥存储** – API 密钥加密存于本地，首次运行时通过自定义扁平对话框输入。

### 快速开始

**前置要求**
- Java 17+
- Maven 3.6+
- 至少一个受支持的 AI 服务 API 密钥，或已运行的 Ollama 实例（可离线使用）

**构建与运行**
```bash
git clone https://github.com/yourusername/Verdictum.git
cd Verdictum
mvn clean compile exec:java -Dexec.mainClass="verdictum.VerdictumApp"
```

首次启动时会要求输入 API 密钥，密钥将安全保存在用户主目录中。

**使用 Ollama (离线)**
1. 安装 [Ollama](https://ollama.com/) 并拉取模型（如 `ollama pull llama3`）。
2. 在 `src/main/resources/models.json` 中添加条目：
```json
{
  "displayName": "Llama 3 (本地)",
  "apiModel": "llama3:latest",
  "platform": "ollama",
  "baseUrl": "http://localhost:11434/api/generate",
  "responseFormat": "ollama"
}
```
3. 启动 Verdictum，选择该模型即可开始分析。

### 工作原理

1. **罪名基础值** – 每种罪名自带固有严重程度权重（如故意杀人罪=0.90）。
2. **动作系数** – 加重或减轻情节（持械、伤亡人数、残忍程度、预谋、自首等）调整基础值。
3. **干扰素** – 案后因素（自首、赔偿、精神障碍、主观恶意等）以乘法因子修正合并分值。
4. **修正链** – 最终钳制前依次应用死刑上限、轻罪保护、极端恶意自首衰减等规则。
5. **刑罚映射** – 最终 R 值（0–1）映射到真实刑罚区间：

| R 区间 | 对应刑罚 |
|--------|----------|
| 0.00 | 无罪 |
| 0.01 – 0.19 | 免予刑事处罚 |
| 0.20 – 0.39 | 管制 / 缓刑 |
| 0.40 – 0.59 | 短期有期徒刑（≤3年） |
| 0.60 – 0.79 | 中长期有期徒刑（3–15年） |
| 0.80 – 0.99 | 长期徒刑 / 无期徒刑 |
| 1.00 | 死刑（细分立即执行与缓期执行） |

### 项目结构

```
verdictum/
├── pom.xml
└── src/main/
    ├── resources/
    │   ├── models.json                # AI 模型配置
    │   └── modules/                    # 可编辑规则模块
    │       ├── config.yaml
    │       ├── base_values.txt
    │       ├── action_coefficients.txt
    │       └── interference_factors.txt
    └── java/verdictum/
        ├── VerdictumApp.java
        ├── client/                     # 多平台 AI 客户端
        ├── config/                     # 常量与 API 密钥管理
        ├── engine/                     # 计算引擎与修正链
        ├── loader/                     # 动态规则加载器
        ├── model/                      # 数据模型
        ├── parser/                     # AI 响应解析器
        ├── storage/                    # 本地案件库存储
        └── ui/                         # 图形界面及自定义组件
            └── components/             # 扁平按钮、圆角容器等
```

### 配置说明

**AI 模型** (`models.json`)  
可自由增删模型，每个条目支持 `displayName`、`apiModel`、`platform`、`baseUrl`、`responseFormat`。

**规则模块**  
`src/main/resources/modules/` 下的文本文件控制全部法律参数，采用简单的 Markdown 表格格式，可直接编辑而无需重新编译。

### 参与贡献

欢迎贡献！您可以参与：
- 扩展更多犯罪类型（财产、经济、网络等）
- 增加更精细的修正规则
- 优化 AI 提示词以提高抽取准确率
- 改进 GUI（主题、图表、报告生成）
- 提供官方 Docker 镜像

请提交 Issue 或 Pull Request。

### 许可证

本项目采用 **MIT 许可证**。详见 [LICENSE](LICENSE) 文件。

### 免责声明

Verdictum 仅为研究与教育工具，**不能**替代专业法律判断。实际案件请务必咨询合格的法律专业人士。
