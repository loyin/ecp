angular.module('filterModule', [])
.filter('yn', function() {
  return function(input) {
  	var v=["否","是"];
    return v[input];
  }
})
.filter('bonus_type', function() {
	return function(input) {
		var v=["市场分红","领导奖","报单奖","推荐奖金","报单奖"];
		return v[input];
	}
})
.filter('money', function() {
	return function(val, dec) {
		val = parseFloat(val);	
		dec = dec || 2;	//小数位
		if(isNaN(val)){
			return '';
		}
		val = val.toFixed(dec).split('.');
		var reg = /(\d{1,3})(?=(\d{3})+(?:$|\D))/g;
		return "￥"+val[0].replace(reg, "$1,") + '.' + val[1];
	}
})
;