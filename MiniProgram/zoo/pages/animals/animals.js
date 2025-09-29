const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    animalList: [],
    isLoad: false,
    page: 1,
    pageSize: 10,
    categoryId: "",
    inputVal: "",
    type: "",
    categories: [],
    location: null,
    hasLocation: false,
    maxDistance: 100000 // 默认100公里
  },

  onCategoryTap: function (event) {
    this.setData({
      type: event.currentTarget.dataset.type,
    })
    this.onPullDownRefresh();
  },

  clearInput: function (e) {
    this.setData({
      inputVal: "",
      type: "",
    });
    this.getGoodsList();
  },

  bindInput: function (e) {
    this.setData({
      inputVal: e.detail.value
    })
    this.getGoodsList();
  },

  // 获取地理位置
  getLocation: function () {
    const that = this;
    wx.showLoading({
      title: '获取位置中',
    });

    wx.getLocation({
      type: 'gcj02',
      success: function (res) {
        const latitude = res.latitude;
        const longitude = res.longitude;
        that.setData({
          location: {
            latitude: latitude,
            longitude: longitude
          },
          hasLocation: true
        });
        wx.hideLoading();
        that.getGoodsList();
      },
      fail: function (err) {
        console.error('获取位置失败', err);
        wx.hideLoading();
        wx.showModal({
          title: '提示',
          content: '需要您授权位置信息才能使用附近功能',
          showCancel: false
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    //修改search的this指向
    this.setData({
      search: this.search.bind(this)
    })
    this.getTypeList();
    this.getGoodsList();
  },

  search: function (value) {
    return new Promise((resolve, reject) => {
      resolve()
    })
  },

  // 选择结果的函数
  selectResult(e) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    wx.showNavigationBarLoading()
    this.setData({
      page: 1
    });
    this.getGoodsList();
    setTimeout(function () {
      wx.hideNavigationBarLoading(); //完成停止加载
      wx.stopPullDownRefresh(); //停止下拉刷新
    }, 1000);
  },

  loadMore: function () {
    var that = this;
    var isLoad = this.data.isLoad;
    if (!isLoad) {
      this.setData({
        page: that.data.page + 1
      });
      this.getGoodsList();
    }
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.loadMore();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

  getTypeList: function () {
    var that = this;
    wx.request({
      url: app.globalData.url + '/type/selectAll',
      success: function (res) {
        if (res.data.code == "200") {
          that.setData({
            categories: res.data.data
          });
        }
      }
    })
  },

  getGoodsList: function () {
    var that = this;
    that.setData({
      isLoad: true
    });

    // 准备请求参数
    const params = {
      name: that.data.inputVal,
      type: that.data.type,
      pageNum: that.data.page,
      pageSize: that.data.pageSize,
      maxDistance: that.data.maxDistance // 默认100公里
    };

    // 如果有位置信息
    if (that.data.location) {
      params.x = that.data.location.longitude;
      params.y = that.data.location.latitude;
    }

    wx.request({
      url: app.globalData.url + '/animal/selectPage',
      data: params,
      success: function (res) {
        if (res.data.code !== "200") {
          that.setData({
            isLoad: false
          });
          return;
        }
        if (that.data.page == 1) {
          that.setData({
            animalList: []
          });
        }
        let newList = res.data.data.list || [];
        // 处理距离显示：不保留小数，单位为米
        newList = newList.map(item => {
          if (item.distance !== null && item.distance !== undefined) {
            // 将距离转换为整数（不保留小数）
            item.distance = Math.round(item.distance) + '米';
          }
          return item;
        });
        // 如果没有定位，清除距离数据
        if (!that.data.location) {
          newList.forEach(item => {
            item.distance = null;
          });
        }
        if (newList.length == 0) {
          that.setData({
            isLoad: true
          });
          return;
        }
        var animal = that.data.animalList;
        for (var i = 0; i < newList.length; i++) {
          animal.push(newList[i]);
        }
        that.setData({
          animalList: animal,
          isLoad: false
        });
      },
      fail: function (err) {
        that.setData({
          isLoad: false
        });
        console.error('请求失败', err);
      }
    })
  }
})