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
        .service('schoolCodesService', schoolCodesService)
        .service('endpointService', endpointService)
        .controller('DirectoryController', DirectoryController)
        .controller("NavController", NavController)
        .controller("SettingsController", SettingsController)
        .controller("OrgController", OrgController)
        .controller("UserController", UserController)
        .controller("UsersController", UsersController)
        .controller("EndpointController", EndpointController)
        .config(config)
        .run(['organizationService', function(organizationService) {
            organizationService.initialize();
        }]);



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
                    users: ['$window', 'userService', 'organizationService', function ($window, userService, organizationService) {
                        return userService.getUsers(organizationService.getActiveOrg().id);
                    }]
                }
            })
            .when("/home", {
                templateUrl: "about"
            })
            .otherwise({
                redirectTo: "home"
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

    UsersController.$inject = [ 'organizationService', '$window', 'userService', 'users'];
    function UsersController(organizationService, $window, userService, users) {
        var self = this;
        self.users = users;
        self.roles = $window.roles;
        self.isNewAccount = isNewAccount;
        self.getUsers = userService.getUsers;
        self.create = create;
        self.find = find;
        self.delete = deleteUser;
        self.hasRole = userService.hasRole;
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
                organizationId: organizationService.getActiveOrg().id,
                editing: true
            };
            self.users.push(user);

        };


        self.searchInput = '';

        function find() {
            userService.getByName(self.searchInput,organizationService.getActiveOrg().id ).then(function(data){
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

    EndpointController.$inject = ['endpointService', 'notificationService', 'settingsService'];

    function EndpointController(endpointService, notificationService, settingsService) {
        var self = this;
        self.showForm = showForm;
        self.save = save;
        self.edit = edit;
        self.documentFormats = [];
        self.deliveryMethods = [];

        initialize();

        function initialize() {
            settingsService.getDocumentFormats().then(function(data){
                self.documentFormats = data;
            });
            settingsService.getDeliveryMethods().then(function(data){
                self.deliveryMethods = data;
            });
        }


        function showForm(endpoint) {
            return endpoint.hasOwnProperty('editing') && endpoint.editing === true;
        };

        function edit(endpoint) {
            endpoint['editing'] = true;
        }

        function save(endpoint) {

            delete endpoint.editing;

            if (endpoint.hasOwnProperty('id')) {
                endpointService.update(endpoint).then(function(data){
                    notificationService.success("Successfully updated endpoint.");
                });

            }
            else {
                //create
                endpointService.create(endpoint).then(function(data){
                    notificationService.success("Successfully created endpoint.");
                });

            }
        };
    }

    OrgController.$inject = [ '$routeParams', 'organizationService', 'org', 'userService', 'endpointService', 'schoolCodesService'];

    function OrgController($routeParams, organizationService, org, userService, endpointService, schoolCodesService) {
        var self = this;

        self.org = org[0];  //should be an array with a single element
        self.editOrg = editOrg;
        self.showOrgForm = showOrgForm;
        self.saveOrg = saveOrg;
        self.isEditableByUser = isEditableByUser;
        self.createEndpoint = createEndpoint;
        self.getEndpoints = getEndpoints;
        self.deleteEndpoint = deleteEndpoint;
        self.endpoints = [];
        self.addSchoolCode = addSchoolCode;
        self.editingSchoolCode = editingSchoolCode;
        self.removeSchoolCode = removeSchoolCode;
        self.saveSchoolCode = saveSchoolCode;
        self.editSchoolCode = editSchoolCode;
        self.setShowServiceProviderForm = setShowServiceProviderForm;
        self.getShowServiceProviderForm = getShowServiceProviderForm;
        self.serviceProviders = [];
        self.institutions = self.org.institutions;
        self.hasServiceProvider = hasServiceProvider;
        self.updateSelectedServiceProviders = updateSelectedServiceProviders;
        self.selectedServiceProviders = [];


        var showServiceProviderForm = false;

        function getServiceProviders() {
            organizationService.getServiceProviders().then(function(data){
                self.serviceProviders = data;
            });
        };

        function setShowServiceProviderForm(show) {

            if (show === true && self.serviceProviders.length == 0) {
                getServiceProviders();
            }
            else if (show == false) {

                organizationService.updateServiceProvidersForInstitition(self.org, self.selectedServiceProviders)
                    .then(function(data){

                    });
            }
            showServiceProviderForm = show;
        }
        function getShowServiceProviderForm() {
            return showServiceProviderForm;
        }

        function indexOf(provider) {
            for(var i=0; i < self.selectedServiceProviders.length; i++) {
                if (self.selectedServiceProviders[i].id === provider.id) {
                    return i;
                }
            }

            return -1;
        }

        function hasServiceProvider(provider) {
           return indexOf(provider) != -1;
        }

        function updateSelectedServiceProviders($event, provider) {
            var index = indexOf(provider);

            if (index > -1) {
                self.selectedServiceProviders.splice(index, 1);
            }
            else {
                self.selectedServiceProviders.push(provider);
            }
        }

        getEndpoints();
        getServiceProvidersForInstitution();

        function getEndpoints() {
            endpointService.getEndpoints(self.org).then(function(data){
                self.endpoints = data;
            });
        }

        function getServiceProvidersForInstitution() {

            if (self.org.type ==1 ) {
                organizationService.getServiceProvidersForInstitution(self.org).then(function(data){
                    self.selectedServiceProviders = data;
                });
            }

        }

        function editSchoolCode(schoolCode) {
            schoolCode.editing = true;
        }

        function editingSchoolCode(schoolCode) {
            return schoolCode.hasOwnProperty('editing');
        }

        function removeSchoolCodeFromModel(schoolCode) {
            var index = self.org.schoolCodes.indexOf(schoolCode);
            if (index > -1) {
                self.org.schoolCodes.splice(index, 1);
            }
        }

        function removeSchoolCode(schoolCode) {
            if (schoolCode.hasOwnProperty("id")) {
                removeSchoolCodeFromModel(schoolCode);
                schoolCodesService.removeSchoolCode(schoolCode).then(function(data){
                    delete schoolCode.editing;
                    console.log("Successfully remove school code.");
                });

            }
            else {
                removeSchoolCodeFromModel(schoolCode);
            }
        }

        function saveSchoolCode(schoolCode) {
            delete schoolCode.editing;

            schoolCodesService.saveSchoolCode(schoolCode).then(function(data){
                console.log("Successfully created new school code.");
            });
        }

        function addSchoolCode() {
            var schoolCode = {
                code: '4226',
                codeType: 'FICE',
                organizationId: self.org.id,
                editing: true
            }

            self.org.schoolCodes.push(schoolCode);
        }

        function createEndpoint() {
            var endpoint = {
                organization:  self.org,
                organizations:  [ self.org ],
                confirmDelivery: false,
                address: '',
                error: false,
                documentFormat: {},
                deliveryMethod: {},
                instructions: '',
                editing: true
            }

            self.endpoints.push(endpoint);

        }

        function removeEndpointFromModel(endpoint) {
            var index = self.endpoints.indexOf(endpoint);
            if (index > -1) {
                self.endpoints.splice(index, 1);
            }
        }

        function deleteEndpoint(endpoint) {

            //If it's an existing org we need to delete it on the server
            if (endpoint.hasOwnProperty('id')) {

                endpointService.deleteEndpoint(endpoint).then(function(data){
                    removeEndpointFromModel(endpoint);
                });

            }
            else {
                //it's a new organization that hasn't been persisted.
                removeEndpointFromModel(endpoint);

            }
        }


        function isEditableByUser() {

            if (userService.activeUser === null) {
                return false;
            }

            if (self.org.id === userService.activeUser.organizationId) {
                return userService.hasRoleByName(userService.activeUser, 'ROLE_ORG_ADMIN');
            }

            return userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN');
        }


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

    DirectoryController.$inject = ['$log', 'organizationService', '$location'];

    function DirectoryController($log, organizationService, $location) {

        var self = this;

        self.organizations = [];
        self.saveOrg = save;
        self.editOrg = edit;
        self.showOrgForm = showForm;
        self.createOrg = create;
        self.deleteOrg = deleteOrg;
        self.findOrganizations = findOrganizations;
        self.removeOrgFromModel = removeOrgFromModel;
        self.createUser = createUser;
        self.schoolCodeType = '';
        self.schoolCode = '';
        self.orgName = '';
        self.resetSearch = resetSearch;

        activate();

        function activate() {
            return getOrganizations().then(function() {

                console.log('Activated Organizations View');
            });
        }

        function resetSearch() {
            self.orgName = '';
            self.schoolCode = '';
            self.schoolCodeType = '';
        }


        function getOrganizations() {
            return organizationService.getOrganizations().then(function(data){
                self.organizations = data;
            });
        }

        function createUser(org) {
            console.log(organizationService.getActiveOrg());

            organizationService.setActiveOrg(org);

            console.log(organizationService.getActiveOrg() );

            $location.path( "users" );
        };


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

        function findOrganizations() {
            organizationService.search(self.orgName, self.schoolCode, self.schoolCodeType).then(function(data){
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


    endpointService.$inject = [ '$http', '$q', '$cacheFactory', 'notificationService'];

    function endpointService($http, $q, $cacheFactory, notificationService) {
        var service = {
            getEndpoints: getEndpoints,
            update: update,
            create: create,
            deleteEndpoint: deleteEndpoint

        };

        return service;

        function deleteEndpoint(endpoint) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/endpoints/' + endpoint.id).success(function (data) {
                deferred.resolve(endpoint);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while deleting an organization.");
            });

            return deferred.promise;
        }

        function update(endpoint) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/endpoints/' + endpoint.id, endpoint).success(function (data) {
                deferred.resolve(endpoint);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating an endpoint.");
            });

            return deferred.promise;
        }


        function create(endpoint) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/endpoints/', endpoint).success(function (data) {
                angular.extend(endpoint, data);
                deferred.resolve(endpoint);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while creating an endpoint.");
            });

            return deferred.promise;
        }



        function getEndpoints(org) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/endpoints', {
                'params': {'organizationId': org.id},
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching endpoints for " + org.name);
            });

            return deferred.promise;
        }
    }

    organizationService.$inject = ['$window', '$http', '$q', '$cacheFactory', '$filter', 'notificationService'];

    function organizationService ($window, $http, $q, $cacheFactory, $filter, notificationService) {


        var service = {
            getOrganizations: getOrganizations,
            getById: getById,
            getByName: getByName,
            deleteOrg: deleteOrg,
            updateOrg: updateOrg,
            createOrg: createOrg,
            find: find,
            getActiveOrg: getActiveOrg,
            setActiveOrg: setActiveOrg,
            search: search,
            getServiceProviders: getServiceProviders,
            getServiceProvidersForInstitution: getServiceProvidersForInstitution,
            updateServiceProvidersForInstitition: setServiceProvidersForInstitution,
            initialize: initialize
        };

        return service;

        var activeOrg;

        function initialize() {
            if ($window.activeUser !== null) {
                find($window.activeUser.organizationId).then(function(orgArray){
                    activeOrg = orgArray[0];
                });

            }
        }


        function getActiveOrg() {
            return activeOrg;
        }

        function setActiveOrg(orgObj) {
            activeOrg = orgObj;
        }

        function deleteOrg(org) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/organizations/' + org.id).success(function (data) {
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


        function search(name,organizationCode,organizationCodeType) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {
                    'name': name,
                    'organizationCode': organizationCode,
                    'organizationCodeType': organizationCodeType
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }

        function getServiceProviders() {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {
                    'type': 2
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the service providers.");
            });

            return deferred.promise;
        }

        function getServiceProvidersForInstitution(institution) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/service-providers', {
                'params': {
                    'institution_id': institution.id
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the service providers.");
            });

            return deferred.promise;
        }

        function setServiceProvidersForInstitution(institution, serviceProviders) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/service-providers', serviceProviders, {
                'params': {
                    'institution_id': institution.id,
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating the service providers for institution.");
            });

            return deferred.promise;
        }

    }



    schoolCodesService.$inject = ['$http', '$q', 'notificationService'];

    function schoolCodesService ($http, $q, notificationService) {


        var service = {
            saveSchoolCode: createSchoolCode,
            removeSchoolCode: removeSchoolCode,
            updateSchoolCode: updateSchoolCode
        };

        return service;


        function removeSchoolCode(schoolCode) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/school-codes/' + schoolCode.id, schoolCode).success(function (data) {
                deferred.resolve(schoolCode);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a school code.");
            });

            return deferred.promise;
        }

        function updateSchoolCode(schoolCode) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/school-codes/' + schoolCode.id, schoolCode).success(function (data) {
                deferred.resolve(schoolCode);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating a school code.");
            });

            return deferred.promise;
        }


        function createSchoolCode(schoolCode) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/school-codes/', schoolCode).success(function (data) {
                angular.extend(schoolCode, data);
                deferred.resolve(schoolCode);
            }).error(function(data){
                notificationService.ajaxInfo(data);
                deferred.reject("An error occured while updating a school code.");
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
            hasRole: hasRole,
            hasRoleByName: hasRoleByName,
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

        function hasRole(user,role) {

            var found = false;

            for (var i=0; i < user.roles.length; i++) {
                if (user.roles[i].id == role.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        function hasRoleByName(user, roleName) {
            var found = false;

            for (var i=0; i < user.roles.length; i++) {
                if (user.roles[i].name === roleName) {
                    found = true;
                    break;
                }
            }
            return found;
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
