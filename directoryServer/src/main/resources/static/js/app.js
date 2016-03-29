(function () {

    var app = angular.module('directoryServer', ['ui.bootstrap', 'ngRoute'])
        .filter('organizationType', organizationType)
        .filter('getByProperty', getByProperty)
        .directive('toNumber', toNumber)
        .service('organizationService', organizationService)
        .controller('AccountController', AccountController)
        .controller('DirectoryController', DirectoryController)
        .controller("NavController", NavController)
        .controller("SettingsController", SettingsController)
        .controller("MyOrgController", MyOrgController)
        .controller("OrgController", OrgController)
        .config(config);


    function config($routeProvider) {
        $routeProvider
            .when("/directory", {
                templateUrl: "organizations",
                controller: "DirectoryController",
                controllerAs: 'orgCtrl'
            }).when("/settings", {
                templateUrl: "settings",
                controller: "SettingsController",
                controllerAs: "settings"
            }).
            when("/organization/:org_id", {
                templateUrl: "organization-details",
                controller: "OrgController",
                controllerAs: "orgCtrl",
                resolve: {
                    org: ['$route', 'organizationService', function ($route, organizationService) {
                        return organizationService.find($route.current.params.org_id);
                    }]
                }
            }).
            otherwise({
                redirectTo: 'home'
            });
    }


    function MyOrgController() {
        var self = this;

    }

    function SettingsController() {
        var self = this;

    }

    OrgController.$inject = [ '$routeParams', 'organizationService', 'org'];

    function OrgController($routeParams, organizationService, org) {
        var self = this;

        self.org = org[0];  //should be an array with a single element
        self.editOrg = editOrg;
        self.showOrgForm = showOrgForm;
        self.saveOrg = saveOrg;

        function activate() {

            organizationService.find($routeParams.org_id).then(function(data){
                self.org = data;
            });
        }

        function editOrg() {
            self.org['editing'] = true;
        };

        function showOrgForm() {
            return self.org.hasOwnProperty('editing') && self.org.editing === true;
        };

        function saveOrg() {

            delete self.org.editing;

            organizationService.updateOrg(self.org).then(function(data){
                console.log("Successfully update org.");
            });


        };

    }


    NavController.$inject = ['$location'];

    function NavController($location) {
        var self = this;

        self.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }

    DirectoryController.$inject = ['$log', 'organizationService'];

    function DirectoryController($log, organizationService) {

        var self = this;

        self.organizations = [];


        activate();

        function activate() {
            return getOrganizations().then(function() {

                console.log('Activated Organizations View');
            });
        }

        function getOrganizations() {
            return organizationService.getOrganizations().then(function(data){
                self.organizations = data;
            });
        }

        self.selectedOrganization = {};

        self.viewOrg = function (org) {
            self.selectedOrganization = org;
            $log.info(org);
        };

        self.createOrg = function () {

            var organization = {
                name: '',
                type: 1,
                street: '',
                city: '',
                state: '',
                zip: '',
                telephone: '',
                website: 'http://',
                editing: true
            };
            self.organizations.push(organization);

        };


        self.searchInput = '';

        self.findOrganizations = function () {
            organizationService.getByName(self.searchInput).then(function(data){
                self.organizations = data;
            });
        };

        self.removeOrgFromModel = function (org) {
            var index = self.organizations.indexOf(org);
            if (index > -1) {
                self.organizations.splice(index, 1);
            }
        };


        self.deleteOrg = function (org) {

            //If it's an existing org we need to delete it on the server
            if (org.hasOwnProperty('id')) {

                organizationService.deleteOrg(org).then(function(data){
                    self.removeOrgFromModel(org);
                });

            }
            else {
                //it's a new organization that hasn't been persisted.
                self.removeOrgFromModel(org);

            }
            console.log(org);
        };

        self.saveOrg = function (org) {

            $log.info(self.selectedOrganization);
            delete org.editing;

            if (org.hasOwnProperty('id')) {
                //update

                organizationService.updateOrg(org).then(function(data){
                    $log.info("Successfully update org.");
                });

            }
            else {
                //create
                organizationService.createOrg(org).then(function(data){
                    $log.info("Successfully created org with id " + data.id);
                });

            }
        };

        self.editOrg = function (org) {
            org['editing'] = true;
            console.log(org);
        };


        self.showOrgForm = function (org) {
            return org.hasOwnProperty('editing') && org.editing === true;
        };

    };

    AccountController.$inject = ['$http', '$location', '$window'];

    function AccountController($http, $location, $window) {
        var self = this;

        self.myAccount = function () {
            console.log($window.activeUser);
        };
    };

    organizationService.$inject = ['$http', '$q', '$cacheFactory', '$filter' ];

    function organizationService ($http, $q, $cacheFactory, $filter) {


        var service = {
            getOrganizations: getOrganizations,
            getById: getById,
            getByName: getByName,
            deleteOrg: deleteOrg,
            updateOrg: updateOrg,
            createOrg: createOrg,
            find: find
        };

        return service;

        function deleteOrg(org) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/organizations/' + org.id).success(function (data) {

                removeOrganization(org);

                deferred.resolve(org);
            }).error(function(){
                deferred.reject("An error occured while deleting an organization.");
            });

            return deferred.promise;
        }

        function updateOrg(org) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id, org).success(function (data) {
                deferred.resolve(org);
            }).error(function(){
                deferred.reject("An error occured while updating an organization.");
            });

            return deferred.promise;
        }


        function createOrg(org) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/organizations/', org).success(function (data) {
                angular.extend(org, data);
                deferred.resolve(org);
            }).error(function(){
                deferred.reject("An error occured while updating an organization.");
            });

            return deferred.promise;
        }


        function getOrganizations() {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                cache: false
            }).success(function (data) {
                organizations = data;
                deferred.resolve(organizations);
            }).error(function(){
                deferred.reject("An error occured while fetching organizations.");
            });

            return deferred.promise;
        };

        function getById(id) {
            var org = $filter('getByProperty')('id', id,  organizations);

            if (org == null) {
                org = find(id);
            }

            return org;
        };

        function find(id) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations/' + id, {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(){
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }

        function getByName(name) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {'name': name},
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(){
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }

    }


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

    function organizationType() {
        return function (input) {

            var type = '';

            switch (input) {
                case 0:
                    type = "System";
                    break;
                case 1:
                    type = "Institution";
                    break;
                case 2:
                    type = "Service Provider";
                    break;
                default:
                    type = "Unknown"
            }

            return type;
        };
    }


})();
