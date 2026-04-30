/**
 * 首页
 * 展示分类导航、热门产品、工厂介绍
 */
const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    categories: [],
    hotProducts: [],
    loading: true,
  },

  onLoad() {
    this.loadData();
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadData().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 加载首页数据
   */
  async loadData() {
    this.setData({ loading: true });

    try {
      // 并行加载分类和热门产品
      const [categories, productsData] = await Promise.all([
        api.getCategories().catch(() => []),
        api.getProducts({ page: 1, size: 10 }).catch(() => ({ records: [] })),
      ]);

      // 处理分类数据 - 添加图标
      const categoryIcons = {
        '沙发': '/assets/icons/sofa.png',
        '桌子': '/assets/icons/table.png',
        '椅子': '/assets/icons/chair.png',
        '床': '/assets/icons/bed.png',
        '柜子': '/assets/icons/cabinet.png',
        '灯饰': '/assets/icons/lighting.png',
      };

      const formattedCategories = (categories || []).map(cat => ({
        ...cat,
        icon: categoryIcons[cat.name] || '/assets/icons/category-default.png',
      }));

      // 确保至少有默认分类显示
      const defaultCategories = [
        { name: '沙发', count: 0, icon: categoryIcons['沙发'] },
        { name: '桌子', count: 0, icon: categoryIcons['桌子'] },
        { name: '椅子', count: 0, icon: categoryIcons['椅子'] },
        { name: '床', count: 0, icon: categoryIcons['床'] },
        { name: '柜子', count: 0, icon: categoryIcons['柜子'] },
        { name: '灯饰', count: 0, icon: categoryIcons['灯饰'] },
      ];

      // 检查询价清单状态（仅登录时）
      const records = productsData.records || [];
      let inquiryProductIds = new Set();
      try {
        const token = wx.getStorageSync('token');
        if (token) {
          const inquiryList = await api.getInquiryList();
          inquiryProductIds = new Set(
            (inquiryList || []).map(item => item.productId || item.product?.id)
          );
        }
      } catch (e) {
        // 未登录时跳过
      }
      const hotProducts = records.map(p => ({
        ...p,
        inInquiryList: inquiryProductIds.has(p.id),
      }));

      this.setData({
        categories: formattedCategories.length > 0 ? formattedCategories : defaultCategories,
        hotProducts,
        loading: false,
      });
    } catch (err) {
      console.error('加载首页数据失败:', err);
      // 显示默认分类
      this.setData({
        categories: [
          { name: '沙发', icon: '/assets/icons/sofa.png' },
          { name: '桌子', icon: '/assets/icons/table.png' },
          { name: '椅子', icon: '/assets/icons/chair.png' },
          { name: '床', icon: '/assets/icons/bed.png' },
          { name: '柜子', icon: '/assets/icons/cabinet.png' },
          { name: '灯饰', icon: '/assets/icons/lighting.png' },
        ],
        hotProducts: [],
        loading: false,
      });
    }
  },

  /**
   * 点击搜索
   */
  onSearchTap() {
    wx.navigateTo({
      url: '/pages/products/products',
    });
  },

  /**
   * 点击分类
   */
  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category;
    wx.navigateTo({
      url: `/pages/products/products?category=${encodeURIComponent(category)}`,
    });
  },

  /**
   * 查看全部产品
   */
  onViewAllProducts() {
    wx.navigateTo({
      url: '/pages/products/products',
    });
  },

  /**
   * 加入询价清单回调
   */
  onAddInquiry() {
    // 刷新询价清单数量
    const app = getApp();
    if (app && app.refreshInquiryCount) {
      app.refreshInquiryCount();
    }
  },
});
