(function(global) {
    "use strict";

    function getFileSize(id, opt) {
        var input = document.getElementById(id);

        input.nextSibling.innerHTML = filesize(input.value, opt);
	setTimeout(function() { getFileSize(id, opt); }, 200);
    };
    global.getFileSize = getFileSize;
})(this);
