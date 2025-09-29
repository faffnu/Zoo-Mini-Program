// pages/subPages/order/list/list.js
const app = getApp()
Page({
  data: {
    currentStatus: '',
    orders: [],
    filteredOrders: [],
    order: {},
    //倒计时相关数据
    countdowns: {},
    timer: null
  },

  onLoad(options) {
    this.setData({
      currentStatus: options.status || ''
    });
  },
  onShow() {
    this.loadOrders();
    this.tradeQuery();
    // 启动定时器
    this.startCountdownTimer();
  },


  onHide() {
    // 页面隐藏时清除定时器
    if (this.data.timer) {
      clearInterval(this.data.timer);
      this.setData({
        timer: null
      });
    }
  },

  onUnload() {
    // 页面卸载时清除定时器
    if (this.data.timer) {
      clearInterval(this.data.timer);
      this.setData({
        timer: null
      });
    }
  },

  // 启动倒计时定时器
  startCountdownTimer() {
    if (this.data.timer) {
      clearInterval(this.data.timer);
    }

    const timer = setInterval(() => {
      this.updateCountdowns();
    }, 1000);

    this.setData({
      timer
    });
  },

  // 更新所有订单的倒计时
  updateCountdowns() {
    const {
      orders,
      countdowns
    } = this.data;
    let updated = false;
    const newCountdowns = {
      ...countdowns
    };

    orders.forEach(order => {
      if (order.state === '待支付') {
        // 计算订单创建时间
        const createTime = new Date(order.createTime.replace(/-/g, '/')).getTime();
        const now = new Date().getTime();
        const elapsed = Math.floor((now - createTime) / 1000); // 已过秒数
        const remaining = 3600 - elapsed; // 剩余秒数(1小时=3600秒)

        if (remaining <= 0) {
          // 订单已超时，更新状态
          this.updateOrderState(order.id, '已取消');
          newCountdowns[order.id] = '已超时';
          updated = true;
        } else {
          // 更新倒计时显示
          const minutes = Math.floor(remaining / 60);
          const seconds = remaining % 60;
          newCountdowns[order.id] = `${minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
          updated = true;
        }
      }
    });

    if (updated) {
      this.setData({
        countdowns: newCountdowns
      });
    }
  },

  // 更新订单状态
  updateOrderState(orderId, newState) {
    const {
      orders
    } = this.data;
    const updatedOrders = orders.map(order => {
      if (order.id === orderId) {
        return {
          ...order,
          state: newState
        };
      }
      return order;
    });

    this.setData({
      orders: updatedOrders,
      filteredOrders: this.filterOrders(this.data.currentStatus)
    });
  },

  switchStatus(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({
      currentStatus: status,
      filteredOrders: this.filterOrders(status)
    });
  },

  filterOrders(status) {
    return status ?
      this.data.orders.filter(item => item.state === status) :
      this.data.orders;
  },

  loadOrders() {
    var that = this;
    var user = wx.getStorageSync('userinfo')
    wx.request({
      url: app.globalData.url + '/order/selectAll',
      header: {
        token: wx.getStorageSync('token')
      },
      data: {
        userId: user.id,
      },
      success: function (res) {
        if (res.data.code === "200") {
          that.setData({
            orders: res.data.data,
          });
          that.setData({
            filteredOrders: that.filterOrders(that.data.currentStatus)
          });
          // 初始化倒计时数据
          that.initCountdowns(res.data.data);
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
    })
  },

  // 初始化倒计时数据
  initCountdowns(orders) {
    const countdowns = {};
    orders.forEach(order => {
      if (order.state === '待支付') {
        const createTime = new Date(order.createTime.replace(/-/g, '/')).getTime();
        const now = new Date().getTime();
        const elapsed = Math.floor((now - createTime) / 1000);
        const remaining = 3600 - elapsed;
        if (remaining <= 0) {
          countdowns[order.id] = '已超时';
        } else {
          const minutes = Math.floor(remaining / 60);
          const seconds = remaining % 60;
          countdowns[order.id] = `${minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
        }
      }
    });
    this.setData({
      countdowns
    });
  },

  pay(e) {
    let that = this;
    const id = e.currentTarget.dataset.id;
    wx.showLoading({
      title: '加载中...'
    });
    wx.request({
      url: app.globalData.url + '/order/selectById/' + id,
      method: 'GET',
      header: {
        token: wx.getStorageSync('token')
      },
      success(res) {
        that.setData({
          order: res.data.data,
        })
      }
    });
    // 发起支付请求
    wx.request({
      url: app.globalData.url + '/alipay/createOrder',
      method: 'POST',
      header: {
        token: wx.getStorageSync('token')
      },
      data: that.data.order,
      success(res) {
        const orderNumber = res.data.data;
        // 跳转到WebView页面，加载支付页
        wx.navigateTo({
          url: '/pages/subPages/webview/webview?orderNumber=' + orderNumber
        });
      }
    });
  },
  cancelPay(e) {
    let that = this;
    const id = e.currentTarget.dataset.id;
    let orders = that.data.orders;
    orders = orders.map(order => {
      if (order.id === id) {
        return {
          ...order,
          state: '已取消'
        }; // 更新状态为 '已取消'
      }
      return order;
    });
    wx.request({
      url: app.globalData.url + '/order/cancel/' + id,
      method: "PUT",
      header: {
        token: wx.getStorageSync('token')
      },
      success(res) {
        if (res.data.code == 200) {
          that.setData({
            orders: orders,
          });
          that.setData({
            filteredOrders: that.filterOrders(that.data.currentStatus)
          })
        }
      }
    });
  },

  tradeQuery() {
    let that = this;
    if (that.data.order != null && Object.keys(that.data.order).length > 0) {
      // 发起支付查询请求
      wx.request({
        url: app.globalData.url + '/alipay/tradeQuery',
        method: 'POST',
        data: that.data.order,
        success(res) {
          if (res.data.code == 200) {
            if (res.data.data == '支付成功') {
              let orders = that.data.orders;
              orders = orders.map(order => {
                if (order.id === that.data.order.id) {
                  return {
                    ...order,
                    state: '待使用'
                  };
                }
                return order;
              });
              that.setData({
                orders: orders,
              });
              that.setData({
                filteredOrders: that.filterOrders(that.data.currentStatus),
                order: {}
              })
            }
            wx.showToast({
              title: res.data.data,
            })


          }
        }
      })
    }


  },

  refund(e) {
    let that = this;
    const id = e.currentTarget.dataset.id;
    let order = null;
    wx.showModal({
      title: '确认退款',
      content: '您确定要申请退款吗？',
      cancelText: '取消',
      confirmText: '确定',
      complete: (res) => {
        if (res.cancel) {}
        if (res.confirm) {
          wx.request({
            url: app.globalData.url + '/order/selectById/' + id,
            method: 'GET',
            header: {
              token: wx.getStorageSync('token')
            },
            success(res) {
              order = res.data.data;
              if (order != null) {
                // 发起退款请求
                wx.request({
                  url: app.globalData.url + '/alipay/refund',
                  method: 'POST',
                  header: {
                    token: wx.getStorageSync('token')
                  },
                  data: order,
                  success(res) {
                    if (res.data.code == 200) {
                      if (res.data.data == '退款成功') {
                        let orders = that.data.orders;
                        orders = orders.map(order => {
                          if (order.id === id) {
                            return {
                              ...order,
                              state: '已退款'
                            };
                          }
                          return order;
                        });
                        that.setData({
                          orders: orders,
                        });
                        that.setData({
                          filteredOrders: that.filterOrders(that.data.currentStatus)
                        })
                      }
                      wx.showToast({
                        title: res.data.data,
                      })

                    }
                  },
                })
              }
            }
          });
        }
      }
    })




  },

  useTicket(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/subPages/order/detail/detail?id=' + id
    });
  },

  comment(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/subPages/comment/edit/comment?id=' + id
    });
  },

});