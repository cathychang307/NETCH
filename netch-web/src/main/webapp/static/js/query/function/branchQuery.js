pageInit(function() {
    $(document).ready(
        function() {
            var mform = $("#actionForm"),
                gridzone = $("#gridzone"),
                grid = $("#gridview").jqGrid({
                    url: url('configurebranchhandler/query'),
                    height: "100",
                    width: "100%",
                    postData: {},
                    hideMultiselect: true,
                    autowidth: true,
                    pager: false,
                    localFirst: true,
                    colModel: [{
                        header: i18n.branchQuery['branchQuery.grid.title.01'],
                        name: 'departmentId',
                        width: 25,
                        sortable: false
                    }, {
                        header: i18n.branchQuery['branchQuery.grid.title.02'],
                        name: 'departmentName',
                        width: 25,
                        sortable: false
                    }, {
                        header: i18n.branchQuery['branchQuery.grid.title.03'],
                        name: 'chargeBankId',
                        width: 25,
                        sortable: false
                    }, {
                        header: i18n.branchQuery['branchQuery.grid.title.04'],
                        name: 'chargeBankName',
                        width: 25,
                        sortable: false
                    }, {
                        header: i18n.branchQuery['branchQuery.grid.title.05'],
                        name: 'tchId',
                        width: 25,
                        sortable: false
                    }, {
                        header: i18n.branchQuery['branchQuery.grid.title.06'],
                        name: 'memo',
                        width: 25,
                        sortable: false
                    }]
                });

            /*UI dislplay*/
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
            });


        });
});
