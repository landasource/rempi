define(function(){
    
    var ctrl = function($scope, $rootScope, $timeout){
                
        $scope.messages = {};
        
        $rootScope.$on('clientError', function(evt, data){
           var time = new Date().getTime();
           $scope.messages[time] = {
               title : "Error at " + data.clientId + " (" + data.errorMessage.text + ")",
               description: data.errorMessage.stackTrace,
               type : 'error'
           };
        
           $timeout(function(){
               delete $scope.messages[time];
           }, 8000);
           
           
        });        
        
        $scope.close = function(time) {
          delete $scope.messages[time];  
        };
    };
    
    return ctrl;
});