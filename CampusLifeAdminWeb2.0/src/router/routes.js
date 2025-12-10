import Layout from '@/layout/index.vue'

// 完整的路由配置（包含权限标识）
export const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台', icon: 'Odometer', permission: 'dashboard' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户列表', icon: 'List', permission: 'user:list' }
      }
    ]
  },
  {
    path: '/service',
    component: Layout,
    redirect: '/service/repair',
    meta: { title: '服务管理', icon: 'Service' },
    children: [
      {
        path: 'repair',
        name: 'RepairManage',
        component: () => import('@/views/service/repair.vue'),
        meta: { title: '报修管理', icon: 'Tools', permission: 'service:repair' }
      },
      {
        path: 'technician',
        name: 'TechnicianManage',
        component: () => import('@/views/service/technician.vue'),
        meta: { title: '维修人员', icon: 'User', permission: 'service:technician' }
      },
      {
        path: 'canteen',
        name: 'CanteenManage',
        component: () => import('@/views/service/canteen.vue'),
        meta: { title: '食堂管理', icon: 'Bowl', permission: 'service:canteen' }
      },
      {
        path: 'food',
        name: 'FoodManage',
        component: () => import('@/views/service/food.vue'),
        meta: { title: '菜品管理', icon: 'Dish', permission: 'service:food' }
      },
      {
        path: 'food-order',
        name: 'FoodOrderManage',
        component: () => import('@/views/service/food-order.vue'),
        meta: { title: '订餐订单', icon: 'List', permission: 'service:food-order' }
      }
    ]
  },
  {
    path: '/operation',
    component: Layout,
    redirect: '/operation/secondhand',
    meta: { title: '运营中心', icon: 'Operation' },
    children: [
      {
        path: 'secondhand',
        name: 'SecondhandManage',
        component: () => import('@/views/operation/secondhand.vue'),
        meta: { title: '二手审核', icon: 'ShoppingCart', permission: 'operation:secondhand' }
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('@/views/operation/category.vue'),
        meta: { title: '分类管理', icon: 'Menu', permission: 'operation:category' }
      },
      {
        path: 'activity',
        name: 'ActivityManage',
        component: () => import('@/views/operation/activity.vue'),
        meta: { title: '活动管理', icon: 'Calendar', permission: 'operation:activity' }
      },
      {
        path: 'content',
        name: 'ContentManage',
        component: () => import('@/views/operation/content.vue'),
        meta: { title: '内容发布', icon: 'Document', permission: 'operation:content' }
      }
    ]
  },
  {
    path: '/finance',
    component: Layout,
    redirect: '/finance/recharge',
    meta: { title: '财务中心', icon: 'Wallet' },
    children: [
      {
        path: 'recharge',
        name: 'RechargeRecord',
        component: () => import('@/views/finance/recharge.vue'),
        meta: { title: '充值记录', icon: 'Money', permission: 'finance:recharge' }
      },
      {
        path: 'transaction',
        name: 'TransactionRecord',
        component: () => import('@/views/finance/transaction.vue'),
        meta: { title: '交易流水', icon: 'Tickets', permission: 'finance:transaction' }
      }
    ]
  },
  {
    path: '/settings',
    component: Layout,
    redirect: '/settings/admin',
    meta: { title: '系统设置', icon: 'Setting' },
    children: [
      {
        path: 'admin',
        name: 'AdminManage',
        component: () => import('@/views/settings/admin.vue'),
        meta: { title: '管理员账号', icon: 'UserFilled', permission: 'settings:admin' }
      },
      {
        path: 'role',
        name: 'RoleManage',
        component: () => import('@/views/settings/role.vue'),
        meta: { title: '角色权限', icon: 'Lock', permission: 'settings:role' }
      },
      {
        path: 'log',
        name: 'SystemLog',
        component: () => import('@/views/settings/log.vue'),
        meta: { title: '系统日志', icon: 'Document', permission: 'settings:log' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  }
]
