function setAllAuthorised(){
		var chkboxelements = $$('input[type="radio"][id^="authorised"]').findAll(function(el)
		{ return true });
		
		for(i=0;i<chkboxelements.length;i++){
			chkboxelements[i].checked=true;
		}	
}
function setAllDenied(){
		var chkboxelements = $$('input[type="radio"][id^="denied"]').findAll(function(el)
		{ return true });
		
		for(i=0;i<chkboxelements.length;i++){
			chkboxelements[i].checked=true;
		}	
}
function setAllWarn(){
		var chkboxelements = $$('input[type="radio"][id^="warn"]').findAll(function(el)
		{ return true });
		
		for(i=0;i<chkboxelements.length;i++){
			chkboxelements[i].checked=true;
		}	
}




