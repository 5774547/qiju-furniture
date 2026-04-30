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
      let url = util.formatImageUrl(product.image, baseUrl);

      // 本地路径或 HTTPS 直接使用
      if (!url.startsWith('http://')) {
        this.setData({ imageUrl: url });
        return;
      }

      // HTTP 图片：用 wx.request 获取 base64 data URI
      // 注：wx.request 到 localhost:8080 是通的（API 正常返回），但下载二进制会超时
      // 改用后端返回 base64 JSON 的方式
      const cacheKey = 'img_cache_' + url;
      const cached = wx.getStorageSync(cacheKey);
      if (cached) {
        this.setData({ imageUrl: cached });
        return;
      }

      // 将 /api/images/xxx 转为 /api/images/data/xxx
      const dataUrl = url.replace('/api/images/', '/api/images/data/');
      wx.request({
        url: dataUrl,
        success: (res) => {
          if (res.statusCode === 200 && res.data?.code === 200) {
            const dataUri = res.data.data.dataUri;
            try {
              wx.setStorageSync(cacheKey, dataUri);
            } catch (e) {}
            this.setData({ imageUrl: dataUri });
          }
        },
        fail: () => {
          this.setData({ imageUrl: '' });
        }
      });
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
     * 阻止图片加载失败
     */
    onImageError() {
      this.setData({ imageUrl: '' });
    },
  },
});
