function capture(clientId, btn) {

    var img = $('<img>').attr('src', '/rempi-server/client/capture?clientId=' + clientId).load(function() {
        $(btn).next('div').html(this);
    });
}

jQuery(function() {

//    jQuery(".capture-button").each(function() {
//        var btn = this;
//        setInterval(function() {
//
//            var $btn = $(this);
//            if (!$btn.hasClass('btn-danger')) {
//                var clientId = $(this).attr("data-clientid");
//                capture(clientId, this);
//            }
//        }.bind(btn), 200);
//
//    }).click(function() {
//        $(this).toggleClass('btn-danger');
//    });

});

$(function() {

    var wsurl = "ws://localhost:8443/rempi-server";
    var ws = new WebSocket(wsurl);
    ws.onopen = function() {
        // Web Socket is connected, send data using send()
        ws.send("Sending first Message");
        console.log("Message is sent...");
    };
    ws.onmessage = function(evt) {        
        console.log("Message is received...", evt);
    };
    ws.onclose = function(evt) {
        // websocket is closed.
        console.error("Connection is closed..." + evt.code + ":" + evt.reason);
    };

});
