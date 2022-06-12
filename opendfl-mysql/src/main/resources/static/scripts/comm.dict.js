dict_status = [
    {id: '0', name: '无效', color: 'red'},
    {id: '1', name: '有效'}
];

dict_status_all = [
    {id: '', name: ''},
    {id: '0', name: '无效', color: 'red'},
    {id: '1', name: '有效'}
];

dict_if = [
    {id: '0', name: '否'},
    {id: '1', name: '是', color: 'red'}
];


function getDictName(dict, id) {
    var name;
    var obj = getDict(dict, id);
    if (obj) {
        name = obj.name;
    }
    return name;
}

function getDict(dict, id) {
    var name;
    var obj;
    for (var i = 0; i < dict.length; i++) {
        obj = dict[i];
        if (obj.id == id) {
            return obj;
        }
    }
    return null;
}

function getDictData(b, d) {
    var a = [], c;
    if (!b) {
        console.log('----getDictData--dict=' + b);
        return a;
    }
    if (d) {
        a.push({id: "-1", name: d});
    }
    for (var e = 0; e < b.length; e++)
        c = b[e], a.push(c);
    return a
}

function formatterCombobox(dict, value) {
    //console.log(value);
    var color = 'black';
    var data = getDict(dict, value);
    if (data) {
        color = data.color;
        if (!color) {
            return data.name;
        }
        return '<font color="' + color + '">' + data.name + '</font>';
    }
    return value;
}

function formatterStatus(value) {
    return formatterCombobox(dict_status, value);
}

function formatterColor(value) {
    if (!value) {
        return '';
    }
    return '<font color="' + value + '">' + value + '</font>';
}

function formatterTypeColor(value) {
    return formatterCombobox(dict_typeColor, value);
}

function formatterReqSysType(value) {
    if (value) {
        return formatterCombobox(dict_reqSysType, value);
    }
    return value;
}

function formatterReqLockType(value) {
    if (value) {
        return formatterCombobox(dict_reqLockType, value);
    }
    return value;
}

function formatterBlackWhiteLimitType(value) {
    if (value) {
        return formatterCombobox(dict_blackWhiteLimitType, value);
    }
    return value;
}


function dgUserNickname(val) {
    if (val) {
        return val.nickname;
    }
    return val;
}

function dgUserUsername(val) {
    if (val) {
        return val.username;
    }
    return val;
}

function dgRoleName(val) {
    if (val) {
        return val.name;
    }
    return val;
}

function dgRoleCode(val) {
    if (val) {
        return val.code;
    }
    return val;
}

function dgUserId(val) {
    if (val) {
        return val.userId;
    }
    return val;
}

dict_typeColor = [
    {"id": "red", "name": "红色", "color": "red"},
    {"id": "black", "name": "黑色", "color": "black"},
    {"id": "green", "name": "绿色", "color": "green"},
    {"id": "blue", "name": "蓝色", "color": "blue"},
    {"id": "yellow", "name": "黄色", "color": "yellow"},
    {"id": "white", "name": "白色", "color": "white"},
    {"id": "pink", "name": "粉红色", "color": "pink"},
    {"id": "purple", "name": "紫色", "color": "purple"}
]

dict_reqSysType = [
    {"id": "i", "name": "IOS"},
    {"id": "a", "name": "安卓"},
    {"id": "h", "name": "H5"},
    {"id": "w", "name": "微信"},
    {"id": "p", "name": "PC"}
]

dict_blackWhiteLimitType = [
    {"id": 1, "name": "用户"},
    {"id": 2, "name": "IP"},
    {"id": 3, "name": "设备号"},
    {"id": 4, "name": "origin"}
]

dict_reqLockType = [
    {"id": "redis", "name": "redis快速失败模式"},
    {"id": "etcdKv", "name": "etcdKv快速失败模式"},
    {"id": "etcdLock", "name": "etcdLock同步模式"},
    {"id": "zk", "name": "zookeeper模式"}
]