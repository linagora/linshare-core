/* We need to create a javascript Date object with the day, month and year integer values */
function getValNewDate(dateDay, dateMonth, dateYear) {
	var newDate = null;
	if ((dateDay!=0) && (dateMonth!=0) && (dateYear!=0)) {
		newDate = new Date(dateYear, dateMonth, dateDay);
	}
	return newDate;
}
