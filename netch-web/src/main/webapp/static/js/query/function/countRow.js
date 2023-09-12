pageInit(function() {
  $(document).ready(function() {
    var mform = $('#mform');
    loadOptions();
    reset();
    function loadOptions() {
      // dropdown menu
      $.ajax({
        url : url("countrowhandler/getInChargeBankId"),
        success : function(json) {
          $("#inChargeBankId").setOptions(json);
        }
      });
      var year = {};
      for (var i = 96; i <= 120; i++) {
        year[i] = i;
      }

      $("#inputYear").setOptions(year);
      var month = {};
      for (var i = 1; i <= 12; i++) {
        month[i] = i;
      }
      $("#inputMonth").setOptions(month);
    }

    function reset() {
      $("#inChargeBankId").val("");
      $("#inputYear").val(new Date().getFullYear() - 1911);
      $("#inputMonth").val(new Date().getMonth() + 1);
      $("#errorMsgSection").val("");
      // test
      // $("#inputYear").val(96);
      // $("#inputMonth").val(9);
      // $("#inChargeBankId").val("0041193");
    }

    var grid = $("#gridview").jqGrid({
      url : url('countrowhandler/query'),
      height : "100",
      width : "100%",
      postData : {
      // oid : oid
      },
      hideMultiselect : true,
      autowidth : true,
      localFirst : true,
      height : 300,
      colModel : [ {
        header : i18n['countRow']['countRow.grid.title.01'], // 分行名稱
        name : 'bankName',
        width : 25,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.02'], // 分行代號
        name : 'inputChargeBankId',
        width : 25,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.03'], // 總筆數
        name : 'sum',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.04'], // 一類
        name : 'connect_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.05'], // 一類
        name : 'sum1_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.06'], // 一類
        name : 'cacheRows_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.07'], // 二類
        name : 'connect_1',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.08'], // 二類
        name : 'sum1_7',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.09'], // 二類
        name : 'cacheRows_1',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.10'], // 甲類
        name : 'connect_2',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.11'], // 甲類
        name : 'sum2_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.12'], // 甲類
        name : 'cacheRows_2',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.13'], // 乙類
        name : 'connect_3',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.14'], // 乙類
        name : 'sum2_5',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.15'], // 乙類
        name : 'cacheRows_3',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.16'], // OBU一類連線
        name : 'connect_4',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.17'], // OBU一類計費
        name : 'sum3_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.18'], // OBU一類快取
        name : 'cacheRows_4',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.19'], // OBU二類連線
        name : 'connect_5',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.20'], // OBU二類計費
        name : 'sum3_5',
        width : 15,
        sortable : false
      }, {
        header : i18n['countRow']['countRow.grid.title.21'], // OBU二類快取
        name : 'cacheRows_5',
        width : 15,
        sortable : false
      } ]
    });

    $(".btns").find('#query').click(function() {
      grid.clearGridData(true);
      checkIfDataExist(query);
    }).end().find('#clear').click(function() {
      reset();
    }).end().find('#csv').click(function() {
      checkIfDataExist(downloadCsv, "CSV")
    }).end().find('#pdf').click(function() {
      checkIfDataExist(downloadPdf, "PDF");
    });

    function query() {
      grid.jqGrid('setGridParam', {
        postData : mform.serializeData()
      });
      grid.trigger("reloadGrid");
    }

    function downloadCsv(action) {
      $.capFileDownload({
        url : url("countrowhandler/downloadCsv"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function downloadPdf(action) {
      $.capFileDownload({
        url : url("countrowhandler/downloadPdf"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function checkIfDataExist(funcCall, action) {
      $("#errorMsgSection").val("");
      $.ajax({
        url : url("countrowhandler/checkIfDataExist"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        }),
        success : function(json) {
          funcCall && funcCall(action);
        }
      });
    }

  });
});
