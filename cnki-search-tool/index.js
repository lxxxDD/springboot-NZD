const { chromium } = require('playwright');
const fs = require('fs');

(async () => {
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  
  // 搜索关键词列表 - 针对"校园生活服务综合平台"优化
  const keywords = [
    // 核心主题 - 综合平台类
    '校园综合服务平台 设计与实现',
    '智慧校园 一站式服务',
    '高校生活服务系统 Spring Boot',
    '校园服务平台 微信小程序',
    
    // 二手交易模块
    '高校二手交易 Spring Boot',
    '校园闲置物品交易平台',
    'C2C二手交易系统设计',
    
    // 食堂点餐模块  
    '高校食堂订餐系统',
    '在线点餐系统 设计与实现',
    '智慧食堂 小程序',
    
    // 报修服务模块
    '高校后勤报修系统',
    '宿舍维修管理系统',
    '报修工单系统 设计',
    
    // 活动管理模块
    '校园活动管理系统 设计与实现',
    '高校社团活动平台',
    
    // 即时通讯模块
    'WebSocket 在线聊天系统',
    '即时通讯 Spring Boot',
    
    // 技术架构类
    '前后端分离 Spring Boot Vue',
    'uni-app 移动端开发',
    'JWT Token 身份认证',
    'MyBatis-Plus 数据持久层'
  ];
  
  const results = [];
  
  for (const keyword of keywords) {
    console.log(`\n正在搜索: ${keyword}`);
    
    // 访问知网搜索
    await page.goto(`https://kns.cnki.net/kns8s/search?classid=YSTT4HG0&kw=${encodeURIComponent(keyword)}&korder=SU`);
    
    // 等待搜索结果加载 - 增加等待时间确保完全加载
    await page.waitForTimeout(4000);
    
    // 提取前8条结果，包含更多信息
    const items = await page.$$eval('.result-table-list tbody tr', (rows) => {
      return rows.slice(0, 8).map(row => {
        const title = row.querySelector('.name a')?.textContent?.trim() || '';
        const authors = row.querySelector('.author')?.textContent?.trim() || '';
        const source = row.querySelector('.source')?.textContent?.trim() || '';
        const date = row.querySelector('.date')?.textContent?.trim() || '';
        // 尝试获取链接
        const link = row.querySelector('.name a')?.href || '';
        return { title, authors, source, date, link };
      }).filter(item => item.title); // 过滤空结果
    });
    
    results.push({ keyword, items });
    console.log(`找到 ${items.length} 条结果`);
    items.forEach((item, i) => {
      console.log(`  ${i+1}. ${item.title} - ${item.authors} - ${item.source} ${item.date}`);
    });
  }
  
  console.log('\n\n========== 搜索完成 ==========\n');
  
  // 统计总数
  const totalCount = results.reduce((sum, r) => sum + r.items.length, 0);
  console.log(`共找到 ${totalCount} 篇相关文献\n`);
  
  // 保存到文件
  const outputFile = 'search_results.json';
  fs.writeFileSync(outputFile, JSON.stringify(results, null, 2), 'utf-8');
  console.log(`结果已保存到: ${outputFile}`);
  
  // 生成Markdown格式报告
  let markdown = '# 知网文献搜索结果\n\n';
  markdown += `> 搜索时间: ${new Date().toLocaleString('zh-CN')}\n\n`;
  markdown += `> 共找到 **${totalCount}** 篇相关文献\n\n---\n\n`;
  
  for (const result of results) {
    if (result.items.length > 0) {
      markdown += `## ${result.keyword}\n\n`;
      markdown += '| 序号 | 标题 | 作者 | 期刊 | 日期 |\n';
      markdown += '|------|------|------|------|------|\n';
      result.items.forEach((item, i) => {
        markdown += `| ${i+1} | ${item.title} | ${item.authors} | ${item.source} | ${item.date} |\n`;
      });
      markdown += '\n';
    }
  }
  
  fs.writeFileSync('文献搜索结果.md', markdown, 'utf-8');
  console.log('Markdown报告已保存到: 文献搜索结果.md');
  
  console.log(JSON.stringify(results, null, 2));
  
  await browser.close();
})();
