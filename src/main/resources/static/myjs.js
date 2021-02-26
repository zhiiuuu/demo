$(function () {
    onOpen();
});


let roomName = "";
let ws;
let currentUser;
let uid;

function sendMsg() {

    // $("#text_area img:not(.emoji)").each((index, item) => {
    //     ws.send("$img" + $(item).prop("outerHTML")); // 单独发送图片消息
    // });
    // $("#text_area img:not(.emoji)").remove(); // 删除图片元素

    if ($("#text_area").html().trim() === "") {
        return; // 不发空消息
    }
    var html = $("#text_area").html();
    if (html.length > 2048) {
        alert("内容太长了");
        return;
    }
    ws.send($("#text_area").html());// 发送其它消息
    $("#text_area").empty(); // 清空消息
    // 拆分消息
}

function onOpen() {
    let str = prompt("输入用户名", "房间A,用户1");
    roomName = str.split(",")[0];
    currentUser = str.split(",")[1];

    $("#user_room").text(roomName);

    uid = uuid();
    console.log(uid)

    ws = new WebSocket("ws://" + window.location.host + "/chat/" + roomName + "/" + currentUser + "/" + uid)
    ws.onmessage = onmessage;
    // 发送消息
    $("#send_button").click(function () {
        sendMsg();
    })
}

var user_item_template = '<div class="item">\n' +
    '                    <img class="ui avatar image" src="./images/{0}">\n' +
    '                    <div class="content" style="margin-top: 5px">\n' +
    '                        <div class="user name">{1}</div>\n' +
    '                    </div>\n' +
    '                </div>';

var msg_item_template =
    '<div class="item">\n' +
    '                    <img class="ui avatar image" src="./images/{0}">\n' +
    '                    <div class="content">\n' +
    '                        <div class="user name">{1}</div>\n' +
    '                        <label class="ui left pointing label ">{2}</label>\n' +
    '                    </div>\n' +
    '                </div>';
var msg_item_current_tempate = '<div class="item current">\n' +
    '                        <img class="ui avatar image" src="./images/{1}">\n' +
    '                    <div class="right floated content">\n' +
    '                        <label class="ui right pointing label green ">\n' +
    '                            {0}' +
    '                        </label>\n' +
    '                    </div>\n' +
    '                </div>';

function onmessage(event) {
    let data = JSON.parse(event.data);
    if (data.type == "update_user") {
        $("#user_list").empty();
        data.users.forEach((user) => {
            $(format(user_item_template, [user.header, user.name, user.uid])).appendTo("#user_list");
        });
        $("#user_count").text(data.users.length);
    } else if (data.type == "normal_msg") {
        if (data.user.uid == uid) {
            var $item = $(format(msg_item_current_tempate, [data.msg, data.user.header]));
            $item.appendTo("#msg_list");
        } else {
            var $item = $(format(msg_item_template, [data.user.header, data.user.name, data.msg]));
            $item.appendTo("#msg_list");
        }
        refreshMessage();
    }
}

// 消息滚动条显示至底部
function refreshMessage() {
    $("#msg_list")[0].scrollTop = $("#msg_list")[0].scrollHeight;
}

function format(format, args) {
    return format.replace(/\{(\d+)\}/g, function (m, n) {
        return args[n] ? args[n] : m;
    });
}

let uuid = function uuid() {
    let s = [];
    let hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    return s.join("");
}
