pageInit(function() {
  $(document).ready(function() {
    var grid = $("#gridview").jqGrid({
      url : url('configurerolehandler/query'),
      height : "100",
      width : "100%",
      postData : {
      // oid : oid
      },
      hideMultiselect : true,
      autowidth : true,
      height : 300,
      pager : false,
      colModel : [ {
        header : i18n['configureRole']['configureRole.grid.title.01'], // 角色名稱
        name : 'roleName',
        width : 25,
        sortable : false
      }, {
        header : i18n['configureRole']['configureRole.grid.title.02'], // 角色描述
        name : 'roleDesc',
        width : 25,
        sortable : false
      }, {
        header : i18n['configureRole']['configureRole.grid.title.02'], // 角色描述
        name : 'roleId',
        width : 25,
        sortable : false
      }, {
        header : i18n['configureRole']['configureRole.grid.title.03'],
        name: 'actions',
        width : 15,
        sortable : false,
        formatter: function(cellvalue, options, rowObject) {
          var roleId = rowObject[2];
          var edit;
          if("59040000" == roleId){
            edit = i18n['configureRole']['configureRole.btn.01'];
          }else{
            edit = "<button class='editrowbtn' type='button' onclick=\"router.to('function/configureRoleDetail', {'method': 'edit'});\">"+i18n['configureRole']['configureRole.btn.02']+"</button>";
          }
          return edit;
        }
      } ],
      onSelectRow : function() {
        var sel = grid.getSelRowDatas();
        if (sel.roleId) {
          router.params = {"roleId" : sel.roleId};
        }
      }
    });

    $(".btns").find('#query').click(function() {
      grid.clearGridData(true);
      checkIfDataExist(query);
    });

    function query() {
      grid.jqGrid('setGridParam', {
        postData : mform.serializeData()
      });
      grid.trigger("reloadGrid");
    }

    function checkIfDataExist(funcCall) {
      $("#errorMsgSection").val("");
      $.ajax({
        url : url("chargequeryhandler/checkIfDataExist"),
        data : mform.serializeData(),
        success : function(json) {
          funcCall && funcCall();
        }
      });
    }

  });
});
