pageInit(function() {
  $(document).ready(function() {
    var mform = $("#actionForm");
    var action = router.params && router.params.action || '';
    var row = router.params && router.params.resp || '';

    $.ajax({
      url : url('configureratehandler/queryRateTypes'),
    }).done(function(resp) {
      resp && mform.find("#inputRateType").setOptions(resp);
      var year = {}, month = {};
      for (var i = 96; i <= 120; i++) {
        year[i] = i;
      }
      for (var i = 1; i <= 12; i++) {
        month[i] = i;
      }
      mform.find("#inputYear").setOptions(year).parent().find("#inputMonth").setOptions(month);
      clearAndReset();
    });



    function clearAndReset() {
      if (action === 'save') {
        mform.reset();
        mform.injectData({
          'inputYear' : API.getToday().substr(0, 4) - 1911,
          'inputMonth' : parseInt(API.getToday().substr(5, 2))
        });
      }
      row && mform.injectData(row);
    }

    function redirectResult(resp, action) {
      router.to('function/rateConfigureResult', {
        'resp' : resp,
        'action' : action
      });
    }

    function checkIfDataExist(funcCall) {
      $.ajax({
        url : url("configureratehandler/checkIfDataExist"),
        data : $.extend(mform.serializeData(), {
          'action' : action
        })
      }).done(function(json) {
        funcCall && funcCall(json, action);
      });
    }

    if (action !== 'save') {
      mform.find(".primary").readOnly(true);
    }else{
      mform.find(".primary").removeClass('hide');
    }

    mform.find("#sure").click(function() {
      mform.find("#errorMsgSection").val("");

      API.confirmAction(i18n.rateConfigure([ 'rateConfigure.msg.07' ]), function() {
        checkIfDataExist(function(json, action) {
          if (json.errorMsgSection) {
            redirectResult(json, action);
          } else {
            $.ajax({
              url : url("configureratehandler/configure"),
              data : $.extend(mform.serializeData(), {
                'action' : action
              })
            }).done(function(resp, textStatus, jqXHR) {
              redirectResult(resp, action);
            });
          }
        });
      });

    }).end().find("#clear").click(function() {

      API.confirmAction(i18n.rateConfigure([ 'rateConfigure.msg.08' ]), function() {
        clearAndReset();
      });
    }).end().find("#cancel").click(function() {

      API.confirmAction(i18n.rateConfigure([ 'rateConfigure.msg.08' ]), function() {
        router.to('function/rateConfigure');
      });
    });
  });
});
