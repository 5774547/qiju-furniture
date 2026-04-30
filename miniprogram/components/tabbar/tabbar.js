/**
 * 底部导航组件
 * 首页 / 询价清单 / 我的
 * 支持 badge 红点提醒
 */
Component({
  /**
   * 组件属性
   */
  properties: {
    // 当前选中项索引 (0=首页, 1=询价清单, 2=我的)
    current: {
      type: Number,
      value: 0,
    },
    // 询价清单数量（角标）
    inquiryCount: {
      type: Number,
      value: 0,
    },
  },

  /**
   * 组件初始数据
   */
  data: {
    list: [
      {
        index: 0,
        text: '首页',
        iconPath: '/assets/icons/home.png',
        selectedIconPath: '/assets/icons/home-active.png',
        pagePath: '/pages/index/index',
      },
      {
        index: 1,
        text: '询价清单',
        iconPath: '/assets/icons/inquiry.png',
        selectedIconPath: '/assets/icons/inquiry-active.png',
        pagePath: '/pages/inquiry-list/inquiry-list',
      },
      {
        index: 2,
        text: '我的',
        iconPath: '/assets/icons/mine.png',
        selectedIconPath: '/assets/icons/mine-active.png',
        pagePath: '/pages/mine/mine',
      },
    ],
  },

  /**
   * 组件方法
   */
  methods: {
    /**
     * 切换 tab
     */
    onSwitchTab(e) {
      const { index, pagePath } = e.currentTarget.dataset;
      const { current } = this.data;

      // 如果已经是当前 tab，不做任何事
      if (index === current) return;

      // 触发自定义事件
      this.triggerEvent('tabchange', { index, pagePath });

      // 使用 switchTab 跳转
      wx.switchTab({
        url: pagePath,
      });
    },
  },
});
