pageInit(function() {
  $(document).ready(function() {
    var mform = $("#actionForm"), gridzone = $("#gridzone"), grid = $("#gridview").jqGrid({
      url : url('configureratehandler/query'),
      height : "100",
      width : "100%",
      postData : {},
      hideMultiselect : true,
      autowidth : true,
      pager : false,
      localFirst : true,
      colModel : [ {
        header : i18n.rateQuery['rateQuery.grid.title.01'],
        name : 'transactionType',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.02'],
        name : 'transactionName',
        width : 30,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.03'],
        name : 'key.transactionId',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.04'],
        name : 'transactionRate',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.05'],
        name : 'transactionPoundage',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.06'],
        name : 'key.effectDateRocYMD',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.07'],
        name : 'transactionRecordsAtDiscount',
        width : 25,
        sortable : false
      }, {
        header : i18n.rateQuery['rateQuery.grid.title.08'],
        name : 'transactionDiscountRate',
        width : 25,
        sortable : false,
        formatter : function(cellvalue, options, rowObject) {
          return (parseFloat(cellvalue) * 100).toFixed(1);
        }
      } ]
    });

    /* UI dislplay */
    gridzone.hide();

    $.ajax({
      url : url('configureratehandler/queryRateTypes'),
    }).done(function(resp) {
      resp && mform.find("#inputRateType").setOptions(resp);
    });

    var year = {}, month = {};
    for (var i = 96; i <= 120; i++) {
      year[i] = i;
    }
    for (var i = 1; i <= 12; i++) {
      month[i] = i;
    }
    mform.find("#inputYear").setOptions(year).parent().find("#inputMonth").setOptions(month);

    function clearAndReset() {
      mform.injectData({
        'inputRateType' : '',
        'inputQueryType' : '0',
        'inputYear' : API.getToday().substr(0, 4) - 1911,
        'inputMonth' : parseInt(API.getToday().substr(5, 2))
      });
    }

    clearAndReset();

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
    }).end().find("#clear").click(function() {
      clearAndReset();
    });

  });
});
