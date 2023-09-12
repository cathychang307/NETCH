pageInit(function() {
  $(document).ready(function() {
    var mform = $('#mform');
    loadOptions();
    reset();
    function loadOptions() {
      // dropdown menu
      $.ajax({
        url : url("querydetailhandler/loadOptions"),
        success : function(json) {
          $("#inquiryChargeBankId").setOptions(json.chargeBankMap);
          $("#inquiryTxCode").setOptions(json.transactionRateMap);
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
      $("#startDate").val("");
      $("#endDate").val("");
      $("#inquiryChargeBankId").val("");
      $("#inquiryAccount").val("");
      $("#inquiryTxCode").val("");
      $("#errorMsgSection").val("");
    }

    var grid = $("#gridview").jqGrid({
      url : url('querydetailhandler/query'),
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
        header : i18n['queryDetail']['queryDetail.grid.title.01'], // 查詢日期
        name : 'inquiryDate',
        width : 15,
        sortable : false
      }, {
        header : i18n['queryDetail']['queryDetail.grid.title.02'], // 查詢者ID
        name : 'inquiryAccount',
        width : 15,
        sortable : false
      }, {
        header : i18n['queryDetail']['queryDetail.grid.title.03'], // 查詢者姓名
        name : 'inquiryUserName',
        width : 15,
        sortable : false
      }, {
        header : i18n['queryDetail']['queryDetail.grid.title.04'], // 查詢類型
        name : 'inquiryTxCode',
        width : 15,
        sortable : false
      }, {
        header : i18n['queryDetail']['queryDetail.grid.title.05'], // 詢條件
        name : 'conditions',
        width : 120,
        sortable : false
      }, {
        header : i18n['queryDetail']['queryDetail.grid.title.06'], //快取
        name : 'inquiryCacheFlag',
        width : 10,
        sortable : false
      } ]
    });

    $(".btns").find('#query').click(function() {
      grid.clearGridData(true);
      checkIfDataExist(query);
    }).end().find('#clear').click(function() {
      reset();
      //test
      // $("#startDate").val("96/08/01");
      // $("#endDate").val("96/09/30");
      // $("#inquiryChargeBankId").val("0041193");
      // $("#inquiryAccount").val("094611");
      // $("#inquiryTxCode").val("4111");
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
        url : url("querydetailhandler/downloadCsv"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function downloadPdf(action) {
      $.capFileDownload({
        url : url("querydetailhandler/downloadPdf"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function checkIfDataExist(funcCall, action) {
      $("#errorMsgSection").val("");
      $.ajax({
        url : url("querydetailhandler/checkIfDataExist"),
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
