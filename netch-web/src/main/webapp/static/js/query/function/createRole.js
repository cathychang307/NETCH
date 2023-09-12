pageInit(function() {
  $(document).ready(function() {

    
    var mform = $('#mform');
    
    $(".btns").find('#sure').click(function() {
      $.ajax({
        url : url("createrolehandler/createQueryRole"),
        data : mform.serializeData(),
        success : function(json) {
          router.to('function/roleOperationResult', json);
        }
      });
    }).end().find('#clear').click(function() {
      mform.reset();
      $("#errorMsgSection").val("");
    }).end().find('#cancel').click(function() {
      window.setCloseConfirm(false);
      API.formSubmit({
        url : url('page/index')
      });
    });
  });
});
