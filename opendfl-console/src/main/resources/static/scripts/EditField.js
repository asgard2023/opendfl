function EditField() {
	this.configure = function(id, eleId) {
		this.id = id;
		this.eleId="editField_id";
		if(eleId){
			this.eleId=eleId;
		}

		this.createElements(); // 动态生成编辑输入框
		this.toScan(); // 初始化动态生成的输入框和按钮、待编辑的DOM元素的display属性
		this.addEvents(); // 给相关的DOM元素添加事件监听器
	}
	
	/**
	 * 用于添加事件的通用函数
	 * 
	 * @param {}
	 *            elem
	 * @param {}
	 *            type
	 * @param {}
	 *            fn
	 * @return {}
	 */
	this.events = function(elem, type, fn) {
		if (elem.attachEvent) {
			elem.attachEvent("on" + type, fn);
		} else if (elem.addEventListener) {
			elem.addEventListener(type, fn, false);
		} else {
			elem["on" + type] = fn;
		}
		return elem;
	}
	/**
	 * 添加事件
	 */
	this.addEvents = function() {
		var that = this;
		this.events(this.btnSubmit, "click", function() {
					that.save();
				});
		this.events(this.btnCancel, "click", function() {
					that.cancel();
				});
		this.events(document.getElementById(this.id), "click", function() {
					that.toEdit();
				});
	}
	
	/**
	 * 将动态生成的输入框和按钮插入到待编辑元素的后面
	 * 
	 * @param {}
	 *            newNode
	 * @param {}
	 *            referenceNode
	 */
	this.insertAfter = function(newNode, referenceNode) {
		if (referenceNode.nextSibling) {
			referenceNode.parentNode.insertBefore(newNode,
					referenceNode.nextSibling);
		} else {
			var p = referenceNode.parentNode || document.body;
			p.appendChild(newNode);
		}
	}
	
	/**
	 * 动态生成输入框和按钮
	 */
	this.createElements = function() {
		this.divContainer = document.createElement("span");
		this.divContainer.id=this.eleId;
		// this.parentElement.appendChild(this.divContainer);
		this.insertAfter(this.divContainer, document.getElementById(this.id));

		this.input = document.createElement("input");
		this.input.type = "text";
		//this.input.size="30";
		this.input.value = document.getElementById(this.id).innerHTML; // 初始化值
		this.divContainer.appendChild(this.input);

		this.btnSubmit = document.createElement("input");
		this.btnSubmit.value = "确认";
		this.btnSubmit.type = "button";
		this.divContainer.appendChild(this.btnSubmit);

		this.btnCancel = document.createElement("input");
		this.btnCancel.type = "button";
		this.btnCancel.value = "取消";
		this.divContainer.appendChild(this.btnCancel);

	}
	
	/**
	 * 转换成编辑状态
	 */
	this.toEdit = function() {
		this.divContainer.style.display = "block";
		document.getElementById(this.id).style.display = "none";
		this.setValue();
	}
	
	/**
	 * 转换成浏览状态
	 */
	this.toScan = function() { // 
		//document.getElementById(this.id).style.display = "block";		
		this.divContainer.style.display = "none";
		//$("#"+this.eleId).remove();
		$("#"+this.id).removeAttr("style");
	}
	
	/**
	 * 获取输入框的值
	 * 
	 * @return {}
	 */
	this.getValue = function() {
		return this.input.value;
	}
	
	/**
	 * 设置输入框的值
	 */
	this.setValue = function() {
		this.input.value = document.getElementById(this.id).innerHTML;
	}
	
	/**
	 * 全部替换
	 * @param {} str
	 * @return {}
	 */
	this.replaceAll=function(str, regxp, replaceStr){
		var re=new RegExp(regxp,'g');//创建含变量的正则表达式   	
		return str.replace(re,replaceStr);//执行替换
	}
	
	/**
	 * 保存编辑结果
	 */
	this.save = function() {
		var v=this.getValue();
		if(v.indexOf("　")>=0){
			var tmp = this.replaceAll(v, "　", "");
			tmp = this.replaceAll(tmp, " ", "");
			if(tmp!=''){
				v=tmp;
			}
		}
		if(v==''){
			v="　　";
		}
		document.getElementById(this.id).innerHTML = v;
		this.toScan();
	}
	
	/**
	 * 取消当前的编辑
	 */
	this.cancel = function() {
		this.toScan();		
	}
	/**
	 * 取当前id
	 * @return {}
	 */
	this.getId=function(){
		return this.id;
	}
}

var tt_editField = new EditField();
function fieldChange(e){
	if(e.id==''){
		alert('fieldChange---No define id!');
		return;
	}
	$("#editField_id").remove();
	if(tt_editField.getId()!=null){
		tt_editField.cancel();
	}
	tt_editField.configure(e.id);	
}
