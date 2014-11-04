// 部分来自网络，不保证全部有用
// 购买和收获地址为OK
function wxBuy(appId, timeStamp, nonceStr, packages, paySign, cb) {
    if (typeof WeixinJSBridge == "undefined") {
        alert("请先通过微信访问！");
    } else {
        WeixinJSBridge.invoke('getBrandWCPayRequest',{
            "appId" : appId, //公众号名称，由商户传入
            "timeStamp" : timeStamp, //时间戳 这里随意使用了一个值
            "nonceStr" : nonceStr, //随机串
            "package" : packages, //扩展字段，由商户传入
            "signType" : "SHA1", //微信签名方式:sha1
            "paySign" : paySign //微信签名
        }, function(res){
            WeixinJSBridge.log(res.err_msg);
            cb(res);
            // 返回 res.err_msg,取值
            // get_brand_wcpay_request:cancel 用户取消
            // get_brand_wcpay_request:fail 发送失败
            // get_brand_wcpay_request:ok 发送成功
            //alert(res.err_code + res.err_desc);
        });
    }
};
// 微信收获地址
function wxGetAddress (appId, cb){
    if (typeof WeixinJSBridge == "undefined") {
        alert("请先通过微信访问！");
    } else {
        WeixinJSBridge.invoke('getRecentlyUsedAddress',{
            //公众号名称，由商户传入
            "appId" : appId
        },function(res){
            cb(res);
            //  返回 res.err_msg,取值
            // get_recently_used_address:fail  获取失败
            // get_recently_used_address:ok  获取成功
            // WeixinJSBridge.log(res.err_msg);
            // 收获地址格式为下列数据共同组成，其中参数列表如下：
            // userName:收货人姓名
            // telNumber:收货人电话号码
            // addressPostalCode:邮政编码
            // proviceFirstStageName:收货地址第⼀一级省、直辖市、自治区、特别行政区名称
            // addressCitySecondStageName:收货地址第二级市名称
            // addressCountiesThirdStageName:收货地址第三级区县名称
            // addressDetailInfo:收货地址详细信息
            // alert(res.userName+res.telNumber+res.addressPostalCode+res.proviceFirstStageName+res.addressCitySecondStageName+res.addressCountiesThirdStageName+res.addressDetailInfo);
        });
    }
};
// 修改微信地址
function wxEditAddress(appId, cb) {
    if (typeof WeixinJSBridge == "undefined") {
        alert("请先通过微信访问！");
    } else {
        WeixinJSBridge.invoke('editTransactionAddress',{
            //公众号名称，由商户传入
            "appId" : appId
        },function(res){
            cb(res);
            // 返回 res.err_msg,取值
            // edit_address:fail  编辑被取消
            // edit_address:ok  编辑成功
            // WeixinJSBridge.log(res.err_msg);
            // 收获地址格式为下列数据共同组成，其中参数列表如下：
            // userName:收货人姓名
            // telNumber:收货人电话号码
            // addressPostalCode:邮政编码
            // proviceFirstStageName:收货地址第⼀一级省、直辖市、自治区、特别行政区名称
            // addressCitySecondStageName:收货地址第二级市名称
            // addressCountiesThirdStageName:收货地址第三级区县名称
            // addressDetailInfo:收货地址详细信息
            // alert(res.userName+res.telNumber+res.addressPostalCode+res.proviceFirstStageName+res.addressCitySecondStageName+res.addressCountiesThirdStageName+res.addressDetailInfo);
        });
    }
}
