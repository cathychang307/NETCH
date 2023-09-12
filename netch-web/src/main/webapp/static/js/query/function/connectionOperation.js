pageInit(function() {
    $(document).ready(function() {
        var actionForm = $("#actionForm");
        $(".btns").find('#sendOperation').click(function() {
            $.ajax({
                url: url("connectionoperationhandler/checkBinding"),
                data: actionForm.serializeData()
            }).done(function() {
                $("#errorMsgSection").val("");
                $.ajax({
                    url: url("connectionoperationhandler/run"),
                    data: actionForm.serializeData()
                }).done(function(resp, textStatus, jqXHR) {
                    router.to('function/connectionOperationResult', {
                        'resp': resp
                    });
                }).fail(function() {
                    // console.debug('run fail!');
                });
            }).fail(function() {
                // console.debug('checkBinding fail!');
            });
        });
    });
});
