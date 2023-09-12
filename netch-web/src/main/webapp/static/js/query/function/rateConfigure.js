pageInit(function() {
  $(document).ready(
      function() {
          var editBtnStr = i18n.rateConfigure['rateConfigure.btn.01'];
          var delBtnStr = i18n.rateConfigure['rateConfigure.btn.02'];
          var editHtml = "<button class='editrowbtn' type='button' onclick=\"router.to('function/rateConfigureDetail', {'action': 'modify'});\">"+editBtnStr+"</button>";
          var delHtml1 = "<button class='delrowbtn' type='button' onclick=\"API.confirmAction(i18n.rateConfigure(['rateConfigure.msg.06'],['";
          var delHtml2 = "']),function(){$.ajax({url: url('configureratehandler/configure'),data: {'action': 'delete', 'inputRateType': '";
          var delHtml3 = "'}}).done(function(resp, textStatus, jqXHR) {router.to('function/rateConfigureResult', {'resp': resp,'action': 'delete'});});});\">"+delBtnStr+"</button>";
          var mform = $("#actionForm"), gridzone = $("#gridzone"), grid = $("#gridview").jqGrid(
            {
              url : url('configureratehandler/queryForMaintain'),
              height : "100",
              width : "100%",
              postData : {},
              hideMultiselect : true,
              autowidth : true,
              pager : false,
              localFirst : true,
              colModel : [
                  {
                    name : 'key.transactionId',
                    hidden : true
                  },
                  {
                    name : 'key.effectDateRocYM',
                    hidden : true
                  },
                  {
                    name : 'transactionShortDesc',
                    hidden : true
                  },
                  {
                    name : 'modifiable',
                    hidden : true
                  },
                  {
                    name : 'key.effectDateRocYear',
                    hidden : true
                  },
                  {
                    name : 'key.effectDateMonth',
                    hidden : true
                  },
                  {
                    name : i18n.def.comboSpace,
                    index : 'editZone',
                    width : 30,
                    sortable : false,
                    formatter : function(cellvalue, options, rowObject) {
                      return rowObject[3] === "true" ? (editHtml + delHtml1 + rowObject[2] + "', '" + rowObject[1] + delHtml2 + rowObject[0] + "', 'inputYear': '" + rowObject[4]
                          + "', 'inputMonth': '" + rowObject[5] + delHtml3) : i18n.rateConfigure['rateConfigure.currntRate'];
                    }
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.01'],
                    name : 'transactionType',
                    width : 25,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.02'],
                    name : 'transactionName',
                    width : 35,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.03'],
                    name : 'inputRateType',
                    width : 25,
                    sortable : false,
                    formatter : function(cellvalue, options, rowObject) {
                      return rowObject[0];
                    }
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.04'],
                    name : 'transactionRate',
                    width : 25,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.05'],
                    name : 'transactionPoundage',
                    width : 25,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.06'],
                    name : 'key.effectDateRocYMD',
                    width : 25,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.07'],
                    name : 'transactionRecordsAtDiscount',
                    width : 25,
                    sortable : false
                  }, {
                    header : i18n.rateConfigure['rateConfigure.grid.title.08'],
                    name : 'transactionDiscountRate',
                    width : 25,
                    sortable : false,
                    formatter : function(cellvalue, options, rowObject) {
                      return (parseFloat(cellvalue) * 100).toFixed(1);
                    }
                  }, {
                    name : 'inputYear',
                    hidden : true,
                    formatter : function(cellvalue, options, rowObject) {
                      return rowObject[4];
                    }
                  }, {
                    name : 'inputMonth',
                    hidden : true,
                    formatter : function(cellvalue, options, rowObject) {
                      return rowObject[5];
                    }
                  } ],
              onSelectRow : function() {
                var sel = grid.getSelRowDatas();
                if (sel.inputRateType) {
                  router.params = $.extend(router.params, {
                    'resp' : sel
                  });
                }
              }
            });

        // UI dislplay
        gridzone.hide();
        $.ajax({
          url : url('configureratehandler/queryRateTypes'),
        }).done(function(resp) {
          resp && mform.find("#inputRateType").setOptions(resp);
        });

        function query() {
          grid.jqGrid('setGridParam', {
            postData : mform.serializeData()
          });
          grid.trigger("reloadGrid");
        }

        mform.find(".btns").find('#query').click(function() {
          gridzone.show();
          mform.find("#errorMsgSection").val("");
          grid.clearGridData(true);
          query();
        }).end().find("#save").click(function() {
          gridzone.hide();
          mform.find("#errorMsgSection").val("");
          grid.clearGridData(true);
          router.to('function/rateConfigureDetail', {
            'action' : 'save'
          });
        }).end().find("#complete").click(function() {
          API.formSubmit({
            url : url('page/index')
          });
        });

      });
});
