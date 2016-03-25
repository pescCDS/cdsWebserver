(function() {

    var app = angular.module('directoryServer',  ['ui.bootstrap', 'ngRoute'])


    .filter('organizationType', function() {
        return function(input) {

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
    })

    .directive('toNumber', function() {
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function(val) {
                    return parseInt(val, 10);
                });
                ngModel.$formatters.push(function(val) {
                    return '' + val;
                });
            }
        }
    })
     .controller('AccountController', [ '$http', '$location', '$window', function($http, $location, $window){
        var self = this;

        self.myAccount = function() {
           console.log($window.activeUser);
        };
    }])
        .controller('OrganizationController', [ '$http','$log', function ($http, $log) {

        var self = this;

        self.organizations = [];

        $http.get('/services/rest/v1/organizations').success(function(data){
           self.organizations = data;
        });

        self.selectedOrganization = {};

        self.viewOrg = function(org) {
            self.selectedOrganization = org;
            self.tab = 3;
            $log.info(org);
        };

        self.createOrg = function() {

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
            } ;
            self.organizations.push(organization);

        };

        self.cancelOrgCreate = function() {
            self.tab = 1;
        };


        this.isSelected = function(tabNum) {
            return self.tab === tabNum;
        }

        self.searchInput = '';

        self.findOrganizations = function() {

            $http.get('/services/rest/v1/organizations', { params: { name: self.searchInput} } ).success(function(data){
                self.organizations = data;
            });
        };

        self.editOrg = function(org) {
            org['editing'] = true;
            console.log(org);
        };

        self.removeOrgFromModel = function(org) {
            var index = self.organizations.indexOf(org);
            if (index > -1) {
                self.organizations.splice(index, 1);
            }
        } ;

        self.deleteOrg = function(org) {

            //If it's an existing org we need to delete it on the server
            if (org.hasOwnProperty('id')) {
                 $http.delete('/services/rest/v1/organizations/' + org.id).success(function(response){
                     console.log(response);
                     self.removeOrgFromModel(org);
                 });
            }
            else {
                //it's a new organization that hasn't been persisted.
                self.removeOrgFromModel(org);

            }
            console.log(org);
        };

        self.saveOrg = function(org) {

            $log.info(self.selectedOrganization);
            delete org.editing;

            if (org.hasOwnProperty('id')) {
                //update
                $http.put('/services/rest/v1/organizations/' + org.id, org).error(function(response){
                    $log.error(response);
                });
            }
            else {
                //create
                $http.post('/services/rest/v1/organizations/', org).success(function(data){

                    angular.extend(org, data);
                    console.log("Successfully created organization.");

                }).error(function(response){
                    $log.error(response);
                });
            }

            self.tab = 1;



        };

        self.showOrgForm = function(org) {
            return org.hasOwnProperty('editing') && org.editing === true;
        };

        self.tab = 1;



    }]).controller("NavController", [ '$location', function($location){
            var self = this;

            self.isActive = function (viewLocation) {
                return viewLocation === $location.path();
            };
    }])
        .controller("SettingsController", function(){
            var self = this;

        })


        .config(['$routeProvider', function ($routeProvider) {

        $routeProvider
            .when("/directory", {
                templateUrl: "organizations",
                controller: "OrganizationController",
                controllerAs: 'orgCtrl'
            }).when("/settings", {
                templateUrl: "settings",
                controller: "SettingsController",
                controllerAs: "settings"
            }).
            otherwise({
                redirectTo: 'home'
            });
    }]);

})();
