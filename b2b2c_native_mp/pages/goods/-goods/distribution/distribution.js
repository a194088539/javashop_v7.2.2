
Component({
  properties: {
    show: {
      type:Boolean,
      observer:function(newVal){
        if(newVal){
          wx.showLoading({title:'生成中...',mask:true})
          setTimeout(() => {
            this.setData({ showDisPopup: true })
            wx.hideLoading()
            this.drawCanvas()
          }, 1500)
        }
      }
    },
    goods_name: String,
    goods_price: String,
    minicode: String,
    thumbnail: String,
    userInfo:Object,
    skuPromotions: Object,
    exchange: Object,
    assemble: Object,
  },
  data: {
    showDisPopup: false
  },
  lifetimes: {
    async ready() {
      wx.getSystemInfo({
        success: (res) => {
          this.setData({
            windowW: res.windowWidth,
            windowH: res.windowHeight,
            rpx: res.windowWidth / 375
          })
        },
      })
    }
  },
  methods: {
    cloneCanvas() { this.setData({ show: false, showDisPopup: false }) },
    // 绘制canvas
    drawCanvas() {
      let that = this
      let rpx = that.data.windowW / 375
      let text = that.data.goods_name
      // 使用 wx.createContext 获取绘图上下文 context
      let context = wx.createCanvasContext('firstCanvas', that)
      // 海报背景
      that.roundRect(context, 55 * rpx, 60 * rpx, 260 * rpx, 440 * rpx, 8)
      // 小程序信息
      context.setFontSize(14)
      context.setFillStyle("#000")
      context.fillText(that.data.userInfo.nickname, 125 * rpx, 90 * rpx)
      context.setFontSize(10)
      context.setFillStyle("#ccc")
      context.fillText('给你推荐了一个好东西', 125 * rpx, 110 * rpx)
      // 商品信息
      that.roundRect(context, 70 * rpx, 130 * rpx, 230 * rpx, 355 * rpx, 8)
      context.strokeStyle = '#f5f5f5'
      context.stroke()
      // 商品图片
      context.drawImage(that.data.thumbnail, 70 * rpx, 130 * rpx, 230 * rpx, 230 * rpx)
      // 商品文字描述
      context.setFontSize(22)
      context.setFillStyle("red")
      if(that.data.skuPromotions) {
        if (that.data.skuPromotions.promotion_type === 'SECKILL') {
          var priceWidth = context.measureText(that.data.skuPromotions.seckill_goods_vo.seckill_price).width + 35;
          context.fillText('￥' + that.data.skuPromotions.seckill_goods_vo.seckill_price, 75 * rpx, 390 * rpx)
        } else if (that.data.skuPromotions.promotion_type === 'GROUPBUY') {
          var priceWidth = context.measureText(that.data.skuPromotions.groupbuy_goods_vo.price).width + 35;
          context.fillText('￥' + that.data.skuPromotions.groupbuy_goods_vo.price, 75 * rpx, 390 * rpx)
        }
      } else if (this.data.exchange) {
        var priceWidth = context.measureText(that.data.exchange.exchange_money).width + 35;
        context.fillText('￥' + that.data.exchange.exchange_money, 75 * rpx, 390 * rpx)
        context.setFontSize(14)
        var pointWidth = context.measureText(that.data.exchange.exchange_point).width + 45;
        context.fillText('+' + that.data.exchange.exchange_point + '积分', (75 * rpx + priceWidth * rpx) - 15, 390 * rpx)
      } else if (this.data.assemble) {
        var priceWidth = context.measureText(that.data.assemble.sales_price).width + 35;
        context.fillText('￥' + that.data.assemble.sales_price, 75 * rpx, 390 * rpx)
      } else {
        context.fillText('￥' + that.data.goods_price, 75 * rpx, 390 * rpx)
      }
      context.setFontSize(12)
      context.strokeStyle = "red";
      if (that.data.skuPromotions) {
        if (that.data.skuPromotions.promotion_type === 'SECKILL') {
          context.fillText('限时抢购', 75 * rpx + priceWidth * rpx, 388 * rpx)
          context.strokeRect(75 * rpx + (priceWidth - 5) * rpx, 373 * rpx, 60, 20 * rpx);
        } else if (that.data.skuPromotions.promotion_type === 'GROUPBUY') {
          context.fillText('团购活动', 75 * rpx + priceWidth * rpx, 388 * rpx)
          context.strokeRect(75 * rpx + (priceWidth - 5) * rpx, 373 * rpx, 60, 20 * rpx);
        }
      } else if (that.data.exchange) {
        context.fillText('积分兑换', 85 * rpx + priceWidth * rpx + pointWidth * rpx, 388 * rpx)
        context.strokeRect(85 * rpx + (priceWidth - 5) * rpx + pointWidth * rpx, 373 * rpx, 60, 20 * rpx);
      } else if (that.data.assemble) {
        let _assemble = that.data.assemble.required_num + '人拼团';
        let textWidth = context.measureText(_assemble).width + 10;
        context.strokeRect(75 * rpx + (priceWidth - 5) * rpx, 373 * rpx, textWidth, 20 * rpx);
        context.fillText(_assemble, 75 * rpx + priceWidth * rpx, 388 * rpx)
      }
      context.setFillStyle("#999")
      if (that.data.skuPromotions) {
        if (that.data.skuPromotions.promotion_type === 'SECKILL') {
          context.fillText('原价：￥' + that.data.skuPromotions.seckill_goods_vo.original_price, 80 * rpx, 410 * rpx)
        } else if (that.data.skuPromotions.promotion_type === 'GROUPBUY') {
          context.fillText('原价：￥' + that.data.skuPromotions.groupbuy_goods_vo.original_price, 80 * rpx, 410 * rpx)
        }
      } else if (that.data.exchange) {
        context.fillText('原价：￥' + that.data.exchange.goods_price, 80 * rpx, 410 * rpx)
      } else if (that.data.assemble) {
        context.fillText('原价：￥' + that.data.assemble.origin_price, 80 * rpx, 410 * rpx)
      }
      // 商品名字，名字很长调用方法将文字折行，传参 文字内容text，画布context
      let row = that.newLine(text, context)
      let a = 20//定义行高22
      for (let i = 0; i < row.length; i++) {
        context.setFontSize(12)
        context.setFillStyle("#000000")
        context.fillText(row[i], 80 * rpx, 435 * rpx + a * i, 150 * rpx)
      }
      // 识别小程序二维码
      context.drawImage(that.data.minicode, 225 * rpx, 415 * rpx, 65 * rpx, 65 * rpx)
      //头像
      context.beginPath()
      context.arc(95 * rpx, 95 * rpx, 25, 0, 2 * Math.PI);
      context.clip();
      if (that.data.userInfo.face) {
        context.drawImage(that.data.userInfo.face, 70 * rpx, 70 * rpx, 50 * rpx, 50 * rpx)
      } else {
        context.drawImage("../../../../static/images/icon-noface.jpg", 70 * rpx, 70 * rpx, 50 * rpx, 50 * rpx)
      }
      context.draw()
    },
    // 圆角
    roundRect(ctx, x, y, w, h, r, c = '#ffffff') {
      if (w < 2 * r) { r = w / 2; }
      if (h < 2 * r) { r = h / 2; }

      ctx.beginPath();
      ctx.fillStyle = c;
      // 左上角
      ctx.arc(x + r, y + r, r, Math.PI, Math.PI * 1.5);
      ctx.moveTo(x + r, y);
      ctx.lineTo(x + w - r, y);
      ctx.lineTo(x + w, y + r);
      //右上角
      ctx.arc(x + w - r, y + r, r, Math.PI * 1.5, Math.PI * 2);
      ctx.lineTo(x + w, y + h - r);
      ctx.lineTo(x + w - r, y + h);
      ///右下角
      ctx.arc(x + w - r, y + h - r, r, 0, Math.PI * 0.5);
      ctx.lineTo(x + r, y + h);
      ctx.lineTo(x, y + h - r);
      //左下角
      ctx.arc(x + r, y + h - r, r, Math.PI * 0.5, Math.PI);
      ctx.lineTo(x, y + r);
      ctx.lineTo(x + r, y);

      ctx.fill();
      ctx.closePath();
    },
    // 点击保存按钮，同时将画布转化为图片
    savePictures() {
      let that = this;
      wx.canvasToTempFilePath({
        x: 55 * that.data.rpx,
        y: 60 * that.data.rpx,
        width: 260 * that.data.rpx,
        height: 450 * that.data.rpx,
        canvasId: 'firstCanvas',
        fileType: 'jpg',
        quality: 1,
        success(res) {
          that.setData({ shareImage: res.tempFilePath })
          that.eventSave()
        }
      }, this)
    },
    // 将商品分享图片保存到本地
    eventSave() {
      wx.saveImageToPhotosAlbum({
        filePath: this.data.shareImage,
        success(res) {
          wx.showToast({
            title: '保存图片成功',
            icon: 'success',
            duration: 2000
          })
        },
        fail(err) {
          if (err.errMsg === 'saveImageToPhotosAlbum:fail auth deny' || err.errMsg === 'saveImageToPhotosAlbum:fail auth denied' || err.errMsg === 'saveImageToPhotosAlbum:fail authorize no response') {
            wx.showModal({
              title: '提示',
              content: '请确认授权，否则图片将无法保存到相册',
              confirmColor:'#f42424',
              success(res){
                if(res.confirm){
                  wx.openSetting({
                    success(res) {
                      if (res.authSetting['scope.writePhotosAlbum']) {
                        wx.showToast({ title: '获取权限成功，再次点击即可保存', icon: "none" })
                      } else {
                        wx.showToast({ title: '获取权限失败，将无法保存到相册哦~', icon: "none" })
                      }
                    }
                  })
                }
              }
            })
          }
        }
      })
    },
    // canvas多文字换行
    newLine(txt, context) {
      let txtArr = txt.split('')
      let temp = ''
      let row = []
      for (let i = 0; i < txtArr.length; i++) {
        if (context.measureText(temp).width < 120 * this.data.rpx) {
          temp += txtArr[i]
        } else {
          i--
          row.push(temp)
          temp = ''
        }
      }
      row.push(temp)
      //如果数组长度大于3 则截取前三个
      if (row.length > 3) {
        let rowCut = row.slice(0, 3);
        let rowPart = rowCut[2];
        let test = "";
        let empty = [];
        for (let a = 0; a < rowPart.length; a++) {
          if (context.measureText(test).width < 100 * this.data.rpx) {
            test += rowPart[a];
          } else {
            break;
          }
        }
        empty.push(test);
        let group = empty[0] + "..." //这里只显示三行，超出的用...表示
        rowCut.splice(2, 1, group);
        row = rowCut;
      }
      return row
    }
  }
})