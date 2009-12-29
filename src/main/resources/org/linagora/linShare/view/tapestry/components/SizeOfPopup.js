function getHeightForPopup() {
	var height = 0;
	if (typeof (window.innerHeight) == 'number') {//!IE
		height = window.innerHeight;
	} else if (document.documentElement && document.documentElement.clientHeight) {//>=IE 6
		height = document.documentElement.clientHeight;
	} else if (document.body && document.body.clientHeight) {//IE 4
		height = document.body.clientHeight;
	}
	return Math.round(height * 0.8);
}


