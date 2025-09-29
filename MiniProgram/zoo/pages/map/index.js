//获取应用实例
var app = getApp();
Page({
  data: {
    fullscreen: false,
    latitude: 23.03736,
    longitude: 113.39543,
    buildlData: app.globalData.map,
    windowHeight: "",
    windowWidth: "",
    isSelectedBuild: 0,
    isSelectedBuildType: 0,
    imgCDN: app.imgCDN,
    islocation: true,
    // 新增：保存地图视野状态
    mapRegion: {
      latitude: 23.03736,
      longitude: 113.39543,
      scale: 16
    }
  },
  onLoad: function () {
    wx.showShareMenu({
      withShareTicket: true
    })
    var _this = this;
    wx.getSystemInfo({
      success: function (res) {
        //获取当前设备宽度与高度，用于定位控键的位置
        _this.setData({
          windowHeight: res.windowHeight,
          windowWidth: res.windowWidth,
        })
      }
    })
    //载入更新后的数据
    this.setData({
      buildlData: app.globalData.map
    })

    // 初始化地图视野状态
    this.setData({
      'mapRegion.scale': this.data.buildlData[this.data.isSelectedBuildType].scale || 16,
      'mapRegion.latitude': this.data.latitude,
      'mapRegion.longitude': this.data.longitude
    })
  },
  onShareAppMessage: function (res) {
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: app.globalData.introduce.name + ' - 动物园导览',
      path: '/pages/map/index',
      success: function (res) {},
      fail: function (res) {}
    }
  },
  regionchange(e) {
    // 视野变化时保存当前视野状态
    if (e.type === 'end') {
      this.setData({
        'mapRegion.latitude': e.detail.centerLocation.latitude || this.data.mapRegion.latitude,
        'mapRegion.longitude': e.detail.centerLocation.longitude || this.data.mapRegion.longitude,
        'mapRegion.scale': e.detail.scale || this.data.mapRegion.scale
      })
    }
  },
  markertap(e) {
    // 选中 其对应的框
    this.setData({
      isSelectedBuild: e.markerId
    })
  },
  navigateSearch() {
    wx.navigateTo({
      url: 'search'
    })
  },
  location: function () {
    var _this = this
    wx.getLocation({
      type: 'gcj02', // 默认为 wgs84 返回 gps 坐标，gcj02 返回可用于 wx.openLocation 的坐标  
      success: function (res) {
        app.globalData.latitude = res.latitude;
        app.globalData.longitude = res.longitude;
        _this.setData({
          longitude: res.longitude,
          latitude: res.latitude,
          // 更新地图视野状态
          'mapRegion.latitude': res.latitude,
          'mapRegion.longitude': res.longitude,
          'mapRegion.scale': 16 // 定位时重置缩放级别
        })
      }
    })
  },
  clickButton: function (e) {
    this.setData({
      fullscreen: !this.data.fullscreen
    })
  },
  changePage: function (event) {
    const newType = event.currentTarget.id;

    // 切换类型时保持当前地图视野不变
    this.setData({
      isSelectedBuildType: newType,
      isSelectedBuild: 0,
      // 使用保存的地图视野参数，而不是使用buildlData中的默认scale
      latitude: this.data.mapRegion.latitude,
      longitude: this.data.mapRegion.longitude,
      scale: this.data.mapRegion.scale
    });
  }
})