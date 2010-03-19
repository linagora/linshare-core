function enableCustomMessage() {

    $('customMessageBlock').style.display = "block";
    $('askCustomMessageBlock').style.display = "none";
}
function toogleAllowedContacts() {
	if ($('allowedContactsBlock').style.display=="none") {
		$('allowedContactsBlock').style.display = "block";
	}
	else {
		$('allowedContactsBlock').style.display = "none";
	}
}
function initAllowedContacts(restricted) {
	if (restricted==true) {
		$('allowedContactsBlock').style.display = "block";
	}
	else {
		$('allowedContactsBlock').style.display = "none";
	}
}