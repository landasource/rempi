define(['jquery'],function($){
   
    var ctrl = function($scope, $rootScope){
        
        $rootScope.$on('clientImage', function(evt, data){
            
            var image = "data:image/jpg;base64," + data.base64Image;
//            $('#clientImage').attr('src', image);
            
            $scope.clientImage = image;
        });
        
    };
    
    return ctrl;
    
});