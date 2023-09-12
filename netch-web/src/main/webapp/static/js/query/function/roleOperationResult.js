pageInit(function() {
  $(document).ready(function() {

    var roleId = router.params.roleId;
    var roleName = router.params.roleName;
    var roleDesc = router.params.roleDesc;
    var roleFunction = router.params.roleFunction;
    var responseMessage = router.params.responseMessage;
    var roleEnabled = router.params.roleEnabled;
    logDebug(JSON.stringify(router.params));
    
    var mform = $('#mform');
    mform.find("#roleId").val(roleId);
    mform.find("#roleName").val(roleName);
    mform.find("#roleDesc").val(roleDesc);
    mform.find("#roleFunction").html(roleFunction);
    mform.find("#responseMessage").val(responseMessage);
    mform.find("#enabledText").html("0" == roleEnabled ? i18n['roleOperationResult']['roleOperationResult.msg.01'] : "");

    $('#end').click(function() {
      window.setCloseConfirm(false);
      API.formSubmit({
        url : url('page/index')
      });
    });

  });
});
