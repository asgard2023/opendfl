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


function doSearchReload() {
    var jsonParam = $('#search-form').serializeJson();
    //status为all时处理
    value_all2empty(jsonParam);
    $('#dg').datagrid('reload', jsonParam);
}

function doSearchTree() {
    $.post('/basedata/model/getModelByName', {name: $("#query_name").val()}
        , function (result) {
            if (result.success) {
                var data = result.dataList;
                var id;
                for (var i = 0; i < data.length; i++) {
                    id = data[i].id;
                    //console.log($('#menuTree'));
                    var findNode = $('#menuTree').tree('find', id); //获取当前节点
                    console.log('--find id=' + id + ' findNode=' + findNode);
                    if (findNode) {
                        $("#menuTree").tree("expandTo", findNode.target);
                        $("#menuTree").tree("select", findNode.target); //定位当前节点
                    }
                }
            } else {
                $.messager.show({    // show error message
                    title: 'Error',
                    msg: result.msgBody
                });
            }
        }, 'json');
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
    var jsonParam = {cid: selectNode.id};
    var entityName = 'model';
    $.ajax({
        url: '/basedata/model/view',
        type: "get",
        cache: false,
        async: false,
        dataType: "json",
        data: jsonParam,
        error: function () {
            alert("Error loading " + url);
        },
        success: function (ret) {
            //console.log(ret);
            if (ret.flag != 0) {
                alert(ret.msgCode + ':' + ret.msgBody);
                return;
            }
            var dataList = ret.dataList;
            var data = dataList[0];

            if (oper == 'edit') {
                var obj = getRowData(entityName, data);
                $('#fm').form('load', obj);
                $('#dlg').dialog('open').dialog('setTitle', 'Edit Model');

            } else if (oper == 'add') {
                var obj = getRowData(entityName, data);
                obj.name = '';
                obj.code = '';
                obj.parentId = obj.id;
                obj.id = '';
                $('#fm').form('load', obj);
                $('#dlg').dialog('open').dialog('setTitle', 'Add Model');
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

    $('#parentName').html(selectNode.text);
    operType = 'add';
    operWidthData(operType);
}

function onEdit() {
    if (!selectNode) {
        alert('Please select a node to edit!');
        return;
    }
    if (!selectNode.color) {
        alert('Can not change package or className');
        return;
    }
    //console.log(selectNode);
    var parentNode = $('#menuTree').tree('getParent', selectNode.target);
    $('#parentName').html(parentNode.text);
    operType = 'edit';
    operWidthData(operType);

}

function onSave() {
    console.log('-----onSave--');
    var jsonParam = $('#fm').serializeJson();
    $('#fm').form('submit', {
        url: '/basedata/model/save',
        onSubmit: function () {
            return $(this).form('validate');
        },
        success: function (result) {
            var result = eval('(' + result + ')');
            if (!result.success) {
                $.messager.show({
                    title: result.msgCode,
                    msg: result.msgBody
                });
            } else {
                jsonParam.id = result.dataList[0];
                updateNode(selectNode, jsonParam);//更新节点
                $('#dlg').dialog('close');        // close the dialog
            }
        }
    });
}

function onDestroy() {
    if (selectNode) {
        $.messager.confirm('Confirm', 'Are you sure you want to destroy this data?', function (r) {
            if (r) {
                $.post('/basedata/model/delete', {id: selectNode.id}, function (result) {
                    if (result.success) {
                        $('#menuTree').tree('remove', selectNode.target);
                    } else {
                        $.messager.show({    // show error message
                            title: 'Error',
                            msg: result.msgBody
                        });
                    }
                }, 'json');
            }
        });
    }
}

function formatter(node) {
    var s = node.text;
    if (node.color) {
        s = '<font color="' + node.color + '">' + node.text + '</font>';
    }
    return s;
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

/**
 *target：DOM 对象，拖放的目标节点。
 source：源节点。
 point：表示拖放操作，可能是值是： 'append'、'top' 或 'bottom'。
 *
 */
function onDrop(target, source, point) {
    var targetId = $('#menuTree').tree('getNode', target).id;
    console.log('----id=' + source.id + ' oldParentId=' + source.parentId + ' newParentId=' + targetId);
    $.post('/basedata/model/updateRel', {
        cid: source.id,
        oldParentId: source.parentId,
        newParentId: targetId
    }, function (result) {
        if (result.success) {
            //$('#menuTree').tree('remove', selectNode.target);
        } else {
            $.messager.show({    // show error message
                title: 'Error',
                msg: result.msgBody
            });
        }
    }, 'json');
}

function listenerName(ex) {
    if (ex.keyCode == 13) {
        doSearch();
    }
}

$('#query_name').keydown(listenerName);
$('#query_code').keydown(listenerName);

function onBeforeExpand(node, param) {
    console.log('-------onBeforeExpand---')
    $('#menuTree').tree('options').url = "/basedata/model/findModelByTree?modelId=" + node.id;
}

function openRoot() {
    var rootNode = $("#menuTree").tree('getRoot');
    // console.log(rootNode);
    //$('#menuTree').tree('expand', rootNode);
    $('#menuTree').tree('expandAll');
}
		
       