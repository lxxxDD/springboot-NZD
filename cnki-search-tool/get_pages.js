const { chromium } = require('playwright');
const fs = require('fs');

// 需要查询页码的18篇文献
const papers = [
  { title: '校园综合服务平台设计与实现', authors: '范敬義', journal: '现代计算机', year: '2023' },
  { title: '校园生活综合服务平台小程序的设计与实现', authors: '左佳丽', journal: '无线互联科技', year: '2021' },
  { title: '高校综合服务平台小程序的设计与实现', authors: '何渺渺', journal: '福建电脑', year: '2021' },
  { title: '基于微信小程序的校园服务平台设计与应用', authors: '张志云', journal: '信息与电脑', year: '2023' },
  { title: '基于微信云开发实现校园二手交易的平台设计', authors: '郭林盛', journal: '现代计算机', year: '2024' },
  { title: '基于微服务架构的高校闲置物品交易平台', authors: '张皓', journal: '电脑知识与技术', year: '2023' },
  { title: '大学校园二手交易平台构建与运营', authors: '孙丽', journal: '科技与创新', year: '2024' },
  { title: '高校食堂工作餐订餐系统的开发与应用', authors: '蒋昕', journal: '高校后勤研究', year: '2025' },
  { title: '基于Android的高校食堂订餐系统设计与开发', authors: '孙杰', journal: '计算机时代', year: '2023' },
  { title: '基于物联网技术的智慧食堂管理系统设计与实现', authors: '潘焜', journal: '科技资讯', year: '2023' },
  { title: '报修系统在高校后勤管理中的应用研究', authors: '任东帅', journal: '高校后勤研究', year: '2022' },
  { title: '基于微信小程序的电气故障报修系统设计与实现', authors: '孙志成', journal: '自动化应用', year: '2024' },
  { title: '基于SpringBoot+Vue的校园活动管理系统设计与实现', authors: '钟育伙', journal: '电子技术', year: '2024' },
  { title: '信息化背景下高校社团建设与管理的创新研究', authors: '张晨', journal: '信息与电脑', year: '2024' },
  { title: '基于Spring Boot和WebSocket的点对点聊天系统研发与部署', authors: '黎志伟', journal: '科学技术创新', year: '2020' },
  { title: 'Uni-app框架下基于Token的身份认证及持久化登录设计与实现', authors: '吴建成', journal: '数字技术与应用', year: '2024' },
  { title: 'Uni-APP移动应用开发技术分析', authors: '任远', journal: '电子技术与软件工程', year: '2023' },
  { title: '基于Spring Boot+Vue框架的阅览室现刊查询系统设计与实现', authors: '孔倩', journal: '现代信息科技', year: '2025' }
];

(async () => {
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  
  const results = [];
  
  for (let i = 0; i < papers.length; i++) {
    const paper = papers[i];
    console.log(`\n[${i+1}/18] 查询: ${paper.title}`);
    
    try {
      // 搜索文献
      const searchUrl = `https://kns.cnki.net/kns8s/search?classid=YSTT4HG0&kw=${encodeURIComponent(paper.title)}&korder=SU`;
      await page.goto(searchUrl);
      await page.waitForTimeout(3000);
      
      // 点击第一个结果进入详情页
      const firstResult = await page.$('.result-table-list tbody tr:first-child .name a');
      if (firstResult) {
        // 获取链接并在新标签页打开
        const href = await firstResult.getAttribute('href');
        const detailUrl = href.startsWith('http') ? href : `https://kns.cnki.net${href}`;
        
        await page.goto(detailUrl);
        await page.waitForTimeout(2000);
        
        // 等待页面加载完成
        await page.waitForTimeout(3000);
        
        let pageInfo = '';
        let issueInfo = '';
        
        // 从页面底部信息提取页码 (页数: 87-89)
        const pageText = await page.$$eval('.top-space p, .brief p, .doc-top p', els => 
          els.map(el => el.textContent).join(' ')
        ).catch(() => '');
        
        const pageMatch = pageText.match(/页[数码][：:]\s*(\d+)[-~](\d+)/);
        if (pageMatch) {
          pageInfo = `${pageMatch[1]}-${pageMatch[2]}`;
        }
        
        // 从顶部来源信息提取卷期 (2020,36(02):87-89)
        const topText = await page.$eval('.top-tip, .wx-tit', el => el.textContent).catch(() => '');
        const fullMatch = topText.match(/(\d{4})[,，]\s*\d+\s*\((\d+)\)[：:]\s*(\d+)[-~](\d+)/);
        if (fullMatch) {
          issueInfo = `${fullMatch[1]}(${fullMatch[2].padStart(2, '0')})`;
          if (!pageInfo) pageInfo = `${fullMatch[3]}-${fullMatch[4]}`;
        }
        
        // 备用：从整个页面文本提取
        if (!pageInfo || !issueInfo) {
          const allText = await page.evaluate(() => document.body.innerText).catch(() => '');
          if (!pageInfo) {
            const m = allText.match(/页[数码][：:]\s*(\d+)[-~](\d+)/);
            if (m) pageInfo = `${m[1]}-${m[2]}`;
          }
          if (!issueInfo) {
            const m = allText.match(/(\d{4})[,，]\s*\d+\s*\((\d+)\)/);
            if (m) issueInfo = `${m[1]}(${m[2].padStart(2, '0')})`;
          }
        }
        
        results.push({
          index: i + 1,
          title: paper.title,
          authors: paper.authors,
          journal: paper.journal,
          issue: issueInfo || `${paper.year}`,
          pages: pageInfo || '待补充'
        });
        
        console.log(`  期号: ${issueInfo || paper.year}, 页码: ${pageInfo || '未找到'}`);
      } else {
        results.push({
          index: i + 1,
          title: paper.title,
          authors: paper.authors,
          journal: paper.journal,
          issue: paper.year,
          pages: '待补充'
        });
        console.log('  未找到详情');
      }
    } catch (err) {
      console.log(`  错误: ${err.message}`);
      results.push({
        index: i + 1,
        title: paper.title,
        authors: paper.authors,
        journal: paper.journal,
        issue: paper.year,
        pages: '待补充'
      });
    }
  }
  
  // 输出结果
  console.log('\n\n========== 查询完成 ==========\n');
  
  let output = '# 参考文献（含页码）\n\n';
  results.forEach(r => {
    output += `[${r.index}] ${r.authors},等．${r.title}[J]．${r.journal}，${r.issue}：${r.pages}．\n`;
  });
  
  fs.writeFileSync('参考文献_含页码.txt', output, 'utf-8');
  console.log('已保存到: 参考文献_含页码.txt');
  console.log(output);
  
  await browser.close();
})();
