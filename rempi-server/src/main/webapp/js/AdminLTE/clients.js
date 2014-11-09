
var wsurl = "ws://localhost:8443/rempi-server";
var app = angular.module('rempi-server', [ 'angular-websocket' ]).config(function(WebSocketProvider) {
    WebSocketProvider.prefix('').uri(wsurl);
});

app.controller('ClientsCtrl', function($scope, WebSocket, $http) {

    $scope.clients = {};

    WebSocket.onopen(function() {
        console.log('connection');
    });

    WebSocket.onmessage(function(event) {
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

    $http.get('/rempi-server/client/json').success(function(data, status, headers, config) {
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
