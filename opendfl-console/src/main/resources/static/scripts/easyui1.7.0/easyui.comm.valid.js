$.extend($.fn.validatebox.defaults.rules, {
    minLength: {//最小长度
        validator: function(value, param){
            return value.length >= param[0];
        },
        message: '至少输入{0}个字符.'
    },
    maxLength: {//最大长度
        validator: function(value, param){
            return value.length <= param[0];
        },
        message: '最多输入{0}个字符.'
    },
    mobile: {//手机号码
    	validator: function(value, param){
    		//匹配移动手机号
    		var PATTERN_CHINAMOBILE = /^1(3[4-9]|5[012789]|8[23478]|4[7]|7[8])\d{8}$/;
    		//匹配联通手机号
    		var PATTERN_CHINAUNICOM =/^1(3[0-2]|5[56]|8[56]|4[5]|7[6])\d{8}$/;
    		//匹配电信手机号
    		var PATTERN_CHINATELECOM =/^1(3[3])|(8[019])\d{8}$/;
    		return PATTERN_CHINAMOBILE.test(value) 
    		|| PATTERN_CHINAUNICOM.test(value) 
    		|| PATTERN_CHINATELECOM.test(value);
        },
        message: '请输入正确的移动电话号码.'
    },
    post: {//邮编
    	validator: function(value, param){
    		//目前只验证6位数字
    		var PATTERN_POST = /^[0-9]{6}$/;
    		return PATTERN_POST.test(value);
        },
        message: '请输入6位数字的邮编.'
    } 
});