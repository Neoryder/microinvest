﻿(function () {
    'use strict';

    var app = angular
        .module('app', ['ui.router', 'ngMessages', 'ngStorage', 'ngMockE2E'])
        .config(config)
        .run(run);

    function config($stateProvider, $urlRouterProvider) {
        // default route
        $urlRouterProvider.otherwise("/");

        // app routes
        $stateProvider
            .state('home', {
                url: '/',
                templateUrl:  'views/order/list.html',
                controller: 'OrderListCtrl',
                controllerAs: 'vm'
            })
            .state('login', {
                url: '/login',
                templateUrl: 'login/index.view.html',
                controller: 'Login.IndexController',
                controllerAs: 'vm'
            })
            .state('orderlist', {
                url: '/orderz/list',
                templateUrl:  'views/order/list.html',
                controller: 'OrderListCtrl'
            })
            .state('ordercreate', {
                url: '/orderz/create',
                templateUrl:  'views/order/create.html',
                controller: 'OrderCreateCtrl'
            })
            .state('traderconsole', {
                url: '/trader/view',
                templateUrl:  'views/trader/view.html',
                controller: 'TraderViewCtrl'
            })
            .state('orderfill', {
                url: '/orderz/fill',
                templateUrl:  'views/order/fill.html',
                controller: 'OrderFillCtrl'
            });
    }

    function run($rootScope, $http, $location, $localStorage, $httpBackend, $timeout) {
        
        $httpBackend.whenGET(/\.html$/).passThrough();
        $httpBackend.whenGET('/api/v1/orderz').passThrough();
        $httpBackend.whenGET('/api/v1/orderzSummary').passThrough();
        $httpBackend.whenPOST('/api/v1/orderz').passThrough();
        $httpBackend.whenPOST('/api/v1/orderzFill').passThrough();

        // keep user logged in after page refresh
        if ($localStorage.currentUser) {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.currentUser.token;
        }

        // redirect to login page if not logged in and trying to access a restricted page
        $rootScope.$on('$locationChangeStart', function (event, ngMessagesitxt, current) {
            var publicPages = ['/login'];
            var restrictedPage = publicPages.indexOf($location.path()) === -1;
            if (restrictedPage && !$localStorage.currentUser) {
                $location.path('/login');
            }
        });
    }
    
    app.controller('OrderCreateCtrl', function ($scope, $http, $location) {
        $scope.order = {
            done: false,
            type: 'buy',
            stock: 'MEG',
            price: '4.53',
            cash: '77.00',
            quantity: '',
            total: '',
            buyingPower: '77.00'
        };
        
        $scope.$watch('order.total', function(newVal, oldVal) {
            if($scope.order.type == 'buy') {
                $scope.order.quantity = newVal/$scope.order.price/1.005;
            }
        });
        
        $scope.$watch('order.quantity', function(newVal, oldVal) {
            if($scope.order.type == 'sell') {
                $scope.order.total = newVal*$scope.order.price*0.99;
            }
        });
        
        $scope.changeTransType = function (type) {
            $scope.order.quantity = '';
            $scope.order.total = '';
        };
        
        $scope.createOrder = function () {
            alert($scope.order);
            console.log($scope.order);
            $http.post('/api/v1/orderz', $scope.order).success(function (data) {
                $location.path('/orderz/list');
            }).error(function (data, status) {
                console.log('Error ' + data);
                alert('Error ' + data);
            })
        }
    });
    
    app.controller('OrderListCtrl', function ($scope, $http) {
        $http.get('/api/v1/orderz').success(function (data) {
            $scope.trans = data;
        }).error(function (data, status) {
            console.log('Error ' + data);
        });
        
        $scope.todoStatusChanged = function (orderz) {
            console.log(orderz);
            $http.put('/api/v1/orderz/' + orderz.id, orderz).success(function (data) {
                console.log('status changed');
            }).error(function (data, status) {
                console.log('Error ' + data);
            })
        }
    });

    app.controller('TraderViewCtrl', function ($scope, $http, $location, $timeout, $state) {

        $http.get('/api/v1/orderzSummary').success(function (data) {
            $scope.trans = data;
        }).error(function (data, status) {
            console.log('Error ' + data);
            console.log('status ' + status);
        });

        $scope.order = {
            done: false,
            type: 'buy',
            stock: 'MEG',
            price: '4.53',
            cash: '77.00',
            quantity: '',
            total: '',
            buyingPower: '77.00'
        };

        $scope.$watch('order.total', function(newVal, oldVal) {
            if($scope.order.type == 'buy') {
                $scope.order.quantity = newVal/$scope.order.price/1.005;
            }
        });

        $scope.$watch('order.quantity', function(newVal, oldVal) {
            if($scope.order.type == 'sell') {
                $scope.order.total = newVal*$scope.order.price*0.99;
            }
        });

        // Function to replicate setInterval using $timeout service.
        $scope.intervalFunction = function(){
            console.log("$state.is():"+$state);
            //Checks whether the state is traderconsole before firing the update data function
            if($state.is('traderconsole')){
                $timeout(function() {
                    console.log("angol");
                    $scope.updateOrderSummary();
                    $scope.intervalFunction();
                }, 1000)
            }
        };

        // Kick off the interval during load
        $scope.intervalFunction();

        $scope.changeTransType = function (type) {
            $scope.order.quantity = '';
            $scope.order.total = '';
        };

        //Method to call to update the list in Trader View
        $scope.updateOrderSummary = function () {
            console.log("updateOrderSummary");
            $http.get('/api/v1/orderzSummary').success(function (data) {
                $scope.trans = data;
            }).error(function (data, status) {
                console.log('Error ' + data);
                console.log('status ' + status);
            });
        }
    });

    app.controller('OrderFillCtrl', function ($scope, $http, $location) {
        $scope.order = {
            done: false,
            type: 'buy',
            stock: 'MEG',
            price: '4.53',
            cash: '77.00',
            quantity: '',
            total: '',
            buyingPower: '77.00'
        };

        $scope.$watch('order.total', function(newVal, oldVal) {
            if($scope.order.type == 'buy') {
                $scope.order.quantity = newVal/$scope.order.price/1.005;
            }
        });

        $scope.$watch('order.quantity', function(newVal, oldVal) {
            if($scope.order.type == 'sell') {
                $scope.order.total = newVal*$scope.order.price*0.99;
            }
        });

        $scope.changeTransType = function (type) {
            $scope.order.quantity = '';
            $scope.order.total = '';
        };

        $scope.fillOrder = function () {
            //alert($scope.order);
            console.log($scope.order);
            $http.post('/api/v1/orderzFill', $scope.order).success(function (data) {
                $location.path('/trader/view');
            }).error(function (data, status) {
                console.log('Error ' + data);
                console.log('status ' + status);

            })
        }
    });

})();