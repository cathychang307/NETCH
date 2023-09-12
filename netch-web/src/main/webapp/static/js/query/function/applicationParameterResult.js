pageInit(function() {
  $(document).ready(function() {
    window.setCloseConfirm(false);
    var resp = router.params && router.params.resp || '';
    // resp && resp.connResult && API.showErrorMessage(resp.connResult);
    // console.debug(JSON.stringify(resp));
    if (resp && resp.title) {
      var t = document.title.match(/(.*)- /);
      if (t && t[0]) {
        // console.debug(t);
        document.title = t[0] + resp.title;
      }
    }

    if(resp){
      $("#mform").injectData(resp);
      $("#title").val(resp.title);
    }

    $(".btns").find("#complete").click(function() {
      API.formSubmit({
        url : url('page/index')
      });
    });
  });
});
