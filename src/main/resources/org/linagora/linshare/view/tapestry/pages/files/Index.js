function processUploadCompleteEvent(message) {
	setTimeout(submitShare, 200);
}
function submitShare() {
	location.reload(true);
}