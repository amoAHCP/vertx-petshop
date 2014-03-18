angular.module('Petstore', [])

var ItemListController = function ($scope) {
    $scope.name = 'AngularJS';
    $scope.pets = [];
    $scope.test = 'asdas';

    var itemWS = new WebSocket("ws://localhost:8080/all");
    var orderWS = new WebSocket("ws://localhost:8080/update");

    itemWS.onopen = function () {
        console.log("item WS open");
    }

    orderWS.onopen = function () {
        console.log("order WS open");
    }

    itemWS.onmessage = function (msg) {
        $scope.$apply(function () {
            $scope.pets = JSON.parse(msg.data) ;
        });
    }


    $scope.placeOrder = function (item) {
        orderWS.send(JSON.stringify(item));
    };

}