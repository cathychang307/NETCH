pageInit(function() {
  $(document).ready(function() {
    var mform = $('#mform');

    var roleId = router.params.roleId;
    var tableContainer = $("#tableContainer");
    renderFunctionOptions();

    function renderFunctionOptions() {
      $.ajax({
        url : url("configureroledetailhandler/renderFunctionOptions"),
        success : function(json) {
          tableContainer.html(json.htmlStr);
          loadData();
        }
      });
    }

    function loadData() {
      resetAll();
      $.ajax({
        url : url("configureroledetailhandler/loadData"),
        data : {
          roleId : roleId,
          action : 'modify'
        },
        success : function(json) {
          mform.injectData(json);
          var array = json.functionIds.split(",");
          for(var i in array){
            $("#" + array[i]).prop("checked", true);
          }
        }
      });
    }
    
    function resetAll(){
      mform.reset();
      $('[name="functionIds"]').prop("checked", false);
    }
    
    function updateRole() {
      $.ajax({
        url : url("configureroledetailhandler/updateRole"),
        data : $.extend(mform.serializeData(), {
          functionIds : $('[name="functionIds"]:checked').map(function() {
            return this.value;
          }).get(),
          action : 'update'
        }),
        success : function(json) {
          router.to('function/roleOperationResult', json);
        }
      });
    }
    
    function deleteRole() {
      $.ajax({
        url : url("configureroledetailhandler/deleteRole"),
        data : $.extend(mform.serializeData(), {
          action : 'delete'
        }),
        success : function(json) {
          router.to('function/roleOperationResult', json);
        }
      });
    }

    function reset() {
      $("#inChargeBankId").val("");
      $("#inputYear").val(new Date().getFullYear() - 1911);
      $("#inputMonth").val(new Date().getMonth() + 1);
      $("#errorMsgSection").val("");
    }

    $(".btns").find('#update').click(function() {
      if(!confirm(i18n['configureRoleDetail']['configureRoleDetail.btn.msg.01'])){ //確定要 [變更] 這個角色嗎
        return;
      }
      updateRole();
    }).end().find('#reload').click(function() {
      if(!confirm(i18n['configureRoleDetail']['configureRoleDetail.btn.msg.02'])){ //確定 [放棄目前的資料] 並 [重新載入] 角色資料嗎
        return;
      }
      loadData();
    }).end().find('#cancel').click(function() {
      if(!confirm(i18n['configureRoleDetail']['configureRoleDetail.btn.msg.03'])){ //確定 [放棄目前的資料] 並 [回到上一頁] 嗎
        return;
      }
      router.to('function/configureRole');
    }).end().find('#deleteRole').click(function() {
      if(!confirm(i18n['configureRoleDetail']['configureRoleDetail.btn.msg.04'])){ //確定要 [刪除] 這個角色嗎
        return;
      }
      deleteRole();
    });
    
    


  });
});
