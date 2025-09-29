// pages/subPages/order/detail/detail.js
import drawQrcode from '../../../../utils/weapp.qrcode.esm.js'
const app = getApp()
Page({
  data: {
    order: null,
    qrcodeUrl: '' // 二维码图片URL
  },

  onLoad(options) {
    const orderId = options.id;
    this.loadOrderDetail(orderId);
    this.generateQRCode();
  },

  // 加载订单详情
  loadOrderDetail(id) {
    wx.showLoading({
      title: '加载中...'
    });
    wx.request({
      url: app.globalData.url + '/order/selectById/' + id,
      method: 'GET',
      header: {
        token: wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data.code == '200') {
          this.setData({
            order: res.data.data
          }, () => {
            this.generateQRCode(res.data.data.orderNumber); // 生成二维码
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  generateQRCode(content) {
    drawQrcode({
      width: 200,
      height: 200,
      canvasId: 'myQrcode',
      text: content,
      image: {
        imageResource: '../../../../images/door.png',
        dx: 70,
        dy: 70,
        dWidth: 60,
        dHeight: 60
      }
    })
  },

  check() {
    let that = this;
    const id = that.data.order.id;
    wx.request({
      url: app.globalData.url + '/order/check/' + id,
      method: "PUT",
      header: {
        token: wx.getStorageSync('token')
      },
      success(res) {
        if (res.data.code == 200) {
          wx.showToast({
            title: '核销成功',
          })
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);

        }
      }
    });
  }

});