jQuery(document).ready(function(){ 
	jQuery("#sortable").sortable({
	
		placeholder : "ui-state-highlight",
		update : function(event,ui){
			var list = ui.item.parent("ul");
			var pos=0;
			jQuery(this).find("input#tabPos").val("");
			var tmp_tabPos = "";
			jQuery(list.find("li")).each(function(){
				jQuery(this).find("span#position").text(pos);
				tmp_tabPos+= jQuery(this).find("span#domainName").text() + ";";
				pos++;
				
			});
			jQuery("input#tabPos").val(tmp_tabPos);
		}
	});
	jQuery("#sortable").disableSelection();
	
});