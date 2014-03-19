angular.module('Petstore', [])

var ItemListController = function ($scope) {
    $scope.name = 'AngularJS';
    $scope.pets = [];
    $scope.test = 'asdas';
    $scope.order = {
        customer: {
            firstname: "",
            lastname: "",
            email: "",
            homeAddress: {
                street1: "",
                zipcode: "",
                city: "",
                country: ""
            }
        },
        basket: [],
        amount: 0

    }

    var itemWS = new WebSocket("ws://localhost:8080/all");
    var orderWS = new WebSocket("ws://localhost:9090/placeOrder");
    var updateOrderWS = new WebSocket("ws://localhost:9090/placeOrder");

    itemWS.onopen = function () {
        console.log("item WS open");
    }

    orderWS.onopen = function () {
        console.log("order WS open");
    }

    updateOrderWS.onopen = function () {
        console.log("updateOrder WS open");
    }

    itemWS.onmessage = function (msg) {
        $scope.$apply(function () {
            $scope.pets = JSON.parse(msg.data);
        });
    }

    $scope.addToBasket = function (item) {
        $scope.order.basket.push(item);
    }


    $scope.placeOrder = function () {
        orderWS.send(JSON.stringify($scope.order));
    }

}