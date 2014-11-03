function capture(clientId, btn) {
    
    var img = $('<img>').attr('src', '/rempi-server/client/capture?clientId=' + clientId).load(function(){
        $(btn).next('div').html(this);
    });
}

jQuery(function() {

    jQuery(".capture-button").each(function(){
       var btn = this;
        setInterval(function(){
            var clientId = $(this).attr("data-clientid");
            capture(clientId, this);
        }.bind(btn), 200);
        
    });
    
});