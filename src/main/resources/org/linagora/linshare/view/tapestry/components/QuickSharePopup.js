	var quickShareCurrentElement;
	var quickShareMaxElement;
	
	function setQuickShareMaxElement(maxEl) {
		quickShareMaxElement = maxEl;
	}
	
	function setQuickShareCurrentElement(curEl) {
		quickShareCurrentElement = curEl;
	
	}
	
	function quickShareShowLast() {
		if (quickShareCurrentElement<quickShareMaxElement-1) {
			quickShareCurrentElement++;
			Element.show('file_quickshare-'+quickShareCurrentElement);
			
			
		}
	}

	function quickShareRemoveLast() {
		if (quickShareCurrentElement>0) {
      		Element.hide('file_quickshare-'+quickShareCurrentElement);
	
			var mychild = $A(document.getElementById('file_quickshare-'+quickShareCurrentElement).childNodes);
		
			for(var c=0; c < mychild.length; c++) {
	
	      		if (mychild[c].type == 'file') {
    	  			mychild[c].value='';
	     	 	} 
			
			}
			
			quickShareCurrentElement--;
		}

	}


