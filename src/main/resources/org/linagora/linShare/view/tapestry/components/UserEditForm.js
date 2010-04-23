function toogleAllowedContactsEdit() {
	if ($('allowedContactsEditBlock').style.display=="none") {
		$('allowedContactsEditBlock').style.display = "block";
	}
	else {
		$('allowedContactsEditBlock').style.display = "none";
	}
}
function initAllowedContactsEdit(restricted) {
	if (restricted==true) {
		$('allowedContactsEditBlock').style.display = "block";
	}
	else {
		$('allowedContactsEditBlock').style.display = "none";
	}
}

