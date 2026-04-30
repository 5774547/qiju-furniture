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
     * 图片加载失败时，从后端代理获取 base64 并写入本地文件
     */
    onImageError() {
      const { imageUrl } = this.data;
      if (!imageUrl || !imageUrl.startsWith('http://localhost:9000')) return;

      // 从 MinIO URL 提取文件名，转成后端 base64 JSON 端点
      const parts = imageUrl.split('/');
      const filename = parts[parts.length - 1];
      const dataUrl = `http://localhost:8080/api/images/data/${filename}`;

      wx.request({
        url: dataUrl,
        success: (res) => {
          if (res.statusCode === 200 && res.data?.code === 200) {
            const dataUri = res.data.data.dataUri;
            const base64 = dataUri.split(',')[1];
            const fm = wx.getFileSystemManager();
            const tempPath = `${wx.env.USER_DATA_PATH}/img_${Date.now()}.jpg`;
            try {
              const buffer = wx.base64ToArrayBuffer(base64);
              fm.writeFileSync(tempPath, buffer);
              this.setData({ imageUrl: tempPath });
            } catch (e) {
              // 写文件失败，尝试 dataURI
              this.setData({ imageUrl: dataUri });
            }
          }
        },
        fail: () => {
          this.setData({ imageUrl: '' });
        },
      });
    },
  },
});
