$(document).ready(function () {
    window.onbeforeunload = function (e) {
        var e = window.event || e;
        e.returnValue = ("重新加载?");
    }
    $.getJSON("../config/data.json", function (data) {
        for (const key in data) {
            if (Object.hasOwnProperty.call(data, key)) {
                const element = data[key];
                let value = sessionStorage.getItem(key);
                if (value) {
                    sessionStorage.setItem(key, value);
                } else if (typeof element === 'object' && !Array.isArray(element)) {
                    sessionStorage.setItem(key, JSON.stringify(element));
                } else {
                    sessionStorage.setItem(key, element);
                }
            }
        }
        let options = sessionStorage.getItem("selected");
        let selected_preset = sessionStorage.getItem("selected_preset");
        let key = sessionStorage.getItem("key");
        if (!selected_preset) {
            danger_tip("<strong>失败!</strong>[selected_preset]不能为空");
            return;
        }
        if (!options) {
            sessionStorage.setItem("selected", selected_preset);
            options = sessionStorage.getItem("selected");
        }
        for (const iterator of key.split(",")) {
            if (options === iterator || selected_preset === iterator) {
                $("#sel2").append('<option selected>' + iterator + '</option>');
            } else {
                $("#sel2").append('<option>' + iterator + '</option>');
            }
        }
        if (options === "/baseapp/smartAcquiringRestApi/loginVerification") {
            $("#aes_from").css({ "display": "inline-block" });
            $("#comment_zero").val(sessionStorage.getItem(options));
        } else {
            $("#aes_from").css({ "display": "none" });
            $("#comment_zero").val(sessionStorage.getItem(options));
        };
    });
    $("#sel2").change(function () {
        let options = $("#sel2 option:selected");
        if (options.text() === "/baseapp/smartAcquiringRestApi/loginVerification") {
            $("#aes_from").css({ "display": "inline-block" });
            $("#comment_zero").val(sessionStorage.getItem(options.text()));
            sessionStorage.setItem("selected", options.text());
        } else {
            $("#aes_from").css({ "display": "none" });
            $("#comment_zero").val(sessionStorage.getItem(options.text()));
            sessionStorage.setItem("selected", options.text());
        };
    });
    $("#comment_zero").keyup(function () {
        let options = $("#sel2 option:selected");
        let value = $("#comment_zero").val();
        sessionStorage.setItem(options.text(), value);
    });
    setInterval(
        function () {
            let timestamp = Date.parse(new Date()) / 1000;
            $(".rounded-pill.bg-primary.text-white").text(timestamp);
        },
        1000
    );
    $(".rounded-pill.bg-primary.text-white").click(function () {
        try {
            navigator.clipboard.writeText($(".rounded-pill.bg-primary.text-white").text());
            success_tip("<strong>成功!</strong>复制成功");
        } catch (error) {
            danger_tip("<strong>失败!</strong>复制失败");
        }
    });
    $("#myModal .btn.btn-success").click(function () {
        try {
            navigator.clipboard.writeText($("#myModal .modal-body").text());
            success_tip("<strong>成功!</strong>复制成功");
        } catch (error) {
            danger_tip("<strong>失败!</strong>复制失败");
        }
    });
    $("#inquiry .btn.btn-outline-success").click(function () {
        $("#inquiry .btn.btn-outline-success").attr("disabled", "disabled");
        $("#inquiry .btn.btn-outline-success .spinner-border.spinner-border-sm").css({ "display": "inline-block" });
        let json_data = {};
        try {
            json_data = JSON.parse($("#comment_zero").val());
            json_data.signKey = $("#sign_from .form-control").val();
            json_data.aesKey = $("#aes_from .form-control").val();
            if ($("#ip .form-control").val()) {
                json_data.pathUrl = 'http://'.concat($("#ip .form-control").val().concat($("#sel2 option:selected").text()));
            }
        } catch (error) {
            danger_tip("<strong>失败!</strong>JSON数据为空或者格式错误");
            $("#inquiry .btn.btn-outline-success").removeAttr("disabled");
            $("#inquiry .btn.btn-outline-success .spinner-border.spinner-border-sm").css({ "display": "none" });
            setTimeout(
                function () {
                    $(".alert-danger").css({ "display": "none", "top": "0%" });
                },
                3000
            );
        }
        if (JSON.stringify(json_data) !== '{}') {
            $.ajax({
                url: '/inquiry',
                data: JSON.stringify(json_data),
                type: 'post',
                "headers": {
                    "Content-Type": "application/json;charset=UTF-8"
                },
                cache: false,
                dataType: 'json',
                success: function (data) {
                    success_tip("<strong>成功!</strong>");
                    $("#myModal .modal-body").text(JSON.stringify(data));
                    $("#inquiry .btn.btn-outline-success").removeAttr("disabled");
                    $("#inquiry .btn.btn-outline-success .spinner-border.spinner-border-sm").css({ "display": "none" });
                },
                error: function (data) {
                    danger_tip("<strong>失败!</strong>".concat(data.responseText));
                    $("#inquiry .btn.btn-outline-success").removeAttr("disabled");
                    $("#inquiry .btn.btn-outline-success .spinner-border.spinner-border-sm").css({ "display": "none" });
                }
            });
        }
    });
    $("#sign .btn.btn-primary.col").click(function () {
        $("#sign .btn.btn-primary.col").attr("disabled", "disabled");
        $("#sign .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "inline-block" });
        let json_data = {};
        try {
            json_data = JSON.parse($("#comment_one").val());
            json_data.signKey = $("#signKey").val();
        } catch (error) {
            danger_tip("<strong>失败!</strong>JSON数据为空或者格式错误");
            $("#sign .btn.btn-primary.col").removeAttr("disabled");
            $("#sign .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
        }
        if (JSON.stringify(json_data) !== '{}') {
            $.ajax({
                url: '/key',
                data: JSON.stringify(json_data),
                type: 'post',
                "headers": {
                    "Content-Type": "application/json;charset=UTF-8"
                },
                cache: false,
                dataType: 'json',
                success: function (data) {
                    success_tip("<strong>成功!</strong>");
                    $("#sign .card-title").text(data.signKey);
                    $("#sign .btn.btn-primary.col").removeAttr("disabled");
                    $("#sign .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                },
                error: function (data) {
                    danger_tip("<strong>失败!</strong>".concat(data.responseText));
                    $("#sign .btn.btn-primary.col").removeAttr("disabled");
                    $("#sign .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                }
            });
        }
    });
    $("#aesE .btn.btn-primary.col").click(function () {
        $("#aesE .btn.btn-primary.col").attr("disabled", "disabled");
        $("#aesE .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "inline-block" });
        let json_data = {};
        json_data.value = $("#comment_two").val();
        json_data.aesKey = $("#aesE_key").val();
        if (JSON.stringify(json_data) !== '{}') {
            $.ajax({
                url: '/aesE',
                data: JSON.stringify(json_data),
                type: 'post',
                "headers": {
                    "Content-Type": "application/json;charset=UTF-8"
                },
                cache: false,
                dataType: 'json',
                success: function (data) {
                    success_tip("<strong>成功!</strong>");
                    $("#aesE .card-title").text(data.aesE);
                    $("#aesE .btn.btn-primary.col").removeAttr("disabled");
                    $("#aesE .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                },
                error: function (data) {
                    danger_tip("<strong>失败!</strong>".concat(data.responseText));
                    $("#aesE .btn.btn-primary.col").removeAttr("disabled");
                    $("#aesE .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                }
            });
        }
    });
    $("#aesD .btn.btn-primary.col").click(function () {
        $("#aesD .btn.btn-primary.col").attr("disabled", "disabled");
        $("#aesD .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "inline-block" });
        let json_data = {};
        json_data.value = $("#comment_three").val();
        json_data.aesKey = $("#aesD_key").val();
        if (JSON.stringify(json_data) !== '{}') {
            $.ajax({
                url: '/aesD',
                data: JSON.stringify(json_data),
                type: 'post',
                "headers": {
                    "Content-Type": "application/json;charset=UTF-8"
                },
                cache: false,
                dataType: 'json',
                success: function (data) {
                    success_tip("<strong>成功!</strong>");
                    $("#aesD .card-title").text(data.aesD);
                    $("#aesD .btn.btn-primary.col").removeAttr("disabled");
                    $("#aesD .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                },
                error: function (data) {
                    danger_tip("<strong>失败!</strong>".concat(data.responseText));
                    $("#aesD .btn.btn-primary.col").removeAttr("disabled");
                    $("#aesD .btn.btn-primary.col .spinner-border.spinner-border-sm").css({ "display": "none" });
                }
            });
        }
    });
})
function success_tip(info) {
    $(".container").prepend('<div class="alert alert-success" style="z-index: 9999;position: fixed;left: 40%;right: 40%;margin: 0 auto;text-align: center;display: block;animation: myfirst 1s;">' + info + '</div>');
    $(".container").append('<div class="alert alert-success" style="z-index: 9999;position: fixed;left: 40%;right: 40%;margin: 0 auto;text-align: center;display: block;animation: myfirst 1s;">' + info + '</div>');
    $(".alert-success").animate({ top: '50%' });
    setTimeout(
        function () {
            $(".alert-success").animate({ top: '100%' });
        },
        1500
    );
    setTimeout(
        function () {
            $(".alert-success").remove();
        },
        2000
    );
}
function danger_tip(info) {
    $(".container").prepend('<div class="alert alert-danger" style="z-index: 9999;position: fixed;left: 40%;right: 40%;margin: 0 auto;text-align: center;display: block;animation: myfirst 1s;">' + info + '</div>');
    $(".container").append('<div class="alert alert-danger" style="z-index: 9999;position: fixed;left: 40%;right: 40%;margin: 0 auto;text-align: center;display: block;animation: myfirst 1s;">' + info + '</div>');
    $(".alert-danger").animate({ top: '50%' });
    setTimeout(
        function () {
            $(".alert-danger").animate({ top: '100%' });
        },
        1500
    );
    setTimeout(
        function () {
            $(".alert-danger").remove();
        },
        2000
    );
}