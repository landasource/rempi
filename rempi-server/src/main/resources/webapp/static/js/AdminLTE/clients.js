
var wsurl = "ws://localhost:1234/ws";
var app = angular.module('rempi-server', [ 'angular-websocket' ]).config(function(WebSocketProvider) {
    WebSocketProvider.prefix('').uri(wsurl);
});

app.controller('ClientsCtrl', function($scope, WebSocket, $http) {

    $scope.clients = {};

    WebSocket.onopen(function() {
        console.log('connection');
    });

    WebSocket.onmessage(function(event) {
    	console.log("WS event happened", event);
    	
        var data = JSON.parse(event.data);
        switch (data.eventType) {

        case "connected":
            $scope.clients[data.clientId] = {};
            break;

        case "disconnected":
            delete $scope.clients[data.clientId];
            break;
        }
    });
    
    $scope.disconnet = function(clientId){
        
        $http.get('/stopClient?clientId=' + clientId);
        
    }

    $http.get('/client/json').success(function(data, status, headers, config) {
        $scope.clients = data;
    }).error(function(data, status, headers, config) {
        // log error
    });

    $scope.capture = function(clientId, evt) {

        var btn = evt.target;

        var img = $('<img>').attr('src', '/rempi-server/client/capture?clientId=' + clientId).load(function() {
            $(btn).next('div').html(this);
        });
    }

});
