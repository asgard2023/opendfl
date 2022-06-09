var isSupportLocalStorage = false;

if (window.localStorage) {
    isSupportLocalStorage = true;
} else {
    console.warn('This browser does NOT support localStorage');
}

var isSupportSessionStorage = false;
if (window.sessionStorage) {
    isSupportSessionStorage = true;
} else {
    console.warn('This browser does NOT support sessionStorage');
}

var app = {
    isSupportLocalStorage: isSupportLocalStorage,
    isSupportSessionStorage: isSupportSessionStorage,
    clientId: '',
    source: 1,
    authorToken: function () {
        var token = sessionStorage.access_token;
        if (!token) {
            showToastmessage('error', '请先登入');
            return;
        }
        return token;
    },
    getQueryString : function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    }
}

var appUtil =
    {
// 设置cookie
    setCookie: function (name, value, expire, path) {
        var curdate = new Date();
        var cookie = name + '=' + encodeURIComponent(value) + '; ';
        if (expire != undefined || expire == 0) {
            if (expire == -1) {
                expire = 366 * 86400 * 1000;// 保存一年
            } else {
                expire = parseInt(expire);
            }
            curdate.setTime(curdate.getTime() + expire);
            cookie += 'expires=' + curdate.toUTCString() + '; ';
        }
        path = path || '/';
        cookie += 'path=' + path;
        document.cookie = cookie;
    },

    // 获取cookie
    getCookie: function (name) {
        var re = '(?:; )?' + encodeURIComponent(name) + '=([^;]*);?';
        re = new RegExp(re);
        if (re.test(document.cookie)) {
            return decodeURIComponent(RegExp.$1);
        }
        return '';
    },
    deleteCookie: function (name) {
        this.setCookie(name, '');
    },
    //清除所有cookie函数
    clearAllCookie: function () {
        console.log('清除cookie');
        this.setCookie("access_token", '');  // 登录token
        this.setCookie("username", '');  //  登录用户名
        var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
        if (keys) {
            for (var i = keys.length; i--;)
                document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()
        }
    },
    getName: function () {
        return this.name;
    }
}

$(function () {
    $.ajaxSetup({
        headers: {
            'authorization': app.authorToken(),
            'lang':'CN'
        }
    });
});



function showToastmessage(title, msg){
    $.messager.show({
        title: title,
        msg: msg,
        timeout: 3000,
        showType: 'slide'
    });
}


/**
 * easyui支持serializeJson
 */
if(typeof jQuery != 'undefined'){
    (function(){
        if (!window.WebUtil)
            WebUtil = {};
    })();
    WebUtil.form = {
        serializeJson : function(frm){
            var json = {};
            frm = frm || $('body');
            if (!frm) {
                return json;
            }
            var inputs = frm.find('input[type!=button][type!=reset][type!=submit][type!=image],select,textarea');
            if (!inputs) {
                return json;
            }
            for (var index = 0; index < inputs.length; index++) {
                var input = $(inputs[index]);
                var name = input.attr('name');
                var value = input.val();
                if (name != null && $.trim(name) != '' && value != null && $.trim(value) != '') {
                    json[name] = value;
                }
            }
            return json;
        }
    };
    (function($){
        $.fn.serializeJson = function(){
            return WebUtil.form.serializeJson($(this));
        };
    }(jQuery));
}