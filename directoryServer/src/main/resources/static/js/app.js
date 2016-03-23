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
            self.selectedOrganization = {};
            self.tab = 2;
        };

        self.cancelOrgCreate = function() {
            self.tab = 1;
        };

        this.submitOrg = function() {
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
