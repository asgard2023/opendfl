$('#mainWindow').css('display', 'block');//用于避免chrome下页面刚打开时，画面会有一小会的格式不好的问题
$('#search-btn').click(function () {
    doSearchTree();
});
$('#reset-btn').click(function () {
    $('#search-form')[0].reset();
});

function doSearch() {
    var jsonParam = $('#search-form').serializeJson();
    //status为all时处理
    value_all2empty(jsonParam);
    $('#dg').datagrid('load', jsonParam);
}

function onChangeConfType(newValue, oldValue) {
    console.log('---onChangeConfType--newValue=' + newValue + " oldValue=" + oldValue);
    var url = '/dflSystem/dflSystemConfig/findSysconfigByParentId?confType=' + newValue;
    $('#menuTree').tree({url: url});
}


function doSearchReload() {
    var jsonParam = $('#search-form').serializeJson();
    //status为all时处理
    value_all2empty(jsonParam);
    $('#dg').datagrid('reload', jsonParam);
}


function doSearchTree() {
    var confType = $('#query_confType').combobox('getValue');
    $.ajax({
        type: 'post',
        data: {confType: confType, name: $("#query_name").val()},
        headers: app.headers,
        dataType: 'json',
        url: '/dflSystem/dflSystemConfig/getSysconfigByName',
        success: function (data, textStatus, jqXHR) {
            if (data.success) {
                var data = data.data;
                var id;
                for (var i = 0; i < data.length; i++) {
                    id = data[i].id;
                    //console.log($('#menuTree'));
                    var findNode = $('#menuTree').tree('find', id); //获取当前节点
                    if (findNode) {
                        $("#menuTree").tree("expandTo", findNode.target);
                        $("#menuTree").tree("select", findNode.target); //定位当前节点
                    }
                }
            } else {
                $.messager.show({    // show error message
                    title: 'Error',
                    msg: data.errorMsg
                });
            }
        },
        error(d, e) {
            $.messager.show({    // show error message
                title: 'Error',
                msg: d.responseJSON.errorMsg
            });
        }
    });
}


var selectNodeText;
var selectNode;

function onDblClick(node) {
    console.log('------beginEdit----');
    selectNodeText = node.text;
    $(this).tree('beginEdit', node.target);
}


function getRowData(entityName, row) {
    var tmp;
    var obj = {}
    obj[entityName] = {};
    for (i in row) {
        tmp = row[i];
        obj[i] = tmp;
    }
    return obj;
}

function operWidthData(oper) {
    var jsonParam = {id: selectNode.id};
    var entityName = 'sysconfig';
    $.ajax({
        url: '/dflSystem/dflSystemConfig/view',
        type: "get",
        cache: false,
        async: false,
        dataType: "json",
        data: jsonParam,
        error: function () {
            alert("Error loading " + url);
        },
        success: function (ret) {
            console.log(ret);
            if (!ret.success) {
                $.messager.show({    // show error message
                    title: 'Error',
                    msg: data.errorMsg
                });
                return;
            }
            var data = ret.data;
            if (oper == 'edit') {
                var obj = getRowData(entityName, data);
                $('#fm').form('load', obj);
                $('#dlg').dialog('open').dialog('setTitle', 'Edit Sysconfig');

            } else if (oper == 'add') {
                var obj = getRowData(entityName, data);
                obj.name = '';
                obj.code = '';
                obj.parentId = obj.id;
                obj.id = '';
                $('#fm').form('load', obj);
                $('#dlg').dialog('open').dialog('setTitle', 'Add Sysconfig');
            }
        }
    });
}

function append() {
    var node = $('#menuTree').tree('getSelected');
    selectNode = node;
    onAdd();
}

function editit() {
    var node = $('#menuTree').tree('getSelected');
    selectNode = node;
    onEdit();
}

function removeit() {
    var node = $('#menuTree').tree('getSelected');
    selectNode = node;
    onDestroy();

}

function collapse() {
    var node = $('#menuTree').tree('getSelected');
    $('#menuTree').tree('collapse', node.target);
}

function expand() {
    var node = $('#menuTree').tree('getSelected');
    $('#menuTree').tree('expand', node.target);
}

function onExpandAll() {
    $('#menuTree').tree('expandAll');
}

function onCollapseAll() {
    $('#menuTree').tree('collapseAll');
}

var operType;

function onAdd() {
    if (!selectNode) {
        alert('Please select a parent node to add!');
        return;
    }

    var confType = selectNode.confType;
    //console.log(confType);
    if (!confType) {
        confType = $('#query_confType').combobox('getValue');
    }

    //console.log(confType);
    if (!confType || '' == confType) {
        alert('根节点添加参数，需要先选配置参数类型!');
        return;
    }

    $('#parentName').html(selectNode.text);
    operType = 'add';
    operWidthData(operType);
    $('#sysconfig_confType').val(confType);
}

function onEdit() {
    if (!selectNode) {
        alert('Please select a node to edit!');
        return;
    }
    //console.log(selectNode);
    var parentNode = $('#menuTree').tree('getParent', selectNode.target);
    if (parentNode) {
        $('#parentName').html(parentNode.text);
    }
    operType = 'edit';
    operWidthData(operType);

}

function onSave() {
    var url = '/dflSystem/dflSystemConfig/save';
    var jsonParam = $('#fm').serializeJson();
    if ($('#fm').form('validate')) {
        $.ajax({
            method: 'post',
            url: url,
            data: jsonParam,
            headers: app.headers,
            dataType: 'json',
            success: function (result) {
                if (result.success) {
                    jsonParam.id = result.data;
                    //console.log(result);
                    //console.log(jsonParam);
                    //console.log(jsonParam.id);
                    updateNode(selectNode, jsonParam);//更新节点
                    $('#dlg').dialog('close');        // close the dialog
                } else {
                    $.messager.show({
                        title: '错误消息:' + result.resultCode,
                        msg: result.errorMsg
                    });
                }
            },
            error: function (d, e) {
                $.messager.show({
                    title: e,
                    msg: d.responseJSON.errorMsg,
                    timeout: 3000,
                    showType: 'slide'
                });
            }
        });
    }
}

function onDestroy() {
    if (selectNode) {
        $.messager.confirm('Confirm', 'Are you sure you want to destroy this data?', function (r) {
            if (r) {
                $.ajax({
                    type: 'post',
                    data: {id: selectNode.id},
                    headers: app.headers,
                    dataType: 'json',
                    url: '/dflSystem/dflSystemConfig/delete',
                    success: function (data, textStatus, jqXHR) {
                        if (data.success) {
                            $('#menuTree').tree('remove', selectNode.target);
                        } else {
                            $.messager.show({    // show error message
                                title: 'Error',
                                msg: data.errorMsg
                            });
                        }
                    },
                    error(d, e) {
                        $.messager.show({    // show error message
                            title: 'Error',
                            msg: d.responseJSON.errorMsg
                        });
                    }
                });
            }
        });
    }
}

dict_tf_conf_type = [
    {id: '1', name: '系统参数', color: 'red'},
    {id: '2', name: 'All product param', color: 'pink'},
    {id: '3', name: 'product param', color: 'pink'},
    {id: '4', name: 'User config', color: 'green'},
    {id: '5', name: '客户参数配置', color: 'orange'}
];

function formatter(node) {
    var s = node.text;


    var color = 'black';
    node.confType = 1;
    var data = getDict(dict_tf_conf_type, node.confType);
    if (data) {
        color = data.color;
    }

    s = '<font color="' + color + '">' + node.text + '</font>';
    return s;
}

function formatterConfType(value) {
    //console.log(value);
    var color = 'black';
    var data = getDict(dict_tf_conf_type, value.id);
    if (data) {
        color = data.color;
    }
    return '<font color="' + color + '">' + value.name + '</font>';

}


function onClick(node) {
    selectNode = node;
}

function onAfterEdit(node) {
    //console.log('------onAfterEdit----');			
    //console.log(selectNodeText);
    //console.log(node);

}

function updateNode(node, obj) {
    if (operType == 'add') {
        $('#menuTree').tree('append', {
            parent: (node ? node.target : null),
            data: [{
                target: node.target,
                id: obj.id,
                parentId: obj.parentId,
                text: obj.name,
                color: obj.color,
                css: obj.css,
                url: obj.url
            }]
        });
    } else if (operType == 'edit') {
        $('#menuTree').tree('update', {
            target: node.target,
            text: obj.name,
            color: obj.color,
            css: obj.css,
            url: obj.url
        });
    }

}

function onBeforeDrop(target, source, point) {
    var targetId = $('#menuTree').tree('getNode', target).id;
    //console.log('---onBeforeDrop-id='+source.id+' oldParentId='+source.parentId+' newParentId='+targetId);
    var isSuccess = true;
    $.ajax({
        type: "post",
        url: '/dflSystem/dflSystemConfig/updateRel',
        data: {id: source.id, oldParentId: source.parentId, newParentId: targetId},
        async: false,
        dataType: "json",
        success: function (result) {
            if (result.success) {
                //$('#menuTree').tree('remove', selectNode.target);
            } else {
                $.messager.show({    // show error message
                    title: 'Error',
                    msg: result.msgBody
                });
                isSuccess = false;
            }
        }
    });
    console.log('---onBeforeDrop--isSuccess=' + isSuccess);
}

/**
 *target：DOM 对象，拖放的目标节点。
 source：源节点。
 point：表示拖放操作，可能是值是： 'append'、'top' 或 'bottom'。
 *
 */
function onDrop(target, source, point) {
//			var targetId = $('#menuTree').tree('getNode', target).id;
//			console.log('----id='+source.id+' oldParentId='+source.parentId+' newParentId='+targetId);
//			$.post('/dflSystem/dflSystemConfig/updateRel',{id:source.id, oldParentId:source.parentId, newParentId:targetId},function(result){
//                            if (result.success){
//                            	//$('#menuTree').tree('remove', selectNode.target);
//                            } else {
//                                $.messager.show({    // show error message
//                                    title: 'Error',
//                                    msg: result.msgBody
//                                });
//                            }
//                        },'json');
}

function listenerName(ex) {
    if (ex.keyCode == 13) {
        doSearchTree();
    }
}

$('#query_name').keydown(listenerName);

function onBeforeExpand(node, param) {
    var confType = $('#query_confType').combobox('getValue');
    var url = "/dflSystem/dflSystemConfig/findSysconfigByParentId?parentId=" + node.id;
    if (confType) {
        url += '&confType=' + confType;
    }
    $('#menuTree').tree('options').url = url;
}

function openRoot() {
    var rootNode = $("#menuTree").tree('getRoot');
    // console.log(rootNode);
    //$('#menuTree').tree('expand', rootNode);
    $('#menuTree').tree('expandAll');
}
		
       