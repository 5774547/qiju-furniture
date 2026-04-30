/**
 * 产品详情页
 * 轮播图、价格、规格参数、评价、加入询价清单
 */
const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    product: {},
    images: [],
    specs: [],
    reviews: [],
    reviewsExpanded: false,
    inInquiryList: false,
    loading: true,
    productId: null,
  },

  onLoad(options) {
    const id = options.id;
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      wx.navigateBack();
      return;
    }

    this.setData({ productId: id });
    this.loadProductDetail(id);
    this.loadReviews(id);
    this.checkInquiryStatus(id);

    // 启用分享
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline'],
    });
  },

  /**
   * 分享给好友
   */
  onShareAppMessage() {
    const { product } = this.data;
    return {
      title: product.name || '栖居家具 - 产品详情',
      path: `/pages/product-detail/product-detail?id=${product.id}`,
      imageUrl: this.data.images[0] || '',
    };
  },

  /**
   * 分享到朋友圈
   */
  onShareTimeline() {
    const { product } = this.data;
    return {
      title: product.name || '栖居家具 - 产品详情',
      query: `id=${product.id}`,
      imageUrl: this.data.images[0] || '',
    };
  },

  /**
   * 加载产品详情
   */
  async loadProductDetail(id) {
    this.setData({ loading: true });
    try {
      const product = await api.getProduct(id);
      if (!product) {
        throw new Error('产品不存在');
      }

      // 处理图片列表
      const baseUrl = api.getBaseUrl();
      let images = [];
      if (product.images && Array.isArray(product.images)) {
        images = product.images.map(img => util.formatImageUrl(img, baseUrl));
      } else if (product.image) {
        images = [util.formatImageUrl(product.image, baseUrl)];
      }

      // 处理规格参数（JSON 对象转数组）
      let specs = [];
      if (product.specs) {
        if (typeof product.specs === 'string') {
          try {
            const parsed = JSON.parse(product.specs);
            specs = Object.entries(parsed).map(([key, value]) => ({ key, value }));
          } catch (e) {
            specs = [];
          }
        } else if (typeof product.specs === 'object') {
          specs = Object.entries(product.specs).map(([key, value]) => ({ key, value }));
        }
      }

      this.setData({
        product,
        images,
        specs,
        loading: false,
      });
    } catch (err) {
      console.error('加载产品详情失败:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
      this.setData({ loading: false });
    }
  },

  /**
   * 加载评价
   */
  async loadReviews(productId) {
    try {
      const reviews = await api.getReviews(productId);
      // WXML doesn't support .repeat(), pre-compute stars
      if (reviews && reviews.length) {
        reviews.forEach(r => { r.stars = '★'.repeat(r.rating || 5); });
      }
      this.setData({ reviews: reviews || [] });
    } catch (err) {
      console.error('加载评价失败:', err);
      this.setData({ reviews: [] });
    }
  },

  /**
   * 检查是否已在询价清单中
   */
  async checkInquiryStatus(productId) {
    try {
      const list = await api.getInquiryList().catch(() => []);
      const inList = (list || []).some(
        item => item.productId === Number(productId) || item.product?.id === Number(productId)
      );
      this.setData({ inInquiryList: inList });
    } catch (err) {
      console.error('检查询价清单状态失败:', err);
    }
  },

  /**
   * 切换评价折叠
   */
  onToggleReviews() {
    this.setData({ reviewsExpanded: !this.data.reviewsExpanded });
  },

  /**
   * 加入询价清单
   */
  onAddToInquiry() {
    const { product, inInquiryList } = this.data;

    // 检查登录
    const auth = require('../../utils/auth');
    if (!auth.ensureLoggedIn()) {
      return;
    }

    if (inInquiryList) {
      // 已在清单中，跳转到询价清单页
      wx.switchTab({
        url: '/pages/inquiry-list/inquiry-list',
      });
      return;
    }

    wx.showLoading({ title: '添加中...', mask: true });

    api.addToInquiryList(product.id, 1)
      .then(() => {
        wx.hideLoading();
        wx.showToast({ title: '已加入询价清单', icon: 'success' });
        this.setData({ inInquiryList: true });

        // 刷新全局数量
        const app = getApp();
        if (app && app.refreshInquiryCount) {
          app.refreshInquiryCount();
        }
      })
      .catch((err) => {
        wx.hideLoading();
        console.error('加入询价清单失败:', err);
      });
  },

  /**
   * 图片加载失败时，用 downloadFile 下载到本地再显示
   */
  onImageError(e) {
    const url = e.target.dataset.src || e.currentTarget.dataset.src || '';
    const index = e.currentTarget.dataset.index;
    if (!url || !url.startsWith('http://') || index === undefined) return;

    wx.downloadFile({
      url: url,
      success: (res) => {
        if (res.statusCode === 200) {
          const images = [...this.data.images];
          images[index] = res.tempFilePath;
          this.setData({ images });
        }
      },
    });
  },

  /**
   * 格式化价格
   */
  formatPrice(price) {
    return util.formatPrice(price);
  },

  /**
   * 格式化时间
   */
  formatTime(timestamp) {
    return util.formatTime(timestamp);
  },
});
