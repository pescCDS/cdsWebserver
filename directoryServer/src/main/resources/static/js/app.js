(function () {

    var app = angular.module('directoryServer', ['ui.bootstrap', 'ngRoute', 'toaster', 'ngAnimate'])
        .filter('organizationType', organizationType)
        .filter('friendlyRoleName', friendlyRoleName)
        .filter('getByProperty', getByProperty)
        .directive('toNumber', toNumber)
        .service('notificationService', notificationService)
        .service('organizationService', organizationService)
        .service('userService', userService)
        .service('settingsService', settingsService)
        .controller('DirectoryController', DirectoryController)
        .controller("NavController", NavController)
        .controller("SettingsController", SettingsController)
        .controller("OrgController", OrgController)
        .controller("UserController", UserController)
        .controller("UsersController", UsersController)
        .config(config);


    function config($routeProvider) {
        $routeProvider
            .when("/directory", {
                templateUrl: "organizations",
                controller: "DirectoryController",
                controllerAs: 'orgCtrl'
            })
            .when("/settings", {
                templateUrl: "settings",
                controller: "SettingsController",
                controllerAs: "settingsCtrl"
            })
            .when("/organization/:org_id", {
                templateUrl: "organization-details",
                controller: "OrgController",
                controllerAs: "orgCtrl",
                resolve: {
                    org: ['$route', 'organizationService', function ($route, organizationService) {
                        return organizationService.find($route.current.params.org_id);
                    }]
                }
            })
            .when("/myorg", {
                templateUrl: "organization-details",
                controller: "OrgController",
                controllerAs: "orgCtrl",
                resolve: {
                    org: ['$window', 'organizationService', function ($window, organizationService) {
                        return organizationService.find($window.activeUser.organizationId);
                    }]
                }
            })
            .when("/user/:user_id", {
                templateUrl: "user-details",
                controller: "UserController",
                controllerAs: "userCtrl",
                resolve: {
                    users: ['$route', 'userService', function ($route, userService) {
                        return userService.find($route.current.params.user_id);
                    }]
                }
            })
            .when("/users", {
                templateUrl: "users",
                controller: "UsersController",
                controllerAs: "usersCtrl",
                resolve: {
                    users: ['$window', 'userService', function ($window, userService) {
                        return userService.getUsers($window.activeUser.organizationId);
                    }]
                }
            }).
            otherwise({
                redirectTo: 'home'
            });
    }

    SettingsController.$inject = ['settingsService'];
    function SettingsController(settingsService) {
        var self = this;

        self.deliveryMethods = [];
        self.documentFormats = [];
        self.getDeliveryMethods = getDeliveryMethods;
        self.getDocumentFormats = getDocumentFormats;

        getDeliveryMethods();


        function getDeliveryMethods() {
            settingsService.getDeliveryMethods().then(function(data){
                self.deliveryMethods = data;
            });
        }
        function getDocumentFormats() {
            settingsService.getDocumentFormats().then(function(data){
                self.documentFormats = data;
            });
        }

    }

    UsersController.$inject = [ '$window', 'userService', 'users'];
    function UsersController($window, userService, users) {
        var self = this;
        self.users = users;
        self.roles = $window.roles;
        self.isNewAccount = isNewAccount;
        self.getUsers = userService.getUsers;
        self.create = create;
        self.find = find;
        self.delete = deleteUser;
        self.hasRole = hasRole;
        self.save = save;
        self.edit = edit;
        self.showForm = showForm;
        self.updateRole = updateRole;
        self.removeFromModel = removeFromModel;

        function create() {

            var user = {
                name: '',
                address: '',
                title: '',
                phone: '',
                email: '',
                roles: [],
                username: '',
                password: '',
                organizationId: $window.activeUser.organizationId,
                editing: true
            };
            self.users.push(user);

        };


        self.searchInput = '';

        function find() {
            userService.getByName(self.searchInput,$window.activeUser.organizationId ).then(function(data){
                self.users = data;
            });
        };

        function removeFromModel(user) {
            var index = self.users.indexOf(user);
            if (index > -1) {
                self.users.splice(index, 1);
            }
        };


        function deleteUser(user) {

            //If it's an existing user we need to delete it on the server
            if (user.hasOwnProperty('id')) {

                userService.deleteUser(user).then(function(data){
                    self.removeFromModel(user);
                });

            }
            else {
                //it's a new user that hasn't been persisted.
                self.removeFromModel(user);

            }
        };

        function isNewAccount(user) {
            return ! user.hasOwnProperty('id');
        }

        function save(user) {

            delete user.editing;

            if (user.hasOwnProperty('id')) {
                //update

                userService.updateUser(user).then(function(data){
                    console.log("Successfully update user.");
                });

            }
            else {
                //create
                userService.createUser(user).then(function(data){
                    console.log("Successfully created user with id " + data.id);
                }).catch(function(e){
                    self.edit(user);
                });;

            }
        };

        function edit(user) {
            user['editing'] = true;
        };


        function showForm(user) {
            return user.hasOwnProperty('editing') && user.editing === true;
        };


        function hasRole(user, role) {
            var found = false;

            for (var i=0; i < user.roles.length; i++) {
                if (user.roles[i].id == role.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        function updateRole($event, role, user){

            var checkbox = $event.target;

            if(checkbox.checked === true){
                user.roles.push(role);
            } else {
                // remove item
                for(var i=0 ; i < user.roles.length; i++) {
                    if(user.roles[i].id == role.id){
                        user.roles.splice(i,1);
                    }
                }
            }
        };


    }

    UserController.$inject = [ '$window', 'notificationService', 'userService', 'users'];
    function UserController($window, notificationService, userService, users) {

        if (users.length != 1) {
            notificationService.error("An error occurred while processing a user record: only one record should be provided.");
        }
        var self = this;
        self.user = users[0];
        self.edit = edit;
        self.showForm = showForm;
        self.save = save;
        self.isNewAccount = isNewAccount;
        self.roles = $window.roles;
        self.updateRole = updateRole;
        self.hasRole = hasRole;

        function edit() {
            self.user['editing'] = true;
        }

        function showForm() {
            return self.user.hasOwnProperty('editing') && self.user.editing === true;
        }

        function save() {
            delete self.user.editing;

            userService.updateUser(self.user).then(function(data){
                console.log("Successfully updated user.");
            });
        }

        function isNewAccount() {
            return ! self.user.hasOwnProperty('id');
        }

        function hasRole(role) {
            var found = false;

            for (var i=0; i < self.user.roles.length; i++) {
                if (self.user.roles[i].id == role.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        function updateRole($event, role){

            var checkbox = $event.target;

            if(checkbox.checked === true){
                self.user.roles.push(role);
            } else {
                // remove item
                for(var i=0 ; i < self.user.roles.length; i++) {
                    if(self.user.roles[i].id == role.id){
                        self.user.roles.splice(i,1);
                    }
                }
            }
        };

    }

    OrgController.$inject = [ '$routeParams', 'organizationService', 'org'];

    function OrgController($routeParams, organizationService, org) {
        var self = this;

        self.org = org[0];  //should be an array with a single element
        self.editOrg = editOrg;
        self.showOrgForm = showOrgForm;
        self.saveOrg = saveOrg;
        self.getUsers = getUsers;


        function activate() {

            organizationService.find($routeParams.org_id).then(function(data){
                self.org = data;
            });
        }

        function getUsers() {

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
        self.saveOrg = save;
        self.editOrg = edit;
        self.showOrgForm = showForm;
        self.createOrg = create;
        self.deleteOrg = deleteOrg;
        self.findOrganizations = findOrganizations;
        self.removeOrgFromModel = removeOrgFromModel;


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

        function create() {

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

        function findOrganizations() {
            organizationService.getByName(self.searchInput).then(function(data){
                self.organizations = data;
            });
        };

        function removeOrgFromModel(org) {
            var index = self.organizations.indexOf(org);
            if (index > -1) {
                self.organizations.splice(index, 1);
            }
        };

        function deleteOrg(org) {

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

        function save(org) {

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

        function edit(org) {
            org['editing'] = true;
            console.log(org);
        };


        function showForm(org) {
            return org.hasOwnProperty('editing') && org.editing === true;
        };

    };



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



    organizationService.$inject = ['$http', '$q', '$cacheFactory', '$filter', 'notificationService'];

    function organizationService ($http, $q, $cacheFactory, $filter, notificationService) {


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
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while deleting an organization.");
            });

            return deferred.promise;
        }

        function updateOrg(org) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id, org).success(function (data) {
                deferred.resolve(org);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating an organization.");
            });

            return deferred.promise;
        }


        function createOrg(org) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/organizations/', org).success(function (data) {
                angular.extend(org, data);
                deferred.resolve(org);
            }).error(function(data){
                notificationService.ajaxInfo(data);
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
            }).error(function(data){
                notificationService.ajaxInfo(data);
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
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }

        function getByName(name) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {'name': name},
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }

    }


    settingsService.$inject = ['$http', '$q', '$cacheFactory', 'notificationService'];

    function settingsService ($http, $q, $cacheFactory, notificationService) {


        var service = {
            getDeliveryMethods: getDeliveryMethods,
            getDocumentFormats: getDocumentFormats
        };

        return service;

        function getDeliveryMethods() {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/delivery-methods', {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching delivery methods.");
            });

            return deferred.promise;
        }

        function getDocumentFormats() {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/document-formats', {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching document formats.");
            });

            return deferred.promise;
        }


    }


    /* User Service */

    userService.$inject = ['$window', '$http', '$q', '$cacheFactory', '$filter', 'notificationService'];

    function userService ($window, $http, $q, $cacheFactory, $filter, notificationService) {


        var service = {
            getUsers: getUsers,
            getById: getById,
            getByName: getByName,
            deleteUser: deleteUser,
            updateUser: updateUser,
            createUser: createUser,
            find: find,
            activeUser: $window.activeUser
        };

        return service;

        function deleteUser(user) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/users/' + user.id).success(function (data) {

                removeUser(user);

                deferred.resolve(user);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a user.");
            });

            return deferred.promise;
        }

        function updateUser(user) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/users/' + user.id, user).success(function (data) {
                deferred.resolve(user);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating a user.");
            });

            return deferred.promise;
        }


        function createUser(user) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/users/', user).success(function (data) {
                angular.extend(user, data);
                deferred.resolve(user);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating a user.");
            });

            return deferred.promise;
        }


        function getUsers(orgID) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/users', {
                'params': {'organizationId': orgID},
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching users.");
            });

            return deferred.promise;
        };

        function getById(id) {
            var user = $filter('getByProperty')('id', id,  users);

            if (user == null) {
                user = find(id);
            }

            return user;
        };

        function find(id) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/users/' + id, {
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the user.");
            });

            return deferred.promise;
        }

        function getByName(name, orgID) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/users', {
                'params': {
                    'organizationId': orgID,
                    'name': name
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the user.");
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
