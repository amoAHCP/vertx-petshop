angular.module('Petstore', ['doowb.angular-pusher']).

    config(['PusherServiceProvider',
        function (PusherServiceProvider) {
            PusherServiceProvider
                .setToken('60b1ae822a161deb3f0c')
                .setOptions({});
        }
    ]);

function MainController($scope) {
    $scope.name = 'AngularJS';
}

var ItemListController = function($scope, $http, Pusher) {
    $scope.items = [];

    Pusher.subscribe('items', 'updated', function (item) {
        // an item was updated. find it in our list and update it.
        for (var i = 0; i < $scope.items.length; i++) {
            if ($scope.items[i].id === item.id) {
                $scope.items[i] = item;
                break;
            }
        }
    });

    var retrieveItems = function () {
        // get a list of items from the api located at '/api/items'
        console.log('getting items');
        $http.get('/all')
            .success(function (items) {
                $scope.items = items;
            }).error(function (error){
                console.log('error');
            });
    };

    $scope.updateItem = function (item) {
        console.log('updating item');
        $http.post('/update', item);
    };

    // load the items
    retrieveItems();
}