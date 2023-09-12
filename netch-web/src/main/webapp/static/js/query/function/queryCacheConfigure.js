pageInit(function() {
  $(document).ready(function() {
    window.setCloseConfirm(false);
    var actionForm = $("#actionForm");

    $.ajax({
      url : url("configureparamshandler/getCurrentParamValue"),
      data : {
        parameterName : 'QUERY_CACHE_INTERVAL'
      }
    }).done(function(resp) {
      resp && resp.currentValue && actionForm.injectData({
        'currentvalue' : resp.currentValue,
        'newvalue' : resp.currentValue
      });
    });

    $(".btns").find('#configure').click(function() {
      $.ajax({
        url : url("configureparamshandler/checkQueryCacheBinding"),
        data : actionForm.serializeData()
      }).done(function() {
        $("#errorMsgSection").val("");
        $.ajax({
          url : url("configureparamshandler/configureQueryCache"),
          data : actionForm.serializeData()
        }).done(function(resp, textStatus, jqXHR) {
          router.to('function/applicationParameterResult', {
            'resp' : resp
          });
        }).fail(function() {
          // console.debug('run fail!');
        });
      }).fail(function() {
        // console.debug('checkBinding fail!');
      });
    }).end().find("#cancel").click(function() {
      API.formSubmit({
        url : url('page/index')
      });
    });
  });
});
