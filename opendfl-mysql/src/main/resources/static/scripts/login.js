var submitflage = false;
function initCookies() {
    var cookie = document.cookie,
        items = cookie.split(";"),
        keys = {};
    items.forEach(function(item) {
        var kv = item.split('=');
        keys[$.trim(kv[0])] = $.trim(kv[1]);
    });
    return keys;
}

function encrypt_login(f) {
    console.log('----encrypt_login--')
    // 获取
    var url= '/frequencyLogin/rsaKey?funcCode=login';
    $.ajax({
        type:"get",
        url:url,
        success:function(data){
            console.log(data);
            // console.log(encrypedPwd);
            f(data.data);
        },
        error: function(result, status, xhr) {
            submitflage = false;
        }
    });
}

function login_req(data) {
    console.log('-----login_req--' + data)
    var user = $("#loginForm").serializeJSON();
    var logindata = {
        grant_type: 'password',
        username: user.username,
        pwd: user.pwd
    };


    if (data) {//走加密方式
        var pwdKey = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
        logindata.username = RSAUtils.encryptedStringOfReversed(pwdKey, logindata.username);
        logindata.pwd = RSAUtils.encryptedStringOfReversed(pwdKey, logindata.pwd);
        logindata.clientIdRsa = data.clientIdRsa;
    }

    sessionStorage.access_token='';
    $.ajax({
        type: 'post',
        url: '/frequencyLogin/login',
        data: logindata,
        success: function (res) {
            console.log(res);
            if (res.success) { // 登录成功
                submitflage = false;
                var tokenExpireTime=res.data.tokenExpireTime;
                var cookieTime = tokenExpireTime * 1000;
                sessionStorage.access_token = res.data.access_token;
                sessionStorage.time = new Date().getTime();

                //appUtil.setCookie("access_token", res.data.access_token);  // 把登录token放在cookie里面
                localStorage.username = user.username;  // 把登录用户名放在cookie里面
                setTimeout(function () {
                    // console.log(logindata,'logindata' )
                    window.location = '/index.html';
                    // _self.$router.push('');
                }, 300);
            } else {
                submitflage = false;
                showToastmessage('error', res.errorMsg);
            }

        },
        error: function (res, msg) {
            submitflage=false;
            var error=res.responseJSON;
            showToastmessage(error.error, error.errorMsg);
        }
    });
}

function loginSubmit() {
    if (submitflage) {
        console.log('-----submitflage---=' + submitflage)
        return;
    }
    submitflage = true;
    var user = $("#loginForm").serializeJSON();

    if(!user.username||user.username.length<4){
        showToastmessage('error', '账号长度不够');
        submitflage=false;
        return;
    }

    if(!user.pwd||user.pwd.length<4){
        showToastmessage('error', '密码长度不够');
        submitflage=false;
        return;
    }

    var isEncrypt=true;//是否加密账号及密码
    if(isEncrypt){
        encrypt_login(login_req);
    }
    else{
        login_req();
    }
}

$(function () {
    $("#pwd").bind("change", function () {
        $("input[name='pwd']").val($(this).val());
    });
    if (localStorage.username) {
        $("input[name='username']").val(localStorage.username);
    }
    $('#loginBtn').click(loginSubmit);
})