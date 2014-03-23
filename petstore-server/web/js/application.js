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

    $scope.orderId = "";

    var itemWS = new WebSocket("ws://localhost:8080/all");
    var orderWS = new WebSocket("ws://localhost:9090/placeOrder");

    itemWS.onopen = function () {
        console.log("item WS open");
    }

    orderWS.onopen = function () {
        console.log("order WS open");
    }


    orderWS.onmessage = function (msg) {
        console.log(msg);
        $scope.toggleDialog('#waitModal', false);
        $scope.orderId = msg.data;
        $scope.$apply();
    }


    itemWS.onmessage = function (msg) {
        $scope.$apply(function () {
            var container = JSON.parse(msg.data);
            if (container.state == "UPDATE") {
                container.products.forEach(function (element) {
                    $scope.pets.forEach(function (pet) {
                        if (pet.id == element.id) {
                            // altes Element löschen
                            $scope.pets.splice($scope.pets.indexOf(pet), 1);
                        }
                    });
                    // neues Element hinzufügen
                    $scope.pets.push(element);
                });
            } else {
                $scope.pets = container.products;
            }
        });
    }


    $scope.addToBasketAndClose = function (item) {
        $scope.addToBasket(item);
        $scope.hideDetails(item);

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
    $scope.newOrder = function () {
        location.reload();
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
        $scope.toggleDialog('#customerModal', false);
        orderWS.send(JSON.stringify($scope.order));
        $scope.toggleDialog('#waitModal', true);
    }

    $scope.showCheckout = function () {
        $scope.toggleDialog('#customerModal', true);
    }

    $scope.showDetails = function (item) {
        $scope.toggleDialog('#' + item.id, true);
    }

    $scope.hideDetails = function (item) {
        $scope.toggleDialog('#' + item.id, false);
    }

    $scope.toggleDialog = function (dialogId, show) {
        $(dialogId).modal(show ? 'show' : 'hide');
    }

    $scope.total = function () {
        var amount = 0;
        $scope.order.basket.basketItems.forEach(function (item) {
            amount += item.amount * item.product.price;
        });

        $scope.amount = amount;
        return $scope.amount;
    }

    //    OBJECTS

    function BasketItem(product) {
        this.product = product;
        this.amount = 1;
    }
}