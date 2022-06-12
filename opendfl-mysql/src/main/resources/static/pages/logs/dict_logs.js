var dict_freqLimitType = [
    {id: 'limit', name: 'limit用户限制'},
    {id: 'limitIp', name: 'limitIp同IP次数频率'},
    {id: 'userIp', name: 'userIp用户IP数限制'},
    {id: 'ipUser', name: 'ipUserIP用户数限制'}
]

var dict_whiteBlackCheckType = [
    {id: 'user', name: "user"},
    {id: 'ip', name: "ip"},
    {id: 'device', name: "device"},
    {id: 'origin', name: "origin"}
]

var dict_limitType = [
    {id: 'limit', name: 'frequency-limit'},
    {id: 'limitIp', name: 'frequency-limitIp'},
    {id: 'userIp', name: 'frequency-userIp'},
    {id: 'ipUser', name: 'frequency-ipUser'},
    {id: 'user', name: "white/black-user"},
    {id: 'ip', name: "white/black-ip"},
    {id: 'device', name: "white/black-device"},
    {id: 'origin', name: "white/black-origin"}
]

var dict_outLimitType = [
    {id: "1", name: "frequency"},
    {id: "2", name: "white"},
    {id: "3", name: "black"}
]

function formatterOutLimitType(value) {
    if (value) {
        return formatterCombobox(dict_outLimitType, value);
    }
    return value;
}