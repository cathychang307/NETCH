 /* 專案JS設定檔    */
$.extend(Properties || {}, {
    window: {
        closeConfirm: false,
        closeWindowMsg: '重新載入後資料將會消失!!\nReload the page data will be lost!!',
    onunload : function() {
    }
    },
    contextName: "/etch/",
    ajaxTimeOut: 60 * 1000 * 3, // timeOut: 1000
    // 下拉選單handler
    ComboBoxHandler: 'codetypehandler/queryByKeys',
    Grid: {
        rowNum: 15,
        rowList: []
    },
    custLoadPageInit: function(isSubPage) {
        //    //for captcha start
        //    this.find(".captcha").each(function() {
        //      var dom = $(this);
        //      var img = $("<img />", {
        //        src : url("captcha.png?cc=" + parseInt(Math.random() * 1000)),
        //        css : {
        //          height : 24,
        //          weight : 60
        //        }
        //      });
        //      dom.bind("refresh", function() {
        //        dom.val("");
        //        img.attr("src", url("captcha.png?cc=" + parseInt(Math.random() * 1000)));
        //      });
        //      var refresh = $("<img />", {
        //        src : url("static/images/refresh.png"),
        //        css : {
        //          height : 24,
        //          cursor : 'pointer'
        //        },
        //        click : function() {
        //          dom.trigger("refresh");
        //        }
        //      });
        //      dom.after(refresh).after(img);
        //    });
        //    //for captcha end
        //for mask start
        var masks = [];
        if (Properties.itemMaskRule) {
            for (var key in Properties.itemMaskRule) {
                masks.push(key);
            }
        }
        var maskItem = this.find(masks.join(","));
        $.each(masks, function(i, v) {
            maskItem.filter(v).each(function() {
                var $this = $(this);
                if ($.isFunction(Properties.itemMaskRule[v])) {
                    $this.data("maskRule", Properties.itemMaskRule[v]).bind("focus.mask", function() {
                        $this.__val($this.data("realValue") || $this.val() || "");
                        $this.data(Properties.maskStr, '');
                    }).bind("keypress.mask", function() {
                        if ($this.data(Properties.maskStr)) {
                            return;
                        }
                        $this.data("realValue", $this.__val());
                    }).bind("blur.mask", function() {
                        if ($this.data(Properties.maskStr)) {
                            return;
                        }
                        $this.data("realValue", $this.__val());
                        $this.trigger("mask");
                    }).bind("change.mask", function() {
                        if ($this.data(Properties.maskStr)) {
                            return;
                        }
                        $this.data("realValue", $this.__val());
                        $this.trigger("mask");
                    }).bind("mask", function() {
                        if ($this.is("span")) {
                            var r = Properties.itemMaskRule[v].call($this);
                            if ($this.data(Properties.maskStr)) {
                                $this.__val(r);
                            }
                            $this.text(r);
                        } else
                            $this.__val(Properties.itemMaskRule[v].call($this));
                    });
                }
            });
        });
        // for mask end
    },
    maskStr: '*',
    timeOut: 'TIME_OUT',
    remindTimeout: false
});
