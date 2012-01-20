function onAllowedClick(obj)
{
	changeStatus(obj,getDefaultStatusValue(obj),false);
}

function onForbiddenClick(obj)
{
	changeStatus(obj,false,true);
}


function onMandatoryClick(obj)
{
	changeStatus(obj,true,true);
}


function getStatusField(obj) {
	var elem = obj.id.split('_');
	var identifier = elem[1];
	
	var id = 'status';
	if(identifier != null) {
		id = id + '_'+ identifier;
	}
//	window.alert("id is : " + id);
	return document.getElementById(id);
}

function changeStatus(obj, checked, disabled) {
	var status = getStatusField(obj);
	status.disabled=disabled;
	status.checked=checked;
}

function getDefaultStatusValue(obj) {
	var elem = obj.id.split('_');
	var identifier = elem[1];
	
	var id = 'defaultStatus';
	if(identifier != null) {
		id = id + '_'+ identifier;
	}
	
	var statusStr=document.getElementsByName(id)[0].value;
//	window.alert("default status value is : " + id);
//	window.alert("default status value is : " + document.getElementsByName(id)[0].value);
	if(statusStr == "true") {
		return true;
	}
	return false;
}
