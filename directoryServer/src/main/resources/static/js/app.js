(function() {

    var app = angular.module('directoryServer', []).filter('organizationType', function() {
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
    });

    app.directive('toNumber', function() {
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
    });

    app.controller('AccountController', [ '$http', function($http){
        var self = this;

        self.show = function(user) {
           console.log(user);
        };
    }]);

    app.controller('OrganizationController', [ '$http','$log', function ($http, $log) {

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

        self.submitOrg = function() {
           $log.info(self.selectedOrganization);

            if (self.selectedOrganization.hasOwnProperty('id')) {
                //update
            }
            else {
                //create
            }

            self.tab = 1;
        } ;

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

        self.deleteOrg = function(org) {

            //If it's an existing org we need to delete it on the server
            if (org.hasOwnProperty('id')) {

            }
            else {
                //it's a new organization that hasn't been persisted.
                var index = self.organizations.indexOf(org);
                if (index > -1) {
                    self.organizations.splice(index, 1);
                }

            }
            console.log(org);
        };

        self.saveOrg = function(org) {
            delete org.editing;

            console.log(org);
        };

        self.showOrgForm = function(org) {
            return org.hasOwnProperty('editing') && org.editing === true;
        };

        self.tab = 1;



    }]);

    app.controller("NavController", function() {
        this.selectedMenu = 1;
        this.selectMenu = function(menu) {
            this.selectedMenu = menu;
        };
        this.isSelected = function(menu) {
            return this.selectedMenu === menu;
        };
    });


    app.controller("MenuController", function() {
        this.tab = 1;
        this.selectTab = function(tabNum) {
          this.tab = tabNum;
        };
        this.isSelected = function(tabNum) {
            return this.tab === tabNum;
        }
    });

})();
