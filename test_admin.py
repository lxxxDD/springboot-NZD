from playwright.sync_api import sync_playwright
import os

os.makedirs("screenshots", exist_ok=True)

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page(viewport={"width": 1920, "height": 1080})
    
    # 登录
    page.goto("http://localhost:3001")
    page.wait_for_load_state("networkidle")
    page.wait_for_timeout(1000)
    
    # 输入账号密码
    page.fill('input[placeholder="管理员账号"]', 'admin')
    page.fill('input[placeholder="登录密码"]', 'admin123')
    page.click('button:has-text("登 录")')
    page.wait_for_timeout(2000)
    
    if "login" not in page.url:
        print("登录成功!")
        
        # 直接通过URL访问各个页面
        pages = [
            ("dashboard", "01_控制台"),
            ("user/list", "02_用户列表"),
            ("finance/recharge", "03_充值管理"),
            ("finance/transaction", "04_交易记录"),
            ("operation/activity", "05_活动管理"),
            ("operation/content", "06_内容管理"),
            ("operation/secondhand", "07_二手商品"),
            ("service/canteen", "08_食堂管理"),
            ("service/food", "09_菜品管理"),
            ("service/repair", "10_报修管理"),
            ("service/technician", "11_维修人员"),
            ("settings/admin", "12_管理员设置"),
            ("settings/role", "13_角色权限"),
            ("settings/log", "14_系统日志"),
        ]
        
        for route, name in pages:
            try:
                page.goto(f"http://localhost:3001/#/{route}")
                page.wait_for_load_state("networkidle")
                page.wait_for_timeout(1500)
                page.screenshot(path=f"screenshots/{name}.png", full_page=True)
                print(f"截图: {name}")
            except Exception as e:
                print(f"失败: {name} - {e}")
    else:
        print("登录失败")
        page.screenshot(path="screenshots/login_failed.png")
    
    browser.close()
    print("完成!")
