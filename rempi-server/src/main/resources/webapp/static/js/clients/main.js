require.config({
    baseUrl : 'static/js/clients',
    paths : {
        jquery : '../jquery',
        semantic : '../semantic.min',
        angular : '../vendor/angular/angular.min',
        'angular-websocket' : '../vendor/angular-websocket/angular-websocket'
    },
    shim : {
        'angular-websocket' : {
            deps : [ 'angular' ]
        },
        'semantic' : {
            deps : [ 'jquery' ]
        },
        'angular' : {
            exports : 'angular'
        },
        'jquery' : {
            exports : 'jQuery'
        }
    }
});

require([ 'jquery', 'angular', 'ToastCtrl', 'ImageCtrl' ,'angular-websocket', 'semantic' ], function($, angular, ToastCtrl, ImageCtrl) {

    var app = angular.module('rempi-server', [ 'angular-websocket' ]);

    var serverAddress = location.host;
    var wsAddress = 'ws://' + serverAddress + '/ws/rempi';

    app.config(function(WebSocketProvider) {
        WebSocketProvider.prefix('').uri(wsAddress);
    });

    app.filter('empty', function() {
        return function(input) {
            return jQuery.isEmptyObject(input);
        };
    });

    app.controller('ToastCtrl', ToastCtrl);
    app.controller('ImageCtrl', ImageCtrl);

    app.controller('ClientsCtrl', function($scope, WebSocket, $http, $rootScope) {

        $scope.clients = {};

        $scope.filteredClients = function() {
            var cls = {};

            if (!$scope.clientNameFilter)
                return $scope.clients;

            angular.forEach($scope.clients, function(val, key) {
                if (key.indexOf($scope.clientNameFilter) > -1) {
                    cls[key] = val;
                }
            });

            return cls;
        };

        WebSocket.onopen(function() {
            console.log('connection');
        });

        WebSocket.onmessage(function(event) {
            //console.log("WS event happened", event);

            var data = JSON.parse(event.data);
            switch (data.eventType) {

            case "connected":
                $scope.clients[data.clientId] = {};
                break;

            case "disconnected":
                delete $scope.clients[data.clientId];
                break;

            case "error":
                $scope.$emit('clientError', data);
                break;
            case "image":
                $scope.$emit('clientImage', data);
                break;
            default:
                console.log(event);
                break;
            }
        });

        $scope.disconnet = function(clientId) {

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
                $(btn).next('span').html(this);
            });
        };

        $scope.startStream = function(clientId) {
            $http.get('/client/startStream?clientId=' + clientId);
        };

        $scope.stopStream = function(clientId) {
            $http.get('/client/stopStream?clientId=' + clientId);
        };

    });

    app.run(function() {

    });

    $('.ui.dropdown').dropdown();

});