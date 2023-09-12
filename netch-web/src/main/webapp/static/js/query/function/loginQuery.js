pageInit(function() {
  $(document).ready(function() {
    var mform = $('#mform');
    loadOptions();
    reset();
    function loadOptions() {
      // dropdown menu
      $.ajax({
        url : url("loginqueryhandler/loadOptions"),
        success : function(json) {
          $("#departmentId").setOptions(json.departmentMap);
        }
      });
    }

    function reset() {
      $("#userId").val("");
      $("#departmentId").val("");
      $("#startDate").val("");
      $("#endDate").val("");
    }

    var grid = $("#gridview").jqGrid({
      url : url('loginqueryhandler/query'),
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
        header : i18n['loginQuery']['loginQuery.grid.title.01'], // 時間
        name : 'accessDatetime',
        width : 20,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.02'], // 使用者ID
        name : 'userId',
        width : 10,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.03'], // 使用者姓名
        name : 'userName',
        width : 10,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.04'], // 單位
        name : 'departmentName',
        width : 10,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.05'], // 角色
        name : 'roleName',
        width : 10,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.06'], //行為
        name : 'primaryStatusDesc',
        width : 25,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.07'], //狀態
        name : 'secondaryStatusDesc',
        width : 5,
        sortable : false
      }, {
        header : i18n['loginQuery']['loginQuery.grid.title.08'], //狀態碼
        name : 'secondaryStatus',
        width : 5,
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
        url : url("loginqueryhandler/downloadCsv"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function downloadPdf(action) {
      $.capFileDownload({
        url : url("loginqueryhandler/downloadPdf"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      });
    }

    function checkIfDataExist(funcCall, action) {
      $("#errorMsgSection").val("");
      $.ajax({
        url : url("loginqueryhandler/checkIfDataExist"),
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
