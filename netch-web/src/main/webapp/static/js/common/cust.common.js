// init
var menu = {
  "child" : [ {

    "name" : "\u7cfb\u7d71\u8a2d\u5b9a",
    "url" : "system",
    "child" : [ {
      "name" : "\u4ee3\u78bc\u8a2d\u5b9a",
      "url" : "system/codetype"
    }, {
      "name" : "\u53c3\u6578\u8a2d\u5b9a",
      "url" : "system/sysparm"
    }, {
      "name" : "\u6d41\u6c34\u865f\u6aa2\u8996",
      "url" : "system/sequence"
    } ]
  }, {

    "name" : "\u7cfb\u7edf\u529f\u80fd",
    "url" : "sample",
    "child" : [ {
      "name" : "\u6a94\u6848\u4e0a\u4e0b\u50b3",
      "url" : "sample/fileUpdDwn"
    } ]
  }, {
    "name" : "\u6392\u7a0b\u7ba1\u7406",
    "url" : "batch",
    "child" : [ {
      "name" : "\u6392\u7a0b\u8a2d\u5b9a",
      "url" : "batch/schedule"
    }, {
      "name" : "\u6392\u7a0bJob\u6e05\u55ae",
      "url" : "batch/jobs"
    }, {
      "name" : "\u6392\u7a0b\u76e3\u63a7",
      "url" : "batch/jobexecution"
    } ]

  } ]
}

// init
$(function() {
  // console.debug("cust common ready init");
  var navTop = $("nav.top"), navSub = $("nav.sub ol");
  function render(res) {
    var _menu = res.child, ul = $("nav.top ul.navmenu");
    // $("#userName").val(res.userName);
    navTop.on("click", "li a", function(ev) {
      ev.preventDefault();
      router.to($(this).attr("url"));
      $("article").empty();
    });

    navSub.on("click", "li a", function(ev) {
      var $this = $(this);
      if ($this.attr("url")) {
        router.to($(this).attr("url"));
      } else {
        if ($this.siblings("ul").length) {
          var sel = $this.siblings("ul");
          sel.is(":visible") ? sel.hide().parent("li").children("a").removeClass('clicked').children("span").removeClass('icon-5').addClass('icon-1') : sel.show().parent("li").children("a").addClass(
              'clicked').children("span").removeClass('icon-1').addClass('icon-5');
        }
      }
      ev.preventDefault();
      return false;
    });

    // render menu
    for ( var m in _menu) {
      ul.append($("<li/>").append($("<a/>", {
        href : "#",
        url : _menu[m].url,
        data : {
          smenu : _menu[m].child,
          url : _menu[m].url
        },
        text : _menu[m].name
      })));
    }

    router.set({
      routes : {
        "" : "loadsub", // default route
        ":page" : "loadsub", // http://xxxxx/xxx/#page
        ":page/:page2" : "loadpage" // http://xxxxx/xxx/#page/page2
      },
      loadfirst : function() {
        ul.find("li a:first").click();
      },
      loadsub : function(folder) {
        folder = "function"; // \u6240\u6709\u9078\u55ae\u90fd\u662f\u5728function\u4e4b\u4e0b\uff0c\u6240\u4ee5\u5beb\u6b7b
        var tlink = navTop.find("a").removeClass("select").filter("a[url=" + folder + "]").addClass("select");
        var smenu = tlink.data("smenu");
        if (navSub.find('a').length) {
          navSub.animate({
            opacity : 0.01
          }, 200, _f);
        } else {
          navSub.css("opacity", "0.01");
          _f();
        }

        function _s(root, s_menu) {
          for ( var sm in s_menu) {
            if (s_menu[sm].child && s_menu[sm].child.length != 0) {
              root.append($("<li/>").append($("<a/>", {
                url : "",
                data : {
                  url : ""
                },
                text : s_menu[sm].name
              }).prepend("<span class='menu-icon icon-1'></span>")).append("<ul class='menu_sub'></ul>"));

              _s(root.find("li ul").last(), s_menu[sm].child);
            } else if (s_menu[sm].url) {
              root.append($("<li/>").append($("<a/>", {
                url : s_menu[sm].url || "",
                data : {
                  url : s_menu[sm].url || ""

                },
                text : s_menu[sm].name
              }).prepend("<span class='menu-icon icon-4'></span>")));
            } else {
              root.append($("<li/>").append($("<a/>", {
                url : '#',
                data : {
                  url : '#'
                },
                text : s_menu[sm].name
              })));
            }
          }
        }

        function _f() {
          navSub.empty().data("cmenu", folder);
          _s(navSub, smenu);
          navSub.animate({
            opacity : 1
          });
        }
      },
      // router method
      loadpage : function(folder, page) {
        var topMenu = navTop.find("a").filter(function() {
          return filter($(this).data("smenu"), folder + "/" + page);
        });
        var topFolder = topMenu.attr("url");
        var refresh = navSub.data("cmenu") ? false : true;
        if (refresh) {
          this.loadsub("function");// \u53ea\u6709\u4e00\u5c64function\u529f\u80fd
        }

        // \u9078\u53d6\u9078\u55ae\u63a7\u5236
        if (navSub.find("a[url='" + folder + '/' + page + "']").length != 0) {
          navSub.find('.selected').removeClass('selected').end().find("a[url='" + folder + '/' + page + "']").addClass("selected");
          // breadcrumb
          var parentFuncName = navSub.find("a[url='" + folder + '/' + page + "']").closest("ul").prev("a").text();
          var funcName = navSub.find("a[url='" + folder + '/' + page + "']").text();
          $("#breadcrumb").html("<img src='../static/images/111.gif'/>" + parentFuncName + "<img src='../static/images/last-grey.s.gif'/>" + funcName);
          // end of breadcrumb
          if (refresh) {
            navSub.find('.selected').parents(".menu_sub").siblings("a").click();
          }
        }

        API.loadPage('query/' + folder + '/' + page);

        function filter(topSmenu, target) {
          for ( var m in topSmenu) {
            if (topSmenu[m].url == target) {
              return true;
            }
            if (topSmenu[m].child) {
              if (filter(topSmenu[m].child, target)) {
                return true;
              }
            }
          }
          return false;
        }
      }
    });
    return true;
  }

  // whit menuhandler start
  menu = false;
  // whit menuhandler end
  navTop.length && $.get(url("menuhandler/queryMenu")).done(function(res) {
    render(res);
  });
  // navTop.length && (menu && render(menu)) || testRender();
  // function testRender(){
  // var res = {};
  // render(res);
  // }

  $("a[href='#language']").click(function() {
    var o = $(this).parents("ol");
    if (o.height() == 18) {
      $(this).parent("li.lang").css('background-image', 'url(' + baseUrl + '/images/icon-down.png)');
      $(o).animate({
        height : 100
      });
    } else {
      $(this).parent("li.lang").css('background-image', 'url(' + baseUrl + '/images/icon-right.png)');
      $(o).animate({
        height : 18
      });
    }
    return false;
  });

  require(
      [ 'jquery-ui' ],
      function(jqueryui) {
        $.datepicker._gotoTodayOriginal = $.datepicker._gotoToday;
        $.datepicker._gotoToday = function(id) {
          // now, call the original handler
          $.datepicker._gotoTodayOriginal.apply(this, [ id ]);
          // invoke selectDate to select the current date and close datepicker.
          var target = $(id), inst = this._getInst(target[0]);
          var dateStr = (dateStr != null ? dateStr : this._formatDate(inst));
          inst.input.val(dateStr);
        };

        $.datepicker._generateMonthYearHeaderOriginal = $.datepicker._generateMonthYearHeader;
        $.datepicker._generateMonthYearHeader = function(inst, drawMonth, drawYear, minDate, maxDate, secondary, monthNames, monthNamesShort) {

          var inMinYear, inMaxYear, month, years, thisYear, determineYear, year, endYear, changeMonth = this._get(inst, "changeMonth"), changeYear = this._get(inst, "changeYear"), showMonthAfterYear = this
              ._get(inst, "showMonthAfterYear"), html = "<div class='ui-datepicker-title'><div class='ui-widget-content '>" + i18n.def.selectDate + "</div>", monthHtml = "";

          // month selection
          if (secondary || !changeMonth) {
            monthHtml += "<span class='ui-datepicker-month'>" + monthNames[drawMonth] + "</span>";
          } else {
            inMinYear = (minDate && minDate.getFullYear() === drawYear);
            inMaxYear = (maxDate && maxDate.getFullYear() === drawYear);
            monthHtml += "<select class='ui-datepicker-month' data-handler='selectMonth' data-event='change'>";
            for (month = 0; month < 12; month++) {
              if ((!inMinYear || month >= minDate.getMonth()) && (!inMaxYear || month <= maxDate.getMonth())) {
                monthHtml += "<option value='" + month + "'" + (month === drawMonth ? " selected='selected'" : "") + ">" + monthNamesShort[month] + "</option>";
              }
            }
            monthHtml += "</select>";
          }

          if (!showMonthAfterYear) {
            html += monthHtml + (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "");
          }

          // year selection
          if (!inst.yearshtml) {
            inst.yearshtml = "";
            if (secondary || !changeYear) {
              html += "<span class='ui-datepicker-year'>" + drawYear + "</span>";
            } else {
              // determine range of years to display
              years = this._get(inst, "yearRange").split(":");
              thisYear = new Date().getFullYear();
              determineYear = function(value) {
                var year = (value.match(/c[+\-].*/) ? drawYear + parseInt(value.substring(1), 10) : (value.match(/[+\-].*/) ? thisYear + parseInt(value, 10) : parseInt(value, 10)));
                return (isNaN(year) ? thisYear : year);
              };
              year = determineYear(years[0]);
              endYear = Math.max(year, determineYear(years[1] || ""));
              year = (minDate ? Math.max(year, minDate.getFullYear()) : year);
              endYear = (maxDate ? Math.min(endYear, maxDate.getFullYear()) : endYear);
              inst.yearshtml += "<select class='ui-datepicker-year' data-handler='selectYear' data-event='change'>";
              for (; year <= endYear; year++) {
                inst.yearshtml += "<option value='" + year + "'" + (year === drawYear ? " selected='selected'" : "") + ">" + '\u6c11\u570b' + (parseInt(year) - 1911) + '\u5e74' + "</option>";
              }
              inst.yearshtml += "</select>";

              html += inst.yearshtml;
              inst.yearshtml = null;
            }
          }

          html += this._get(inst, "yearSuffix");
          if (showMonthAfterYear) {
            html += (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "") + monthHtml;
          }
          html += "</div>"; // Close datepicker_header
          return html;
        };

        $.datepicker.setDefaults({
          closeText : '\u95dc\u9589',
          currentText : '\u4eca\u5929',
          dayNamesMin : [ "\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d" ],
          monthNames : [ "\u4e00\u6708", "\u4e8c\u6708", "\u4e09\u6708", "\u56db\u6708", "\u4e94\u6708", "\u516d\u6708", "\u4e03\u6708", "\u516b\u6708", "\u4e5d\u6708", "\u5341\u6708", "\u5341\u4e00\u6708", "\u5341\u4e8c\u6708" ],
          monthNamesShort : [ "\u4e00\u6708", "\u4e8c\u6708", "\u4e09\u6708", "\u56db\u6708", "\u4e94\u6708", "\u516d\u6708", "\u4e03\u6708", "\u516b\u6708", "\u4e5d\u6708", "\u5341\u6708", "\u5341\u4e00\u6708", "\u5341\u4e8c\u6708" ],
          showMonthAfterYear : true,
          onChangeMonthYear : function(year, month, inst) {
            var ym = API.getToday().substr(0, 7), changeYm = year + "/" + (month < 10 ? "0" : "") + month;
            if (ym !== changeYm) {
              $(this).datepicker('setDate', changeYm + '/01');
            }
          },
          dateFormat : 'yy/mm/dd'
        });

      });

  /* timeout controls */
  // Do idle process
  var idleDuration = 10;
  try {
    idleDuration = prop && prop[Properties.timeOut];
  } catch (e) {
    logDebug("Can't find prop");
  }

  // \u8a08\u6578\u5668\u6e1b\u5dee(\u9019\u88e1\u662f\u5206\u9418)
  var gapTime = 1;
  if (Properties.remindTimeout) {
    // #Cola235 \u589e\u52a0\u5207\u63db\u9801reset timer
    // \u8a08\u6578\u5668(\u9019\u88e1\u662f\u6beb\u79d2)
    window.timecount = (idleDuration - gapTime) * 60 * 1000;
    logDebug("set timer time::" + timecount);
    var t1merConfirm = [];
    var timer2 = null;
    // TIMER FUNC1
    var cccheckMethod = function(dxx) {
      $.ajax({
        url : url('checktimeouthandler/check'),
        asyn : true,
        data : {
          isContinues : dxx.isContinues
        }
      }).done(function(d) {
        if (d.errorPage) {
          window.setCloseConfirm(false);
          window.location = d.errorPage;
        }
      });
    };
    // TIMER FUNC2
    var takeTimerReset = function() {
      timer.reset(timecount);
    };
    window.timer = $.timer(timecount, function() {
      var pathname = window.location.pathname;
      if (!/(timeout)$|(error)$/i.test(pathname)) {
        if (t1merConfirm != undefined && t1merConfirm[0] && t1merConfirm[0].hidden == false) {
          // DO NOTTHING
        } else {
          timer2 = $.timer(gapTime * 60 * 1000, function() {
            // \u8d85\u904e\u6642\u9593\u6c92\u7d66\u78ba\u8a8d\u52d5\u4f5c,\u5c31\u7576\u505a\u53d6\u6d88\u4ea4\u6613
            cccheckMethod({
              isContinues : false
            });
          }, false);
          t1merConfirm = CommonAPI.showConfirmMessage('\u60a8\u5df2\u9592\u7f6e\uff0c\u8acb\u554f\u662f\u5426\u7e7c\u7e8c\u7533\u8acb\u4f5c\u696d?', function(data) {
            timer2.stop();
            cccheckMethod({
              isContinues : data
            });
            // \u6309\u4e86\u4e4b\u5f8c,\u8981\u91cd\u65b0\u5012\u6578
            t1merConfirm = [];
            takeTimerReset();
          });
        }
      }
    }, false);
    // IDLE\u7559\u8457\uff0c\u7576user\u6c92\u770b\u5230confirm pop\uff0c\u6642\u9593\u5230\u4e86idle\u9084\u662f\u8981\u5c0e\u5012timeout?
    ifvisible && ifvisible.setIdleDuration(idleDuration * 60);// minute*60
    // logDebug("idleDuration is ::: " + idleDuration);
    ifvisible.on('idle', function() {
      $.unblockUI();
      $.ajax({
        url : url('checktimeouthandler/check'),
        asyn : true,
        data : {},
        success : function(d) {
          if (d.errorPage) {
            window.setCloseConfirm(false);
            window.location = d.errorPage;
          }
        }
      });
    });
    ifvisible.on('wakeup', function() {
      // $(".ui-dialog-content").dialog("close");
    });
  }

  // window.i18n.load("messages", {async: true}).done(function() {
  // $.extend(Properties, {
  // myCustMessages : {
  // custom_error_messages : {
  // '#myName' : {
  // 'required' : {
  // 'message' : i18n.messages('myName.required')
  // },
  // 'fieldName' : {
  // 'message' : i18n.messages('myName.fieldName')
  // }
  // },
  // '.mine' : {
  // 'required' : {
  // 'message' : i18n.messages('mine.required')
  // }
  // }
  // }
  // },
  // myCustRegEx : {
  // 'minSize' : {
  // 'regex' : 'none',
  // 'alertText' : i18n.messages('minSize.alertText'),
  // 'alertText2' : i18n.messages('minSize.alertText2')
  // },
  // 'myCustValid' : {
  // 'regex' : /^(0)(9)([0-9]{8})?$/,
  // 'alertText' : i18n.messages('myCustValid.alertText')
  // }
  // }
  // });
  // });

  // cust valiation regex
  $.extend($.validationEngineLanguage.allRules, Properties.myCustRegEx);

  // cust valid method
  $.extend(window, {
    _minSize : function(field, rules, i, options) {
      var min = rules[i + 2], len = field.val().length, mId = '#' + field.attr('id'), custMsg = '';
      if (len < min) {
        if (typeof options.custom_error_messages[mId] != "undefined" && typeof options.custom_error_messages[mId]['fieldName'] != "undefined") {
          custMsg = options.custom_error_messages[mId]['fieldName']['message'];
        }
        var rule = options.allrules.minSize;
        return custMsg + rule.alertText + min + rule.alertText2;
      }
    },
    regex : function(field, rules, i, options) {
      var val = field.val();
      rules.push('required');
      var r = new RegExp(options.allrules[rules[i + 2]].regex);
      if (val) {
        if (!r.test(val)) {
          return options.allrules[rules[i + 2]].alertText;
        }
      }
    }
  });

  $.extend(window.API, {
    confirmAction : function(message, action) {
      if (!confirm(message)) {
        return;
      }
      action && action();
    }
  });

  // common item
  $.extend(Properties, {
    itemMaskRule : {
      "[class=date]" : function(val) {
        var $this = $(this), val = $.trim($this.val()), a = $this.data("realValue").split('/');
        if (a.length == 3) {
          var y = parseInt(a[0]);
          $this.data("realValue", ((y > 1911) ? y - 1911 : y) + '/' + a[1] + '/' + a[2]);
        }
        return val;
      },
      "[class*=numeric]" : function(val) {
        /* 2012-03-12 for subfix */
        var $this = $(this), val = $this.val() + "", _sf = $this.attr("subfix") || "";
        /* 20130108 \u4e0d\u53ef\u8f38\u5165SPACE */
        val = $.trim($this.val());
        if (val && /[0-9,.]+/.test(val)) {
          var re = new RegExp(_sf, "g");
          val = val.replace(/,/g, "").replace(re, "");
          $this.data("realValue", val);
          // 2011-10-20 update for negative number by Sunkist
          return val.replace(/^(-?)(\d+)((\.\d+)?)$/, function(s, s0, s1, s2) {
            return s0 + s1.replace(/\d{1,3}(?=(\d{3})+$)/g, "$&,") + s2 + _sf;
          });
        }
        return val;
      },
      "[class*=maskVal]" : function(val) {
        var $this = $(this), val = $this.val() + "";
        val = val.toUpperCase();
        if (val) {
          if ($this.data(Properties.maskStr)) {
            return $this.__val();
          }
          $this.data("realValue", val);
          $this.data(Properties.maskStr, true);
          return val.replace(/^(.)(.*)(.)$/i, function(s, s0, s1, s2) {
            return s0 + s1.replace(/./g, Properties.maskStr) + s2;
          });
        }
        return val;
      },
      "[class*=fullText]" : function(val) {// \u8f49\u5168\u5f62
        var $this = $(this), val = $this.val() + "";
        var fVal = val.toFull();
        $this.data("realValue", fVal);
        return fVal;
      },
      "[class*=upText]" : function(val) {// \u8f49\u5927\u5beb
        var $this = $(this), val = $this.val() + "";
        var uVal = val.toUpperCase();
        $this.data("realValue", uVal);
        return uVal;
      },
      "[class*=trim]" : function(val) {// \u53bb\u9664\u591a\u9918\u7a7a\u767d
        var $this = $(this);
        var uVal = $.trim($this.val());
        $this.data("realValue", uVal);
        return uVal;
      }
    }
  });
});
