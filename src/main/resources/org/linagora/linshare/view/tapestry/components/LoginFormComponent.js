function submitWithCookie() {
	document.cookie = "linshare_persistent_login="+document.getElementById('login').value+"; path=/";
	if (document.getElementById('login').value.indexOf('@') != -1) {
		document.getElementById('invalidLoginFormat').style.visibility = 'hidden'; 
		return false;
	}
	document.forms["linshareLoginForm"].submit();
}

document.observe("dom:loaded", function() {
	var loginElt = document.getElementById('login');
	
	if (loginElt != null) {
		var ca = document.cookie.split(';');
		var login = "";
		for(var i=0;i < ca.length;i++) {
			var c = ca[i];
			while (c.charAt(0)==' ') {
				c = c.substring(1,c.length);
			}
			if (c.indexOf("linshare_persistent_login=") == 0) {
				login = c.substring("linshare_persistent_login=".length,c.length);
				break;
			}
		}
		loginElt.value=login;
	}
});