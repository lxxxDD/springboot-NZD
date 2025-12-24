const express = require('express');
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(express.json());
app.use(express.static('public'));

let browser = null;
let isSearching = false;
let clients = [];

// 发送日志给所有客户端
function broadcastLog(message, type = 'info') {
  const log = {
    time: new Date().toLocaleTimeString(),
    message,
    type
  };
  const eventString = `data: ${JSON.stringify(log)}\n\n`;
  clients.forEach(client => client.res.write(eventString));
}

// SSE 事件流接口
app.get('/api/events', (req, res) => {
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  
  const clientId = Date.now();
  const newClient = {
    id: clientId,
    res
  };
  clients.push(newClient);
  
  req.on('close', () => {
    clients = clients.filter(c => c.id !== clientId);
  });
});

// 初始化浏览器
async function initBrowser() {
  if (!browser) {
    broadcastLog('正在初始化量子浏览器核心...', 'system');
    browser = await chromium.launch({ headless: false });
    broadcastLog('浏览器核心加载完成', 'success');
  }
  return browser;
}

// 搜索接口
app.post('/api/search', async (req, res) => {
  if (isSearching) {
    return res.json({ success: false, message: '搜索正在进行中，请稍后' });
  }
  
  const { keywords, resultsPerKeyword = 8, sortBy = 'SU' } = req.body;
  
  if (!keywords || keywords.length === 0) {
    return res.json({ success: false, message: '请输入关键词' });
  }
  
  isSearching = true;
  const results = [];
  broadcastLog(`任务初始化：准备搜索 ${keywords.length} 个关键词`, 'system');
  
  try {
    const browser = await initBrowser();
    const page = await browser.newPage();
    broadcastLog('新建隐身页面成功，准备接入知网数据库...', 'system');
    
    for (const [index, keyword] of keywords.entries()) {
      broadcastLog(`[${index + 1}/${keywords.length}] 正在分析关键词向量: ${keyword}`, 'info');
      
      // 访问知网搜索
      const url = `https://kns.cnki.net/kns8s/search?classid=YSTT4HG0&kw=${encodeURIComponent(keyword)}&korder=${sortBy}`;
      broadcastLog(`正在建立数据链路: ${url.substring(0, 50)}...`, 'process');
      await page.goto(url);
      
      broadcastLog('等待DOM树构建与数据渲染...', 'process');
      await page.waitForTimeout(4000); // 等待时间
      
      broadcastLog('正在执行页面解析算法...', 'process');
      const items = await page.$$eval('.result-table-list tbody tr', (rows, limit) => {
        return rows.slice(0, limit).map(row => {
          const titleEl = row.querySelector('.name a');
          const title = titleEl?.textContent?.trim() || '';
          const link = titleEl?.href || '';
          const authors = row.querySelector('.author')?.textContent?.trim() || '';
          const source = row.querySelector('.source')?.textContent?.trim() || '';
          const date = row.querySelector('.date')?.textContent?.trim() || '';
          const cite = row.querySelector('.quote')?.textContent?.trim() || '0';
          const download = row.querySelector('.download')?.textContent?.trim() || '0';
          return { title, authors, source, date, link, cite, download };
        }).filter(item => item.title);
      }, resultsPerKeyword);
      
      broadcastLog(`数据提取完成，捕获 ${items.length} 条相关记录`, 'success');
      items.forEach(item => {
        broadcastLog(`  > 捕获: ${item.title.substring(0, 30)}...`, 'detail');
      });
      
      results.push({ keyword, count: items.length, items });
      
      // 随机延迟增加真实感
      if (index < keywords.length - 1) {
        const delay = Math.floor(Math.random() * 2000) + 1000;
        broadcastLog(`冷却系统介入，休眠 ${delay}ms...`, 'system');
        await page.waitForTimeout(delay);
      }
    }
    
    await page.close();
    broadcastLog('释放页面资源，断开连接', 'system');
    
    // 保存结果
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
    fs.writeFileSync(`results/search_${timestamp}.json`, JSON.stringify(results, null, 2), 'utf-8');
    broadcastLog(`数据已持久化到本地: results/search_${timestamp}.json`, 'success');
    
    res.json({ success: true, results, totalCount: results.reduce((s, r) => s + r.items.length, 0) });
    broadcastLog('所有任务执行完毕，系统就绪', 'success');
    
  } catch (error) {
    console.error('搜索出错:', error);
    broadcastLog(`系统异常: ${error.message}`, 'error');
    res.json({ success: false, message: error.message });
  } finally {
    isSearching = false;
  }
});

// 导出为Markdown
app.post('/api/export/markdown', (req, res) => {
  const { results } = req.body;
  let md = `# 知网文献搜索结果\n\n`;
  md += `> 导出时间: ${new Date().toLocaleString('zh-CN')}\n\n`;
  md += `> 共 **${results.reduce((s, r) => s + r.items.length, 0)}** 篇文献\n\n---\n\n`;
  
  for (const r of results) {
    if (r.items.length > 0) {
      md += `## ${r.keyword}\n\n`;
      md += '| 序号 | 标题 | 作者 | 期刊 | 日期 | 被引 |\n';
      md += '|------|------|------|------|------|------|\n';
      r.items.forEach((item, i) => {
        md += `| ${i+1} | ${item.title} | ${item.authors} | ${item.source} | ${item.date} | ${item.cite} |\n`;
      });
      md += '\n';
    }
  }
  
  res.json({ success: true, content: md });
});

// 导出为BibTeX
app.post('/api/export/bibtex', (req, res) => {
  const { results } = req.body;
  let bib = '';
  let index = 1;
  
  for (const r of results) {
    for (const item of r.items) {
      const key = `ref${index++}`;
      const year = item.date?.match(/\d{4}/)?.[0] || '';
      bib += `@article{${key},\n`;
      bib += `  title = {${item.title}},\n`;
      bib += `  author = {${item.authors.replace(/;/g, ' and ')}},\n`;
      bib += `  journal = {${item.source}},\n`;
      bib += `  year = {${year}},\n`;
      bib += `  note = {${item.date}}\n`;
      bib += `}\n\n`;
    }
  }
  
  res.json({ success: true, content: bib });
});

// 获取搜索状态
app.get('/api/status', (req, res) => {
  res.json({ isSearching });
});

// 确保results目录存在
if (!fs.existsSync('results')) {
  fs.mkdirSync('results');
}

// 确保public目录存在
if (!fs.existsSync('public')) {
  fs.mkdirSync('public');
}

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`\n✨ 知网文献搜索工具已启动`);
  console.log(`📍 访问地址: http://localhost:${PORT}`);
  console.log(`\n按 Ctrl+C 停止服务\n`);
});

// 关闭时清理
process.on('SIGINT', async () => {
  if (browser) await browser.close();
  process.exit();
});
