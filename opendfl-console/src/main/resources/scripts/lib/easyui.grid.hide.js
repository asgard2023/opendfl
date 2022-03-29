/**
 * 
 * @requires jQuery,EasyUI
 * 
 * 为datagrid、treegrid增加表头菜单，用于显示或隐藏列，注意：冻结列不在此菜单中
 */
var createGridHeaderContextMenu = function(e, field) {
	e.preventDefault();
	var grid = $(this);/* grid本身 */
	var headerContextMenu = this.headerContextMenu;/* grid上的列头菜单对象 */
	if (!headerContextMenu) {
		var tmenu = $('<div ></div>').appendTo('body');
		var fields = grid.datagrid('getColumnFields');
		for (var i = 0; i < fields.length; i++) {
			var fildOption = grid.datagrid('getColumnOption', fields[i]);
			if (!fildOption.hidden) {
				$('<div iconCls="icon-ok" field="' + fields[i] + '"/>')
						.html(fildOption.title).appendTo(tmenu);
			} else {
				$('<div iconCls="icon-empty" field="' + fields[i] + '"/>')
						.html(fildOption.title).appendTo(tmenu);
			}
		}
		headerContextMenu = this.headerContextMenu = tmenu.menu({
					onClick : function(item) {
						var field = $(item.target).attr('field');
						if (item.iconCls == 'icon-ok') {
							grid.datagrid('hideColumn', field);
							$(this).menu('setIcon', {
										target : item.target,
										iconCls : 'icon-empty'
									});
						} else {
							grid.datagrid('showColumn', field);
							$(this).menu('setIcon', {
										target : item.target,
										iconCls : 'icon-ok'
									});
						}
					}
				});
	}
	headerContextMenu.menu('show', {
				left : e.pageX,
				top : e.pageY
			});
};
$.fn.datagrid.defaults.onHeaderContextMenu = createGridHeaderContextMenu;