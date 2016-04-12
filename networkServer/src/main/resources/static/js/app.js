(function () {

    var app = angular.module('networkServer', ['ui.bootstrap', 'ngRoute', 'toaster', 'ngAnimate'])
        .filter('friendlyRoleName', friendlyRoleName)
        .filter('getByProperty', getByProperty)
        .directive('toNumber', toNumber)
        .directive('fileModel', fileModel)
        .service('transactionService', transactionService)
        .service('notificationService', notificationService)
        .service('fileUpload', fileUpload)
        .controller("NavController", NavController)
        .controller("TransactionController", TransactionController)
        .controller("TransferController", TransferController)
        .config(config)
        .run(['transactionService', function(transactionService) {
            transactionService.initialize();
        }]);



    function config($routeProvider) {
        $routeProvider
            .when("/transactions", {
                templateUrl: "transactions",
                controller: "TransactionController",
                controllerAs: 'transactionCtrl'
            })
            .when("/transfers", {
                templateUrl: "transfers",
                controller: "TransferController",
                controllerAs: "transferCtrl"
            })
            .when("/home", {
                templateUrl: "about"
            })
            .otherwise({
                redirectTo: "home"
            });
    }

    TransactionController.$inject = ['transactionService'];
    function TransactionController(transactionService) {
        var self = this;

        self.transactions = [];
        self.status = '';
        self.startDate = '';
        self.stopDate = '';
        self.openStartDatePopup = openStartDatePopup;
        self.openEndDatePopup = openEndDatePopup;

        self.fetchSize = 1;

        self.startDatePopup = {
            opened: false
        };

        self.endDatePopup = {
            opened: false
        };

        self.dateOptions = {
            formatYear: 'yy',
            maxDate: new Date(2020, 5, 22),
            minDate: new Date(),
            startingDay: 1
        };




        //getTransactions();

        function today() {
            self.startDate = new Date();
            self.endDate = new Date();
        }

        function clear() {
            self.startDate = null;
            self.endDate = null;
        }

        function openStartDatePopup() {
            self.startDatePopup.opened = true;
        };

        function openEndDatePopup() {
            self.endDatePopup.opened = true;
        };


        function getTransactions() {
            transactionService.getTransactions().then(function(data){
                self.transactions = data;

            });
        }
    }


    TransferController.$inject = ['fileUpload'];
    function TransferController(fileUpload) {
        var self = this;

        self.transfers = [];

        self.transferFile = transferFile;
        self.documentFormats = getSupportedDocumentFormatsForEndpoint();
        self.documentFormat = ''; //selected document format.
        self.recipientName = '';
        self.senderName = '';
        self.endpointURL = 'http://';
        self.fileToUpload = '';

        function transferFile() {
            console.log("Transfer file.");
            fileUpload.uploadFileToUrl(self.fileToUpload, self.endpointURL);
        }

        function getSupportedDocumentFormatsForEndpoint() {
            //TODO : ajax call to endpont to get supported doc formats.
            return [{ id: 1, name: 'XML', description: 'EXtensible Markup Language.'},
                { id: 2, name: 'PDF', description: 'Adobe Portable Document Format.'},
                { id: 3, name: 'PESCXML', description: 'PESC’s Core Component Naming convention is based on the Core Component naming convention described in the UNCEFACT Core Component Technical Specification. The UNCEFACT Core Component Technical Specification’s naming convention is based on the standards outlined ISO 11179 Part 5 – Naming and Identification Principles for Data Elements. The Core Component Technical Specification expands upon the ISO 11179 naming convention standards to include Core Component Types and Business Information Entities.'},
                { id: 4, name: 'TEXT', description: 'A human-readable format'} ];
        }
    }



    NavController.$inject = ['$location'];

    function NavController($location) {
        var self = this;

        self.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }

    notificationService.$inject = [ 'toaster'] ;

    function notificationService(toaster) {
        var service = {
            success: success,
            error: error,
            ajaxInfo: ajaxInfo
        } ;

        return service;

        function success(text) {
            toaster.pop('success', "Success", text);
        }

        function ajaxInfo(responseObject) {
            toaster.pop('info', responseObject.error, responseObject.message);
        }

        function error(text) {
            toaster.pop('error', "Error", text);
        }
    }


    fileUpload.$inject = [ '$http', 'notificationService'] ;

    function fileUpload($http, notificationService) {
        var service = {
            uploadFileToUrl: uploadFileToUrl
        } ;

        return service;

        function uploadFileToUrl(file, uploadUrl){
            var fd = new FormData();
            fd.append('file', file);
            fd.append('recipientId', 1);
            fd.append('networkServerId', 2);
            fd.append('senderId', 3);
            fd.append('fileFormat', 'PDF');
            fd.append('webServiceUrl', uploadUrl);

            $http.post('/sendFile', fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
            .success(function(data){
                notificationService.success("Successfully uploaded file.");
            })
            .error(function(data){
                notificationService.error("Failed to upload file.");
            });
        }

    }


    transactionService.$inject = [ '$http', '$q', '$cacheFactory', 'notificationService'];

    function transactionService($http, $q, $cacheFactory, notificationService) {
        var service = {
            getTransactions: getTransactions,
            initialize: initialize
        };

        return service;

        function initialize() {
            console.log("TODO: transactionService initialize")
        }


        function getTransactions() {

            var deferred = $q.defer();

            $http.get('/getTransactions', {
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching transactions.");
            });

            return deferred.promise;
        }
    }

    fileModel.$inject = [ '$parse' ];

    function fileModel($parse) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;

                element.bind('change', function(){
                    scope.$apply(function(){
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    };


    function toNumber() {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function (val) {
                    return parseInt(val, 10);
                });
                ngModel.$formatters.push(function (val) {
                    return '' + val;
                });
            }
        }
    }

    function getByProperty() {
        return function(propertyName, propertyValue, collection) {
            var i=0, len=collection.length;
            for (; i<len; i++) {
                if (collection[i][propertyName] == +propertyValue) {
                    return collection[i];
                }
            }
            return null;
        }
    }

    //TODO: refactor roles names for the network server.
    function friendlyRoleName() {
        return function (input) {

            var friendlyName = '';

            switch (input) {
                case 'ROLE_SYSTEM_ADMIN':
                    friendlyName = "System Administrator";
                    break;
                case 'ROLE_ORG_ADMIN':
                    friendlyName = "Organization Administrator";
                    break;
                case 'ROLE_SUPPORT':
                    friendlyName = "Support Technician";
                    break;
                default:
                    friendlyName = "Unknown Role"
            }

            return friendlyName;
        };
    }


})();
