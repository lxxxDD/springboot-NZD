const WordExtractor = require('word-extractor');
const fs = require('fs');

const extractor = new WordExtractor();

async function readDoc(filePath) {
  try {
    const extracted = await extractor.extract(filePath);
    const text = extracted.getBody();
    console.log('=== 文件内容 ===\n');
    console.log(text);
    fs.writeFileSync(filePath.replace(/\.doc$/, '.txt'), text, 'utf-8');
    console.log('\n已保存为txt文件');
  } catch (err) {
    console.error('错误:', err.message);
  }
}

// 读取论文规范
readDoc('d:/AGXNZDBiShe/lgfbishe/word/附件1 广西农业职业技术大学职业本科毕业论文（设计）规范（修订）.doc');
