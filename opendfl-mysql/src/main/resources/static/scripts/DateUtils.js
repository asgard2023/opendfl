/**
 * 日期处理通用方法
 * @returns {DateUtils}
 */
function DateUtils() {

    this.parseDate = function (dateStr) {
        return DateUtils.parseDate(dateStr);
    }

    this.getDateStr = function (dateStr) {
        return DateUtils.getDateStr(dateStr);
    }

    this.valueOfTime = function (value) {
        var hour = parseInt(value / 60);
        var minute = value % 60;
        var rValue = "";
        if (hour < 10) {
            rValue = "0" + hour;
        } else {
            rValue = hour;
        }

        if (minute < 10) {
            rValue = rValue + "0" + minute;
        } else {
            rValue = rValue + "" + minute;
        }
        return rValue;
    }


    this.formatDate = function (oDate, exp) {
        return DateUtils.formatDate(oDate, exp);
    }

    this.parseDayAndTime = function (dayStr, timeStr) {
        if (typeof dayStr == 'string' && typeof timeStr == 'string') {
            var dateStr = dayStr + " " + timeStr;

            var results = dateStr.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{2})(\d{2}) *$/);

            if (results && results.length > 5)
                return new Date(parseInt(results[1]), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10));

        }
    }

    this.distantHour = function (date1, date2) {
        return Math.abs((date2.getTime() - date1.getTime())) / 3600000;
    }

    this.distantDay = function (date1, date2) {
        return Math.abs((date2.getTime() - date1.getTime())) / 3600000 / 24;
    }

    this.distantMonth = function (date1, date2) {
        return Math.abs((date2.getTime() - date1.getTime())) / 3600000 / 24 / 365 * 12;
    }

    this.distantYear = function (date1, date2) {
        var year1 = date1.getFullYear();
        var year2 = date2.getFullYear();
        return Math.abs(year2 - year1);
    }

    this.dateBetween = function (date, date1, date2) {
        var isBetween;
        if (date.getTime() >= date1.getTime() && date.getTime() < date2.getTime()) {
            isBetween = true;
        } else {
            isBetween = false;
        }
        //log.info('-----dateBetween-'+date.format('yyyy-MM-dd')+'-------'+date1.format('yyyy-MM-dd')+'------'+date2.format('yyyy-MM-dd')+'--isBetween='+isBetween);
        return isBetween;
    }
    this.addDate = function (date, days) {
        if (days == undefined || days == '') {
            days = 1;
        }
        date.setDate(date.getDate() + days);
        return date;
    }


}

//静态属性
DateUtils.FORMAT_DATE = "yyyy-MM-dd";
DateUtils.DAY_ONE = 24 * 60 * 60 * 1000;
DateUtils.DATE_LENGTH = DateUtils.FORMAT_DATE.length;

//静态方法
DateUtils.parseDate = function (dateStr) {
    if (typeof dateStr == 'string') {
        var results = dateStr.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
        if (!results) {
            results = dateStr.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}) *$/);
        }
        if (results && results.length > 5)
            return new Date(parseInt(results[1]), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10));
    }
}

DateUtils.getDateStr = function (dateStr) {
    if (dateStr != undefined && dateStr != null && dateStr.length > DateUtils.DATE_LENGTH) {
        return dateStr.substring(0, DateUtils.DATE_LENGTH);
    }
    return dateStr;
}

DateUtils.formatDate = function (oDate, exp) {
    if (oDate instanceof Date) {
        if (exp != undefined) {
            return oDate.format(exp);
        }
        return oDate.format('yyyy-MM-dd');
    }
    return DateUtils.getDateStr(oDate);
}

/**
 * 获取时区数
 */
DateUtils.offset = (Date.UTC(2000, 4, 23, 0, 0, 0, 0) - Date.parse(new Date(2000, 4, 23, 0, 0, 0).toString())) / 3600000;

DateUtils.utcDateStr = function (d, offset) {
    var utc = d.getTime() + (d.getTimezoneOffset() * 60000);
    // using supplied offset
    if (offset == undefined) {
        offset = DateUtils.offset;
    }
    var nd = new Date(utc + (3600000 * offset));
    // return time as a string
    return nd.toLocaleString();
}
DateUtils.utcDate = function (d, offset) {
    var utc = d.getTime() + (d.getTimezoneOffset() * 60000);
    // using supplied offset
    if (offset == undefined) {
        offset = DateUtils.offset;
    }
    var nd = new Date(utc + (3600000 * offset));
    // return time as a string
    return nd;
}

DateUtils.hourBefore = function (d, hour) {
    if (hour == undefined || hour == 0) {
        return d;
    }
    var time = d.getTime() - (hour * 3600000);

    var nd = new Date(time);
    //nd.setTime(time);
    // return time as a string
    return nd;
}

/**
 * 计算年龄
 * @param {} birthday
 * @param {} curDay
 * @return {}
 */
DateUtils.getAge = function (birthday, curDay) {
    if (birthday && curDay) {
        if (!(birthday instanceof Date)) {
            birthday = this.parseDate(birthday);
        }
        return Math.ceil((curDay - birthday) / (365 * 24 * 60 * 60 * 1000));
    } else {
        return 0;
    }
}

/**
 * 分钟转成小时:分钟
 * 或分钟部份的小时计算
 * 支持String 125->02:05   01:75-->02:15
 * @param value
 * @param isDayLimit 0/false不限; 1/true限24小时内; 2超出24小时清空
 * @returns
 */
DateUtils.timeHour = function (value, isDayLimit) {
    var h, m, r;
    var s = value;
    s = Number(s);
    h = parseInt(s / 60);
    m = s % 60;

    if (isDayLimit) {
        h = h % 24;
    }
    //h=h%12; 12小时制
    //h="0"+h;
    //h=h.substr(h.length-2,h.length);
    if (h < 10) {
        r = "0" + h;
    } else {
        r = h;
    }
    r = r + ":";
    if (m < 10) {
        r = r + "0" + m;
    } else {
        r = r + m;
    }
    if (isDayLimit == 2) {
        return "";
    }
    return r;
}

/**
 * 秒转成分钟:秒
 * 或分钟部份的小时计算
 * 支持String 125->02:05   01:75-->02:15
 * @param value 秒
 * @returns
 */
DateUtils.timeMinute = function (value) {
    var h, m, r;
    var s = value;
    s = Number(s);
    h = parseInt(s / 60);
    m = s % 60;

    //h=h%12; 12小时制
    //h="0"+h;
    //h=h.substr(h.length-2,h.length);
    if (h < 10) {
        r = "0" + h;
    } else {
        r = h;
    }
    r = r + ":";
    if (m < 10) {
        r = r + "0" + m;
    } else {
        r = r + m;
    }
    return r;
}


function timestampFormatDateTime(value, row, index) {
    if(!value){
        return ;
    }
    var date = new Date(value);
    return DateUtils.formatDate(date, 'yyyy-MM-dd hh:mm:ss');
}

//判断是否闰年
Date.prototype.isLeapYear = function () {
    var year = this.getYear();
    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) return true;
    else return false;
}
//计算月份的天数
Date.prototype.calDays = function () {
    var year = this.getYear();
    var month = this.getMonth() + 1;
    var date = new Date(year, month, 0);
    return date.getDate();
}

/**
 * 转换成星期
 * @param {} day
 * @return {String}
 */
Date.prototype.WeekTransform = function (day) {
    switch (day) {
        case 0:
            return "日";
        case 1:
            return "一";
        case 2:
            return "二";
        case 3:
            return "三";
        case 4:
            return "四";
        case 5:
            return "五";
        case 6:
            return "六";
    }
}

/**
 * 日期时间格式化，支持yyyy-MM-dd hh:mm:ss ww(周) utc(utc时间)
 * @param {} format
 * @return {}
 */
Date.prototype.format = function (format) {
    var isUtc = false;
    var time = this.getTime();
    if (format && format.indexOf(' utc') > 0) {
        isUtc = true;
        var d = new Date();
        d.setTime(this.getTime());
        d = DateUtils.utcDate(d, 0);
        this.setTime(d.getTime());
        format = format.replace(' utc', '');
    }
    var o = {
        "y+": this.getFullYear() + 1, //year
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //date
        "w+": this.WeekTransform(this.getDay()),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1,
            (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    if (isUtc) {//恢复时间
        this.setTime(time);
    }
    return format;
}
Date.prototype.next = function (range) {
    if (range == 0)
        return this;
    if (range == null)
        range = 1;
    return new Date(this.getTime() + range * DateUtils.DAY_ONE);
}


