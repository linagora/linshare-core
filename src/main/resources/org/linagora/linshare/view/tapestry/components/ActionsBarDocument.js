function submitFormWithAction(formName, currentAction)
{
	f = eval("document.forms['" + formName + "']");
	f.action.value=currentAction;
	f.submit();
}
function setFormWithAction(formName, currentAction)
{
	f = eval("document.forms['" + formName + "']");
	f.action.value=currentAction;
}