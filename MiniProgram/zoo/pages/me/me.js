const app = getApp();
Page({
  data: {
    avatarUrl: '/images/avatar.png',
    username: '点击登录',
    userInfo: {},
    hasUserInfo: false,
  },
  onShareAppMessage(res) {
    return {
      title: '分享给朋友',
      path: 'pages/index/index'
    }
  },
  onLoad: function () {},

  onShow: function () {
    // 使用 wx.getStorageSync 获取用户信息
    let userinfo = wx.getStorageSync('userinfo');
    // 检查 userinfo 是否存在且不为空对象
    if (userinfo && Object.keys(userinfo).length > 0) {
      this.setData({
        userInfo: userinfo,
        hasUserInfo: true
      });
    } else {
      this.setData({
        hasUserInfo: false
      });
    }
  },

  login() {
    // 使用wx.getUserProfile获取用户信息，开发者通过该接口获取用户个人信息需用户确认
    // 开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
    wx.getUserProfile({
      desc: '用于获取信息', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res) => {
        // 1. 获取微信登录凭证 code
        wx.login({
          success: (loginRes) => {
            if (loginRes.code) {
              wx.request({
                url: app.globalData.url + '/wxLogin',
                method: 'POST',
                data: {
                  code: loginRes.code,
                  userInfo: res.userInfo
                },
                success: (resp) => {
                  if (resp.data.data.isBanned == '是') {
                    wx.showToast({
                      title: '该用户被禁用，无法登录'
                    })
                  } else {
                    this.setData({
                      hasUserInfo: true,
                    })
                    // 存储登录态 token
                    wx.setStorageSync('token', resp.data.data.token)
                    wx.setStorageSync('userinfo', resp.data.data)
                    wx.showToast({
                      title: '登录成功'
                    })
                  }
                },
                fail: res => {
                  wx.showToast({
                    title: '登录失败'
                  })
                }
              })
            }
          }
        })
      },
      fail: res => {
        console.log(res)
      }
    })
  },

  userDetail() {
    wx.navigateTo({
      url: '/pages/subPages/user/detail/detail' // 替换为你要跳转的页面的路径
    });
  },

  // 查看全部订单
  viewAllOrders() {
    wx.navigateTo({
      url: '/pages/subPages/order/list/list'
    });
  },

  // 带状态跳转
  navigateToFilteredOrders(e) {
    const status = e.currentTarget.dataset.status;
    wx.navigateTo({
      url: '/pages/subPages/order/list/list?status=' + status
    });
  }

})