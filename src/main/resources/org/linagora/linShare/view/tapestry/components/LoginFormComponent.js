function submitWithCookie() {
	document.cookie = "linshare_persistent_login="+document.getElementById('login').value+"; path=/";
	document.forms["linshareLoginForm"].submit();
}

document.observe("dom:loaded", function() {
	var ca = document.cookie.split(';');
	var login = "";
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') {
			c = c.substring(1,c.length);
		}
		if (c.indexOf("linshare_persistent_login=") == 0) {
			login = c.substring("linshare_persistent_login=".length,c.length);
		}
	}
	document.getElementById('login').value=login;
});