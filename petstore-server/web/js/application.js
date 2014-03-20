angular.module('Petstore', [])

var ItemListController = function ($scope) {
    $scope.name = 'AngularJS';
    $scope.pets = [];
    $scope.test = 'asdas';
    $scope.order = {
        id: 3,
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
        basket: {
            basketItems: []
        },
        amount: 0
    }

    var itemWS = new WebSocket("ws://localhost:8080/all");
    var updateItemWS = new WebSocket("ws://localhost:8080/update");
    var orderWS = new WebSocket("ws://localhost:9090/placeOrder");

    itemWS.onopen = function () {
        console.log("item WS open");
    }

    orderWS.onopen = function () {
        console.log("order WS open");
    }

    updateItemWS.onopen = function () {
        console.log("updateItemWS open");
    }

    updateItemWS.onmessage = function (msg) {
        $scope.$apply(function () {
            $scope.pets.push(JSON.parse(msg.data));
        });
    }

    orderWS.onmessage = function (msg) {
        console.log(msg);
        $('#waitModal').modal('hide');
    }


    itemWS.onmessage = function (msg) {
        $scope.$apply(function () {
            $scope.pets = JSON.parse(msg.data).products;
        });
    }
    // adding some items to the basket
    $scope.addToBasket = function (item) {
        var found = false;
        $scope.order.basket.basketItems.forEach(function (basketItem) {
            if (basketItem.product === item) {
                basketItem.amount += 1;
                basketItem.product.amount -= 1;
                found = true;
            }
        });

        if (!found) {
            $scope.pushItem(item);
            item.amount -= 1;
        }

    }
    // remove from Basket ;)
    $scope.removeOneFromBasket = function (basketItem) {
        var idx = $scope.order.basket.basketItems.indexOf(basketItem);
        var item = $scope.order.basket.basketItems[idx];
        if (item.amount == 1) {
            $scope.order.basket.basketItems.splice(idx, 1);
            item.product.amount += 1
        } else {
            item.amount = item.amount - 1;
            item.product.amount += 1
        }
    }

    // remove from Basket ;)
    $scope.addOneToBasket = function (basketItem) {
        var idx = $scope.order.basket.basketItems.indexOf(basketItem);
        var item = $scope.order.basket.basketItems[idx];
        if (item.product.amount > 0) {
            item.amount = item.amount + 1;
            item.product.amount -= 1;
        }
    }

    $scope.removeFromBasket = function (basketItem) {
        var idx = $scope.order.basket.basketItems.indexOf(basketItem);
        $scope.order.basket.basketItems.splice(idx, 1);
        basketItem.product.amount += basketItem.amount;
    }


    $scope.pushItem = function (item) {
        var newBasketItem = new BasketItem(item);
        $scope.order.basket.basketItems.push(newBasketItem);
    }


    $scope.placeOrder = function () {
        orderWS.send(JSON.stringify($scope.order));
        $('#waitModal').modal('show');
    }

    function BasketItem(product) {
        this.product = product;
        this.amount = 1;
    }
}