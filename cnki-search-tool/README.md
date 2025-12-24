# 知网论文自动搜索工具

基于 Playwright 的知网(CNKI)论文自动搜索工具，用于批量搜索学术文献。

## 功能特点

- 自动打开浏览器访问知网
- 批量搜索多个关键词
- 自动提取论文标题、作者、期刊、日期
- 输出JSON格式结果

## 安装使用

### 1. 安装依赖

```bash
npm install
```

### 2. 安装浏览器驱动

```bash
npm run install-browser
```

### 3. 运行搜索

```bash
npm start
```

## 自定义关键词

编辑 `index.js` 文件中的 `keywords` 数组，添加你需要搜索的关键词：

```javascript
const keywords = [
  '校园二手交易平台',
  'Spring Boot 电商系统',
  // 添加更多关键词...
];
```

## 输出格式

搜索结果会以JSON格式输出，包含：
- `keyword`: 搜索关键词
- `items`: 搜索结果数组
  - `title`: 论文标题
  - `authors`: 作者
  - `source`: 期刊名称
  - `date`: 发表日期

## 注意事项

- 需要联网访问知网
- 浏览器会自动打开，请勿关闭
- 搜索完成后浏览器会自动关闭
