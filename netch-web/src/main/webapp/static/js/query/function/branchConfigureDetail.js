pageInit(function() {
    $(document).ready(function() {
        var mform = $("#actionForm");
        var action = router.params && router.params.action || '';
        var row = router.params && router.params.resp || '';
        row && mform.injectData(row);

        function redirectResult(resp, action) {
            router.to('function/branchConfigureResult', {
                'resp': resp,
                'action': action
            });
        }

        function checkIfDataExist(funcCall) {
            $.ajax({
                url: url("configurebranchhandler/checkIfDataExist"),
                data: $.extend(mform.serializeData(), {
                    'action': action
                })
            }).done(function(json) {
                funcCall && funcCall(json, action);
            });
        }

        if (action !== 'save') {
            mform.find("#departmentId").readOnly(true);
        }


        mform.find("#execute").click(function() {
            mform.find("#errorMsgSection").val("");

            API.confirmAction(i18n.branchConfigure(['branchConfigure.msg.07']),function(){
              checkIfDataExist(function(json, action) {
                if (json.errorMsgSection) {
                  redirectResult(json, action);
                }else{
                  $.ajax({
                      url: url("configurebranchhandler/configure"),
                      data: $.extend(mform.serializeData(), {'action': action})
                  }).done(function(resp, textStatus, jqXHR) {
                      redirectResult(resp, action);
                  });
                }
              });
            });

        }).end().find("#reload").click(function() {

          API.confirmAction(i18n.branchConfigure(['branchConfigure.msg.08']),function(){
            if (action === 'save'){
              mform.reset();
            }
            checkIfDataExist(function(json, action) {
              json && mform.injectData(json);
            });
          });
        }).end().find("#cancel").click(function() {

          API.confirmAction(i18n.branchConfigure(['branchConfigure.msg.08']),function(){
            router.to('function/branchConfigure');
          });
        });
    });
});
