// map.js
var app = getApp();
var amapFile = require('../../utils/amap-wx.130.js');

Page({
  data: {
    latitude: null,
    longitude: null,
    markers: [],
    distance: 0,
    duration: 0,
    formattedDistance: '计算中...',
    polyline: []
  },

  onLoad: function (options) {
    this.mapContext = wx.createMapContext('navi_map')
    this.target = options
    this.initLocation()
  },

  initLocation: function() {
    const _this = this
    wx.getLocation({
      type: 'gcj02',
      success: function (res) {
        app.globalData.latitude = res.latitude
        app.globalData.longitude = res.longitude
        _this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        }, () => _this.routing())
      },
      fail: function () {
        wx.showModal({
          title: '定位权限已关闭',
          content: '请到系统设置中打开定位权限',
          success: () => wx.navigateBack()
        })
      }
    })
  },

  routing: function () {
    const _this = this
    const myAmapFun = new amapFile.AMapWX({ key: require('../../config.js').key })
    
    const routeData = {
      origin: `${this.data.longitude},${this.data.latitude}`,
      destination: `${this.target.longitude},${this.target.latitude}`,
      success: function (data) {
        if (!data.paths || !data.paths[0]) return
        
        const path = data.paths[0]
        const steps = path.steps || []
        const points = steps.reduce((acc, step) => {
          return acc.concat(step.polyline.split(';').map(p => {
            const [longitude, latitude] = p.split(',')
            return { longitude: parseFloat(longitude), latitude: parseFloat(latitude) }
          }))
        }, [])

        _this.updateMap(path, points)
      }
    }

    // 根据距离选择导航方式
    const distance = this.calculateDirectDistance()
    distance < 0.85 ? myAmapFun.getWalkingRoute(routeData) : myAmapFun.getDrivingRoute(routeData)
  },

  calculateDirectDistance: function() {
    return Math.abs(this.data.longitude - this.target.longitude) + 
           Math.abs(this.data.latitude - this.target.latitude)
  },

  updateMap: function(path, points) {
    const distance = path.distance || 0
    const duration = Math.ceil((path.duration || 0) / 60)  // 转换为分钟
    
    this.setData({
      distance,
      duration,
      formattedDistance: distance > 1000 ? 
        `${(distance/1000).toFixed(1)}公里` : `${distance}米`,
      markers: this.createMarkers(),
      polyline: [{
        points,
        color: "#1890FF",
        width: 6,
        arrowLine: true
      }]
    })
  },

  createMarkers: function() {
    return [{
      id: 1,
      width: 25,
      height: 35,
      iconPath: "/images/map/mapicon_start.png",
      latitude: this.data.latitude,
      longitude: this.data.longitude
    }, {
      id: 2,
      width: 25,
      height: 35,
      iconPath: "/images/map/mapicon_end.png",
      latitude: this.target.latitude,
      longitude: this.target.longitude
    }]
  },

  centerLocation: function() {
    this.mapContext.moveToLocation()
  }
})