pageInit(function() {
    $(document).ready(function() {
        var editBtnStr = i18n.branchConfigure['branchConfigure.btn.01'];
        var delBtnStr = i18n.branchConfigure['branchConfigure.btn.02'];
        var editHtml = "<button class='editrowbtn' type='button' onclick=\"router.to('function/branchConfigureDetail', {'action': 'modify'});\">"+editBtnStr+"</button>";
        var delHtml1 = "<button class='delrowbtn' type='button' onclick=\"API.confirmAction(i18n.branchConfigure(['branchConfigure.msg.06'],['";
        var delHtml2 = "']),function(){$.ajax({url: url('configurebranchhandler/configure'),data: {'action': 'delete', 'departmentId': '";
        var delHtml3 = "'}}).done(function(resp, textStatus, jqXHR) {router.to('function/branchConfigureResult', {'resp': resp,'action': 'delete'});});});\">"+delBtnStr+"</button>";
        var mform = $("#actionForm"),
            gridzone = $("#gridzone"),
            grid = $("#gridview").jqGrid({
                url: url('configurebranchhandler/queryForMaintain'),
                height: "100",
                width: "100%",
                postData: {},
                hideMultiselect: true,
                autowidth: true,
                pager: false,
                localFirst: true,
                colModel: [{
                    name: i18n.def.comboSpace,
                    index: 'departmentId',
                    width: 25,
                    sortable: false,
                    formatter: function(cellvalue, options, rowObject) {
                        return editHtml + delHtml1 + cellvalue + delHtml2 + cellvalue + delHtml3;
                    }
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.01'],
                    name: 'departmentId',
                    width: 25,
                    sortable: false
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.02'],
                    name: 'departmentName',
                    width: 25,
                    sortable: false
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.03'],
                    name: 'chargeBankId',
                    width: 25,
                    sortable: false
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.04'],
                    name: 'chargeBankName',
                    width: 25,
                    sortable: false
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.05'],
                    name: 'tchId',
                    width: 25,
                    sortable: false
                }, {
                    header: i18n.branchConfigure['branchConfigure.grid.title.06'],
                    name: 'memo',
                    width: 25,
                    sortable: false
                }],
                onSelectRow: function() {
                    var sel = grid.getSelRowDatas();
                    if (sel.departmentId) {
                        router.params = $.extend(router.params, {
                            'resp': sel
                        });
                    }
                }
            });

        // UI dislplay
        gridzone.hide();
        $.ajax({
            url: url('configurebranchhandler/queryBranches'),
        }).done(function(resp) {
            resp && mform.find("#selectedDepartmentId").setOptions(resp);
        });

        function query() {
            grid.jqGrid('setGridParam', {
                postData: mform.serializeData()
            });
            grid.trigger("reloadGrid");
        }

        mform.find(".btns").find('#query').click(function() {
            gridzone.show();
            mform.find("#errorMsgSection").val("");
            grid.clearGridData(true);
            query();
        }).end().find("#save").click(function() {
            gridzone.hide();
            mform.find("#errorMsgSection").val("");
            grid.clearGridData(true);
            router.to('function/branchConfigureDetail', {
                'action': 'save'
            });
        }).end().find("#complete").click(function() {
            API.formSubmit({
                url: url('page/index')
            });
        });


    });
});
