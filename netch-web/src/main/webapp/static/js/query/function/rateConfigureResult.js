pageInit(function() {
    $(document).ready(function() {
        window.setCloseConfirm(false);
        var mform = $("#actionForm");
        var action = router.params && router.params.action || '';
        var resp = router.params && router.params.resp || '';
        resp && mform.injectData(resp);
        
        mform.find("#complete").click(function() {
            API.formSubmit({
                url: url('page/index')
            });
        });
    });
});
