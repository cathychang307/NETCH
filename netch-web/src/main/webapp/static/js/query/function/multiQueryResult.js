pageInit(function() {
  $(document).ready(function() {
    window.setCloseConfirm(false);
    var mform = $("#actionForm");
    var resp = router.params && router.params.resp || '';
    resp && mform.injectData(resp);

    mform.find("#complete").click(function() {
      API.formSubmit({
        url : url('page/index')
      });
    }).end().find("#print").click(function() {
      print();
    });

    function print() {
      window.print();
      $.ajax({
        url : url("singlequeryhandler/printWriteLog"),
        data : $.extend(mform.serializeData(), {
          'resp' : resp
        }),
      });
    }

  });
});
