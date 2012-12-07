


/*
	This function switches the style class name for one element represented
	by currentIndex.
	It changes also all styles class name to the initial value.
*/
function changeStyle(numberElement,currentIndex,attributName,oldClass,newClass){

		for(i=1;i<=numberElement;i++){
			if(i!=currentIndex){
				document.getElementById(attributName+i).className=oldClass;
			}else{
				document.getElementById(attributName+i).className=newClass;
			}
		}
		return false;
}



