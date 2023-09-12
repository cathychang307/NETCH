pageInit(function() {
  $(document).ready(function() {
    var mform = $("#actionForm");
    var row = router.params && router.params.resp || '';
    row && mform.injectData(row);

    function redirectResult(resp) {
      router.to('function/singleQueryResult', {
        'resp' : resp
      });
    }

    mform.find("#sure").click(function() {

      mform.find("#errorMsgSection").val("");
      $.ajax({
        url : url("singlequeryhandler/inquiry"),
        data : mform.serializeData()
      }).done(function(resp, textStatus, jqXHR) {
        if (resp.errorMsgSection) {
          mform.find("#errorMsgSection").val(resp.errorMsgSection);
        } else {
          redirectResult(resp);
        }
      });

    }).end().find("#clear").click(function() {

      mform.reset();
    });
  });
});
