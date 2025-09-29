// pages/subPages/ticket/detail/detail.js
const app = getApp();

Page({
  /**
   * 页面的初始数据
   */
  data: {
    ticketinfo: {},
    showModalStatus: false,
    userinfo: {},
    date: '2025-05-21',
    num: 1,
    totalprice: 0,
    finalPrice: 0,
    discountAmount: 0,
    selectedVoucher: null,
    availableVouchers: [],
    showVoucherModal: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      userinfo: wx.getStorageSync('userinfo'),
    })

    let that = this;
    let id = options.id;
    if (id !== null) {
      wx.request({
        url: app.globalData.url + '/ticket/selectById/' + id,
        method: 'GET',
        success: function (res) {
          if (res.data.code == 200) {
            that.setData({
              ticketinfo: res.data.data,
              totalprice: res.data.data.price,
              finalPrice: res.data.data.price
            })
          }
        }
      })
    }
  },

  /**
   * 获取用户可用优惠券
   */
  getAvailableVouchers() {
    const that = this;
    const userId = this.data.userinfo.id;
    wx.request({
      url: app.globalData.url + '/userVoucher/usable/' + userId,
      method: 'GET',
      header: {
        token: wx.getStorageSync('token')
      },
      success: function (res) {
        if (res.data.code == 200) {
          that.setData({
            availableVouchers: res.data.data
          });
        }
      }
    });
  },

  /**
   * 显示优惠券选择模态框
   */
  showVoucherModal() {
    this.getAvailableVouchers();
    this.setData({
      showVoucherModal: true
    });
  },

  /**
   * 隐藏优惠券选择模态框
   */
  hideVoucherModal() {
    this.setData({
      showVoucherModal: false
    });
  },

  /**
   * 选择优惠券
   */
  selectVoucher(e) {
    const voucher = e.currentTarget.dataset.voucher;
    this.setData({
      selectedVoucher: voucher
    });
  },

  /**
   * 确认选择优惠券
   */
  confirmVoucher() {
    this.calculateFinalPrice();
    this.hideVoucherModal();
  },

  /**
   * 计算最终价格
   */
  calculateFinalPrice() {
    const {
      totalprice,
      selectedVoucher,
      num
    } = this.data;
    const originalTotal = totalprice * num;
    let finalPrice = originalTotal;
    let discountAmount = 0;
    if (selectedVoucher != null) {
      const voucher = selectedVoucher.voucherInfo || selectedVoucher;
      if (voucher.type == 1) { // 折扣券
        finalPrice = originalTotal * voucher.value;
        discountAmount = originalTotal - finalPrice;
      } else if (voucher.type == 2) { // 满减券
        if (originalTotal >= voucher.minAmount) {
          finalPrice = originalTotal - voucher.value;
          discountAmount = voucher.value;
          // 确保最终价格不会为负数
          if (finalPrice < 0) {
            finalPrice = 0;
            discountAmount = originalTotal;
          }
        }
      }
    }

    this.setData({
      finalPrice: finalPrice.toFixed(2),
      discountAmount: discountAmount.toFixed(2)
    });
  },

  /**
   * 设置数量
   */
  setValue: function (e) {
    var that = this;
    if (e.detail.value <= 0) {
      wx.showToast({
        title: '数量不能小于等于0哦！',
        icon: "none",
        duration: 500
      })
      var total = this.data.ticketinfo.price
      this.setData({
        num: 1,
        totalprice: total,
      }, () => {
        this.calculateFinalPrice();
      })
    } else {
      var total = this.data.ticketinfo.price
      console.log(total)
      this.setData({
        num: e.detail.value,
        totalprice: total,
      }, () => {
        this.calculateFinalPrice();
      });
    }
  },

  /**
   * 增加数量
   */
  addJia: function (e) {
    var conum;
    var that = this;
    this.data.num++;
    conum = that.data.num
    var total = this.data.ticketinfo.price
    that.setData({
      num: conum,
      totalprice: total,
    }, () => {
      this.calculateFinalPrice();
    })
  },

  /**
   * 减少数量
   */
  addjian: function (e) {
    var jian;
    var that = this;
    this.data.num--;
    jian = this.data.num;
    var total = this.data.ticketinfo.price
    that.setData({
      num: jian,
      totalprice: total,
    }, () => {
      this.calculateFinalPrice();
    })
    if (jian <= 0) {
      wx.showToast({
        title: '数量不能为0哦！',
        icon: "none",
        duration: 500
      })
      jian = 1;
      var total = jian * this.data.ticketinfo.price;
      that.setData({
        num: jian,
        totalprice: total,
      }, () => {
        this.calculateFinalPrice();
      })
    }
  },

  /**
   * 日期选择
   */
  bindDateChange: function (e) {
    this.setData({
      date: e.detail.value
    })
  },

  /**
   * 打开/关闭抽屉
   */
  powerDrawer: function (e) {
    if (this.data.userinfo != null && Object.keys(this.data.userinfo).length > 0) {
      var currentStatu = e.currentTarget.dataset.statu;
      this.util(currentStatu)
    } else {
      wx.showToast({
        title: '请先登录',
        duration: 1500,
      });
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/me/me',
        });
      }, 1500);
    }
  },

  util: function (currentStatu) {
    var animation = wx.createAnimation({
      duration: 200,
      timingFunction: "linear",
      delay: 0
    });

    this.animation = animation;
    animation.translateY(240).step();

    this.setData({
      animationData: animation.export()
    })

    setTimeout(function () {
      animation.translateY(0).step()
      this.setData({
        animationData: animation
      })

      if (currentStatu == "close") {
        this.setData({
          showModalStatus: false
        });
      }
    }.bind(this), 200)

    if (currentStatu == "open") {
      this.setData({
        showModalStatus: true
      });
    }
  },

  /**
   * 提交订单
   */
  bindSave: function (e) {
    var that = this;
    if (that.data.num <= 0) {
      wx.showModal({
        title: '提示',
        content: '数量不能小于等于0',
        showCancel: false
      })
      return
    }

    const orderData = {
      number: that.data.num,
      totalPrice: that.data.totalprice * that.data.num,
      useDate: that.data.date,
      userId: wx.getStorageSync('userinfo').id,
      ticketId: that.data.ticketinfo.id,
      voucherId: that.data.selectedVoucher ? that.data.selectedVoucher.voucherId : null,
      discountAmount: that.data.discountAmount
    };

    wx.request({
      url: app.globalData.url + '/order/add',
      method: 'POST',
      data: orderData,
      header: {
        token: wx.getStorageSync('token')
      },
      success: function (res) {
        if (res.data.code == 200) {
          console.log(res.data)
          if (that.data.selectedVoucher != null) {
            wx.request({
              url: app.globalData.url + '/userVoucher/use/' + that.data.selectedVoucher.id + '?orderId=' + res.data.data.id,
              method: 'POST',
              header: {
                token: wx.getStorageSync('token')
              },
              success: function (result) {}
            })
          }
          wx.showModal({
            title: '订单创建成功',
            content: '订单已创建，请在1小时内完成支付，超时订单将自动取消',
            showCancel: false,
            success: () => {
              setTimeout(() => {
                wx.switchTab({
                  url: '/pages/me/me',
                });
              }, 1500);
            }
          });
        }
        if (res.statusCode === 401) {
          wx.showModal({
            title: '提示',
            content: 'token无效，请重新登录',
            showCancel: false,
            success(res) {
              wx.removeStorageSync('userinfo');
              wx.removeStorageSync('token');
              if (res.confirm) {
                wx.switchTab({
                  url: '/pages/me/me',
                })
              }
            }
          })
        }
      },
      fail: function (res) {
        console.log(res);
      }
    })
  },

  pay(order) {
    let that = this;
    wx.request({
      url: app.globalData.url + '/alipay/createOrder',
      method: 'POST',
      header: {
        token: wx.getStorageSync('token')
      },
      data: order,
      success(res) {
        const orderNumber = res.data.data;
        wx.navigateTo({
          url: '/pages/subPages/webview/webview?orderNumber=' + orderNumber
        });
      }
    });
  },
})