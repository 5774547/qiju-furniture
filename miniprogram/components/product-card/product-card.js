/**
 * 产品卡片组件
 * 展示：图片、名称、分类标签、零售价(划掉)、批发价(突出)、"加入询价清单"按钮
 */
Component({
  /**
   * 组件属性
   */
  properties: {
    product: {
      type: Object,
      value: {},
    },
    // 是否已加入询价清单
    inInquiryList: {
      type: Boolean,
      value: false,
    },
  },

  /**
   * 组件初始数据
   */
  data: {
    imageUrl: '',
  },

  /**
   * 组件生命周期
   */
  lifetimes: {
    attached() {
      this.processImage();
    },
  },

  /**
   * 监听属性变化
   */
  observers: {
    'product.image'(val) {
      this.processImage();
    },
  },

  /**
   * 组件方法
   */
  methods: {
    /**
     * 处理图片 URL
     */
    processImage() {
      const { product } = this.data;
      if (!product || !product.image) {
        this.setData({ imageUrl: '' });
        return;
      }

      const util = require('../../utils/util');
      const api = require('../../utils/api');
      const baseUrl = api.getBaseUrl();
      const imageUrl = util.formatImageUrl(product.image, baseUrl);
      this.setData({ imageUrl });
    },

    /**
     * 点击卡片跳转产品详情
     */
    onTapCard() {
      const { product } = this.data;
      if (!product || !product.id) return;

      wx.navigateTo({
        url: `/pages/product-detail/product-detail?id=${product.id}`,
      });
    },

    /**
     * 加入询价清单
     */
    onAddToInquiry() {
      const { product, inInquiryList } = this.data;
      if (!product || !product.id) return;

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

      const api = require('../../utils/api');

      // 显示加载
      wx.showLoading({ title: '添加中...', mask: true });

      api.addToInquiryList(product.id, 1)
        .then(() => {
          wx.hideLoading();
          wx.showToast({ title: '已加入询价清单', icon: 'success' });
          this.setData({ inInquiryList: true });
          // 通知父页面刷新
          this.triggerEvent('addinquiry', { productId: product.id });
        })
        .catch((err) => {
          wx.hideLoading();
          console.error('加入询价清单失败:', err);
        });
    },

    /**
     * 图片加载失败时，通过后端代理下载
     */
    onImageError() {
      const { product, imageUrl } = this.data;
      if (!product || !product.image) return;
      // 只对 HTTP 代理图片做下载重试
      if (!imageUrl || !imageUrl.startsWith('http://')) return;
      wx.downloadFile({
        url: imageUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            this.setData({ imageUrl: res.tempFilePath });
          }
        },
        fail: () => {
          this.setData({ imageUrl: '' });
        },
      });
    },
  },
});
