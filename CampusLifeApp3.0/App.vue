<script>
	import { initRouteGuard } from '@/utils/routeGuard.js'
	import { websocket } from '@/utils/websocket.js'
	
	export default {
		onLaunch: function() {
			console.log('App Launch')
			
			// 初始化路由守卫
			initRouteGuard()
			
			// 连接WebSocket
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo && userInfo.id) {
				websocket.connect(userInfo.id)
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">


/* 在 uni.scss 或 App.vue 中 */

// 主色调保留
$primary: #6366F1;
$primary-dark: #4F46E5;
$bg-color: #F8FAFC;

// 前卫设计变量
$glass-bg: rgba(255, 255, 255, 0.65);
$glass-border: 1px solid rgba(255, 255, 255, 0.6);
$glass-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
$blur-val: blur(12px);

// 弥散背景球通用样式
.blob-bg {
  position: fixed;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.6;
  animation: float 10s infinite ease-in-out;
}

@keyframes float {
  0% { transform: translate(0, 0); }
  50% { transform: translate(20px, 40px); }
  100% { transform: translate(0, 0); }
}

// 容器修正
.page-container {
  position: relative;
  min-height: 100vh;
  background: #F8FAFC;
  overflow: hidden;
}
	/* Import Material Symbols Outlined Font */
	@font-face {
	  font-family: 'Material Symbols Outlined';
	  font-style: normal;
	  font-weight: 400;
	  src: url(https://fonts.gstatic.com/s/materialsymbolsoutlined/v175/kJF1BvYX7BgnkSrUwT8OhrdQw4oELdPIeeII9v6oDMzByHX9rA6RzaxHMPdY43zj-jCxv3fzvRNU22ZXGJpEpjC_1n-q_4MrImHCIJIZrDCvHOej.woff2) format('woff2');
	}

	/* 注意要写在第一行，注意不能引入至uni.scss，同时给style标签加入lang="scss"属性 */
	@import "@/uni_modules/uview-plus/index.scss";
	
	/*每个页面公共css */
	/* Global layout fixes */
	page {
		overflow-x: hidden;
		background-color: #F8FAFC;
	}
	
	/* WeChat Mini Program doesn't support * selector, so we apply box-sizing to common elements manually */
	view, scroll-view, swiper, swiper-item, cover-view, cover-image, icon, text, rich-text, progress, button, checkbox, form, input, label, radio, slider, switch, textarea, navigator, audio, camera, image, video, live-player, live-pusher, map, canvas, open-data, web-view, ad {
		box-sizing: border-box;
	}
	
	image {
		max-width: 100%;
		display: block;
	}
	
	scroll-view {
		width: 100%;
	}
</style>
