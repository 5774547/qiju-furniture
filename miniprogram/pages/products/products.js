/**
 * 产品列表页
 * 分类切换、搜索过滤、分页加载
 */
const api = require('../../utils/api');

Page({
  data: {
    categories: [],
    currentCategory: '',
    keyword: '',
    products: [],
    page: 1,
    size: 20,
    total: 0,
    hasMore: true,
    loading: false,
    inquiryProductIds: new Set(),
  },

  onLoad(options) {
    // 如果从首页传入了分类参数
    if (options.category) {
      this.setData({ currentCategory: options.category });
    }
    this.loadCategories();
    this.loadProducts(true);
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.setData({ page: 1, products: [], hasMore: true });
    this.loadProducts(true).finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 上拉加载更多
   */
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts(false);
    }
  },

  /**
   * 加载分类
   */
  async loadCategories() {
    try {
      const categories = await api.getCategories();
      this.setData({ categories: categories || [] });
    } catch (err) {
      console.error('加载分类失败:', err);
      // 使用默认分类
      this.setData({
        categories: [
          { name: '沙发' }, { name: '桌子' },
          { name: '椅子' }, { name: '床' },
          { name: '柜子' }, { name: '灯饰' },
        ],
      });
    }
  },

  /**
   * 加载产品列表
   * @param {boolean} refresh - 是否刷新
   */
  async loadProducts(refresh = false) {
    if (this.data.loading) return;

    const { page, size, currentCategory, keyword, products } = this.data;
    const targetPage = refresh ? 1 : page;

    this.setData({ loading: true });

    try {
      const params = { page: targetPage, size };
      if (currentCategory) {
        params.category = currentCategory;
      }
      if (keyword.trim()) {
        params.keyword = keyword.trim();
      }

      const res = await api.getProducts(params);
      const records = res.records || [];

      // 获取询价清单状态
      const inquiryList = await api.getInquiryList().catch(() => []);
      const inquiryProductIds = new Set(
        (inquiryList || []).map(item => item.productId || item.product?.id)
      );

      const formattedRecords = records.map(p => ({
        ...p,
        inInquiryList: inquiryProductIds.has(p.id),
      }));

      this.setData({
        products: refresh ? formattedRecords : [...products, ...formattedRecords],
        total: res.total || 0,
        page: targetPage + 1,
        hasMore: records.length >= size,
        loading: false,
        inquiryProductIds,
      });
    } catch (err) {
      console.error('加载产品列表失败:', err);
      this.setData({ loading: false });
    }
  },

  /**
   * 搜索输入
   */
  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  /**
   * 执行搜索
   */
  onSearch() {
    this.setData({ page: 1, products: [], hasMore: true });
    this.loadProducts(true);
  },

  /**
   * 清除搜索关键字
   */
  onClearKeyword() {
    this.setData({ keyword: '', page: 1, products: [], hasMore: true });
    this.loadProducts(true);
  },

  /**
   * 点击分类 Tab
   */
  onCategoryTabTap(e) {
    const category = e.currentTarget.dataset.category;
    if (this.data.currentCategory === category) return;

    this.setData({
      currentCategory: category,
      page: 1,
      products: [],
      hasMore: true,
    });
    this.loadProducts(true);
  },

  /**
   * 加入询价清单回调
   */
  onAddInquiry() {
    const app = getApp();
    if (app && app.refreshInquiryCount) {
      app.refreshInquiryCount();
    }
  },
});
