(function() {

    var app = angular.module('admin', []);
    app.controller('OrganizationController', function () {
        this.organizations = organizationsMockData;
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

    var organizationsMockData = [ {
        name: 'Parchment',
        type: 'Vendor',
        contactName: 'Rajeev Arora',
        address:  '3000 Lava Ridge Ct, Roseville, CA 95661',
        creationDate: 1388123412323,
        hasAdminRole: true
    }, {
        name: 'Butte Community College',
        type: 'Institution',
        contactName: 'James Whetstone',
        address:  '3536 Butte Campus Dr, Oroville, CA 95965',
        creationDate: 1388123412323,
        hasAdminRole: true
    } ];

})();
