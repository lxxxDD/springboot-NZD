/**
 * 心知天气 API（免费版）
 */

const API_KEY = 'SL8TZF55MqTUw2s-h'

/**
 * 获取实时天气
 */
export function getWeatherNow(location = 'nanning') {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `https://api.seniverse.com/v3/weather/now.json`,
      method: 'GET',
      data: {
        key: API_KEY,
        location: location,
        language: 'zh-Hans',
        unit: 'c'
      },
      success: (res) => {
        console.log('心知天气返回:', res.data)
        if (res.data.results && res.data.results[0]) {
          const result = res.data.results[0]
          resolve({
            temp: result.now.temperature,
            text: result.now.text,
            humidity: '65',  // 免费版无湿度，使用固定值
            icon: result.now.code,
            city: result.location.name
          })
        } else {
          reject(new Error('获取天气失败'))
        }
      },
      fail: (err) => {
        console.error('天气请求失败:', err)
        reject(err)
      }
    })
  })
}
