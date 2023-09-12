pageInit(function() {
    $(document).ready(function() {
        window.setCloseConfirm(false);
        var resp = router.params && router.params.resp || '';
        // resp && resp.connResult && API.showErrorMessage(resp.connResult);
        // console.debug(resp);
        resp && $("#mform").injectData(resp);

        $(".btns").find("#complete").click(function() {
            API.formSubmit({
                url: url('page/index')
            });
        });
    });
});
