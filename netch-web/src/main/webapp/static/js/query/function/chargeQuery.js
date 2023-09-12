pageInit(function() {
  $(document).ready(function() {
    var mform = $('#mform');
    loadOptions();
    reset();
    function loadOptions() {
      // dropdown menu
      $.ajax({
        url : url("chargequeryhandler/getInChargeBankId"),
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

      //test
      // $("#inputYear").val(96);
      // $("#inputMonth").val(9);
      // $("#inChargeBankId").val("0041193");
    }

    var grid = $("#gridview").jqGrid({
      url : url('chargequeryhandler/query'),
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
        header : i18n['chargeQuery']['chargeQuery.grid.title.01'], // 分行名稱
        name : 'bankName',
        width : 25,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.02'], // 分行代號
        name : 'inputChargeBankId',
        width : 25,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.03'], // 總筆數
        name : 'sum',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.04'], // 總金額
        name : 'totals',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.05'], // 一類筆數
        name : 'sum1_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.06'], // 一類金額
        name : 'totalsFirst',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.07'], // 二類筆數
        name : 'sum1_7',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.08'], // 二類金額
        name : 'totalsSecond',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.09'], // 甲類筆數
        name : 'sum2_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.10'], // 甲類金額
        name : 'totalsOne',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.11'], // 乙類筆數
        name : 'sum2_5',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.12'], // 乙類金額
        name : 'totalsTwo',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.13'], // OBU總金額
        name : 'totalsObu',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.14'], // OBU一類筆數
        name : 'sum3_0',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.15'], // OBU一類金額
        name : 'totalsObuOne',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.16'], // OBU二類筆數
        name : 'sum3_5',
        width : 15,
        sortable : false
      }, {
        header : i18n['chargeQuery']['chargeQuery.grid.title.17'], // OBU二類金額
        name : 'totalsObuTwo',
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
        url : url("chargequeryhandler/downloadCsv"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function downloadPdf(action) {
      $.capFileDownload({
        url : url("chargequeryhandler/downloadPdf"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function checkIfDataExist(funcCall, action) {
      $("#errorMsgSection").val("");
      $.ajax({
        url : url("chargequeryhandler/checkIfDataExist"),
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
