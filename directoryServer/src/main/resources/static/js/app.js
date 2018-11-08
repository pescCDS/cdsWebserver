/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function () {

    var app = angular.module('directoryServer', ['ui.bootstrap', 'ngRoute', 'toaster', 'ngAnimate', 'ngSanitize'])
        .filter('friendlyRoleName', friendlyRoleName)
        .filter('getByProperty', getByProperty)
        .filter('trueFalse', trueFalse)
        .directive('toNumber', toNumber)
        .directive('fileModel', fileModel)
        .directive('emptyToNull', emptyToNull)
        .service('toasterService', toasterService)
        .service('organizationService', organizationService)
        .service('userService', userService)
        .service('settingsService', settingsService)
        .service('schoolCodesService', schoolCodesService)
        .service('endpointService', endpointService)
        .service('messageService', messageService)
        .service('fileUpload', fileUpload)
        .service('contactService', contactService)
        .service('actuatorService', actuatorService)
        .service('usageService', usageService)
        .controller('DirectoryController', DirectoryController)
        .controller("NavController", NavController)
        .controller("SettingsController", SettingsController)
        .controller("UploadController", UploadController)
        .controller("MessageController", MessageController)
        .controller("OrgController", OrgController)
        .controller("UserController", UserController)
        .controller("UsersController", UsersController)
        .controller("EndpointController", EndpointController)
        .controller("EndpointSelectorController", EndpointSelectorController)
        .controller("RegistrationController", RegistrationController)
        .controller("ActuatorController", ActuatorController)
        .controller("UsageController", UsageController)
        .config(config)
        .run(['organizationService', function (organizationService) {
            organizationService.initialize();
        }]);


    function config($routeProvider, $httpProvider) {

        //Create a random token to be used as the CSRF token.
        function b(a){return a?(a^Math.random()*16>>a/4).toString(16):([1e16]+1e16).replace(/[01]/g,b)};

        $httpProvider.interceptors.push(function() {
            return {
                'request': function(response) {
                    // put a new random secret into our CSRF-TOKEN Cookie before each request
                    document.cookie = 'CSRF-TOKEN=' + b();
                    return response;
                }
            };
        });

        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';


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
            }).
            when("/messages", {
                templateUrl: "messages",
                controller: "MessageController",
                controllerAs: "messageCtrl"
            }).
            when("/registration-thank-you", {
                templateUrl: "registration-thank-you"
            })
            .when("/endpoint-selector/:institution_id", {
                templateUrl: "endpoint-selector",
                controller: "EndpointSelectorController",
                controllerAs: "endpointCtrl",
                resolve: {
                    institutions: ['$route', 'organizationService', function ($route, organizationService) {
                        return organizationService.find($route.current.params.institution_id);
                    }],
                    endpoints: ['$route', 'endpointService', function ($route, endpointService) {
                        return endpointService.getEndpointsById($route.current.params.institution_id);

                    }]
                }
            })
            .when("/registration-form", {
                templateUrl: "registration-form",
                controller: "RegistrationController",
                controllerAs: "regCtrl"
            })
            .when("/home", {
                templateUrl: "about"
            })
            .when("/actuator-view", {
                templateUrl: "actuator-view",
                controller: "ActuatorController",
                controllerAs: "actCtrl"
            })
            .when("/api-usage", {
                templateUrl: "api-usage",
                controller: "UsageController",
                controllerAs: "usageCtrl"
            })
            .otherwise({
                redirectTo: "home"
            });
    }


    UploadController.$inject = ['fileUpload', 'organizationService', 'toasterService', '$uibModalInstance', 'org', '$timeout'];
    function UploadController(fileUpload, organizationService, toasterService, $uibModalInstance, org, $timeout) {
        var self = this;

        self.org = org;
        self.uploadFile = uploadFile;
        self.fileToUpload = '';
        self.showHistory = false;
        self.toggleHistory = toggleHistory;
        self.uploads = [];
        self.ok = ok;
        self.cancel = cancel;
        self.showResults = showResults;
        self.updateFilter = updateFilter;
        self.filterResults = filterResults;
        self.filter = [ 'ERROR'];
        self.hasFilter = hasFilter;


        function hasFilter(resultType) {
            return self.filter.indexOf(resultType) != -1;
        }
        function updateFilter(resultType) {
            var i = self.filter.indexOf(resultType);

            if (i == -1) {
                self.filter.push(resultType);
            }
            else {
                self.filter.splice(i, 1);
            }

        }

        function filterResults(resultRecord) {

            if (self.filter.indexOf(resultRecord.outcome) == -1) {
                return;
            }

            return resultRecord;
        }

        function ok() {
            $uibModalInstance.dismiss('cancel');
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

        function showResults(uploadObj) {

            if (!uploadObj.hasOwnProperty('results')) {
                organizationService.getUploadResults(self.org, uploadObj.id).then(function(data){
                    uploadObj['results'] = data;
                }, function error(data){
                    toasterService.error("Failed to retrieve insitution upload results.");
                });
            }


        }

        function toggleHistory() {
            self.showHistory = !self.showHistory;

            if (self.showHistory) {
                organizationService.getUploads(self.org).then(function(data){
                    self.uploads = data;
                }, function error(data){
                    toasterService.error("Failed to retrieve uploads.");
                })
            }
        }

        function showStatus(uploadRecord) {
            $timeout(fileUpload.getCSVStatus, 1000, true, uploadRecord.id).then(function(response){

                if (response.data[0].endTime == null) {
                     uploadRecord.status = 'Processing line number ' + response.data[0].lineNumber;
                     showStatus(uploadRecord);
                }
                else {
                    uploadRecord.endTime = response.data[0].endTime;
                }

                console.log(response.data[0]);
            });
        }
        function uploadFile() {

            if (self.fileToUpload == '') {
                toasterService.error("Please select a file to upload.");
                return;
            }

            fileUpload.uploadFileToUrl(self.fileToUpload, self.org, '/services/rest/v1/institutions/csv').then(function(response){

                if (response.status == 200) {
                    var uploadRecord = response.data;


                    console.log(uploadRecord);

                    self.uploads.push(uploadRecord);
                    self.showHistory = true;

                    if (uploadRecord.endTime == null) {

                        showStatus(uploadRecord);
                    }
                }
            });
        }

    }

    fileUpload.$inject = [ '$http', 'toasterService', '$q'] ;

    function fileUpload($http, toasterService, $q) {
        var service = {
            uploadFileToUrl: uploadFileToUrl,
            getCSVStatus: getCSVStatus
        } ;

        return service;

        function getCSVStatus(uploadID) {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/institutions/csv-status',  {
                    'params': {
                        'upload_id': uploadID
                    },
                    cache: false
                }).then(function(response){
                    deferred.resolve(response);
                });

            return deferred.promise;
        };


        function uploadFileToUrl(file, org, url){
            var deferred = $q.defer();

            var fd = new FormData();
            fd.append('file', file);
            fd.append('org_id', org.id);

            $http.post(url, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }

    }

    SettingsController.$inject = ['settingsService', 'toasterService'];
    function SettingsController(settingsService, toasterService) {
        var self = this;

        self.deliveryMethods = [];
        self.documentFormats = [];
        self.departments = [];
        self.documentTypes = [];

        self.getDeliveryMethods = getDeliveryMethods;
        self.getDocumentFormats = getDocumentFormats;
        self.getDepartments = getDepartments;
        self.getDocumentTypes = getDocumentTypes;
        self.create = create;
        self.showForm = showForm;
        self.deleteObject = deleteObject;
        self.updateObject = updateObject;
        self.save = save;

        function showForm(obj) {
            return obj.hasOwnProperty('editing') && obj.editing == true;
        }

        function removeFromModel(resourceName, obj) {
            var list = getList(resourceName);

            var index = list.indexOf(obj);
            if (index > -1) {
                list.splice(index, 1);
            }
        };

        function getList(resourceName) {
            if (resourceName == "document-types") {
                return self.documentTypes;
            }
            else if (resourceName == "document-formats") {
                return self.documentFormats;
            }
            else if (resourceName == "departments") {
                return self.departments;
            }
            else if (resourceName == "delivery-methods") {
                return self.deliveryMethods;
            }
        }

        function updateObject(resourceName, obj) {
            settingsService.updateObject(resourceName, obj).then(function(obj) {
                toasterService.info("Successfully updated " + obj.name);
            });
        }

        function deleteObject(resourceName, obj) {
            if (obj.hasOwnProperty('id')) {
                settingsService.deleteObject(resourceName, obj).then(function(obj){
                    removeFromModel(resourceName, obj);
                });
            }
            else {
                removeFromModel(resourceName, obj);
            }
        }


        getDeliveryMethods();

        function construct() {
            var obj = {
                'name'  : '',
                'description' : '',
                'editing' : true

            }
            return obj;
        }

        function save(resourceName, obj) {
            delete obj.editing;

            if (obj.hasOwnProperty('id')){
                settingsService.updateObject(resourceName, obj).then(function(data){
                   toasterService.info("Successfully updated resource.");
                }, function(data){
                    toasterService.ajaxInfo(data);
                    obj.editing = true;
                });
            }
            else {
                settingsService.createObject(resourceName,obj).then(function(data){
                    toasterService.info("Successfully created resource.");
                },function(data){
                    toasterService.ajaxInfo(data);
                    obj.editing = true;
                });
            }
        }

        function create(what) {

            var obj = construct();

            if (what == "document-types") {
                self.documentTypes.unshift(obj)
            }
            else if (what == "document-formats") {
                self.documentFormats.unshift(obj);
            }
            else if (what == "departments") {
                self.departments.unshift(obj);
            }
            else if (what == "delivery-methods") {
                self.deliveryMethods.unshift(obj);
            }

        }

        function getDeliveryMethods() {
            settingsService.getDeliveryMethods().then(function (data) {
                self.deliveryMethods = data;
            });
        }

        function getDocumentFormats() {
            settingsService.getDocumentFormats().then(function (data) {
                self.documentFormats = data;
            });
        }

        function getDepartments() {
            settingsService.getDepartments().then(function (data) {
                self.departments = data;
            });
        }

        function getDocumentTypes() {
            settingsService.getDocumentTypes().then(function (data) {
                self.documentTypes = data;
            });
        }

    }

    MessageController.$inject = ['messageService', 'organizationService'];
    function MessageController(messageService, organizationService) {
        var self = this;
        self.org = organizationService.getActiveOrg();
        self.dismiss = dismissMessage;
        self.messageList = [ ];

        self.getMessages = getMessages;

        self.totalRecords = 0;
        self.limit = 5;
        self.offset = 1;


        getMessages();


        function getMessages() {
            messageService.getMessages(self.org.id, self.limit,
                (self.offset-1)*self.limit).then(function(response){

                    if (response.status == 200) {
                        self.totalRecords = response.headers('X-Total-Count');
                        self.messageList = response.data;
                    }
                    else {
                        toasterService.ajaxInfo(response.data);
                    }

                });
        }

        function dismissMessage(msg) {
            messageService.dismissMessage(msg).then(function(data){
                var index = self.messageList.indexOf(msg);
                if (index > -1) {
                    self.messageList[index].dismissed = true;
                }
            },function(error){
               //TODO: handle
            }) ;
        }


    }

    RegistrationController.$inject = ['organizationService', 'toasterService', '$window', '$location'];
    function RegistrationController(organizationService, toasterService, $window, $location) {
        var self = this;
        self.register = register;
        self.hasOrgType=hasOrgType;
        self.updateOrgType=updateOrgType;
        self.organizationTypes = $window.organizationTypes;

        self.org = {
            name: '',
            organizationTypes: [],
            street: '',
            city: '',
            state: '',
            zip: '',
            telephone: '',
            website: 'http://'
        };

        self.user = {
            name: '',
            title: '',
            phone: '',
            email: '',
            username: '',
            password: ''
        };


        function register() {

            var bag = {
                'user': self.user,
                'organization': self.org
            };

            if (isValidPassword(self.user.password) == false) {
                toasterService.error(PASSWORD_REQUIREMENTS);
                return;
            }

            if (self.org.organizationTypes.length == 0) {
                toasterService.error("Please indicate whether your organization is an Institution, Service Provider, or both.");
                return;
            }


            organizationService.register(bag).then(function (data) {
                    toasterService.success("Thank you for registering.  An email will be sent to " + self.user.email + " when your organization becomes activated.");
                    $location.path('/registration-thank-you')

                },
                function (response) {
                    toasterService.error(response.message);
                });

            return true;
        }


        function hasOrgType(orgType) {
            return organizationService.hasOrgType(self.org, orgType);
        }


        function updateOrgType($event, orgType) {

            var checkbox = $event.target;

            if (checkbox.checked === true) {
                self.org.organizationTypes.push(orgType);
            } else {
                // remove item
                for (var i = 0; i < self.org.organizationTypes.length; i++) {
                    if (self.org.organizationTypes[i].id == orgType.id) {
                        self.org.organizationTypes.splice(i, 1);
                    }
                }
            }
        };

    }


    UsersController.$inject = ['organizationService', '$window', 'userService', 'users', 'toasterService'];
    function UsersController(organizationService, $window, userService, users, toasterService) {
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
        self.org = organizationService.getActiveOrg();
        self.isRoleDisabled = isRoleDisabled;

        function isRoleDisabled(role) {

            if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                return false;
            }
            else {
                //Disable roles for anything but "ROLE_ORG_ADMIN"
                if (role.id != 2) {
                    return true;
                }
            }
        }

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
            userService.getByName(self.searchInput, organizationService.getActiveOrg().id).then(function (data) {
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

                userService.deleteUser(user).then(function (data) {
                    self.removeFromModel(user);
                });

            }
            else {
                //it's a new user that hasn't been persisted.
                self.removeFromModel(user);

            }
        };

        function isNewAccount(user) {
            return !user.hasOwnProperty('id');
        }

        function save(user) {

            delete user.editing;

            if (user.hasOwnProperty('id')) {
                //update

                userService.updateUser(user).then(function (data) {
                    toasterService.info("Successfully updated user.");
                },function(data) {
                    user.editing = true;
                    toasterService.ajaxInfo(data);
                });

            }
            else {
                //create
                userService.createUser(user).then(function (data) {
                    toasterService.info("Successfully created user with id " + data.id);
                }).catch(function (e) {
                    self.edit(user);
                });


            }
        };

        function edit(user) {
            user['editing'] = true;
        };


        function showForm(user) {
            return user.hasOwnProperty('editing') && user.editing === true;
        };


        function updateRole($event, role, user) {

            var checkbox = $event.target;

            if (checkbox.checked === true) {
                user.roles.push(role);
            } else {
                // remove item
                for (var i = 0; i < user.roles.length; i++) {
                    if (user.roles[i].id == role.id) {
                        user.roles.splice(i, 1);
                    }
                }
            }
        };


    }

    UserController.$inject = ['$window', 'toasterService', 'userService', 'users'];
    function UserController($window, toasterService, userService, users) {

        if (users.length != 1) {
            toasterService.error("An error occurred while processing a user record: only one record should be provided.");
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
        self.newPassword = '';
        self.confirmPassword = '';
        self.showPasswordForm = false;
        self.updatePassword = updatePassword;
        self.isRoleDisabled = isRoleDisabled;

        function isRoleDisabled(role) {

            if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                return false;
            }
            else {
                //Disable roles for anything but "ROLE_ORG_ADMIN"
                if (role.id != 2) {
                    return true;
                }
            }
        }

        function edit() {
            self.user['editing'] = true;
        }

        function showForm() {
            return self.user.hasOwnProperty('editing') && self.user.editing === true;
        }

        function save() {
            delete self.user.editing;

            userService.updateUser(self.user).then(function (data) {
                toasterService.info("Successfully updated user.");
            }, function(data) {
                self.user.editing = true;
                toasterService.ajaxInfo(data);
            });
        }

        function isNewAccount() {
            return !self.user.hasOwnProperty('id');
        }

        function hasRole(role) {
            var found = false;

            for (var i = 0; i < self.user.roles.length; i++) {
                if (self.user.roles[i].id == role.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }


        function updateRole($event, role) {

            var checkbox = $event.target;

            if (checkbox.checked === true) {
                self.user.roles.push(role);
            } else {
                // remove item
                for (var i = 0; i < self.user.roles.length; i++) {
                    if (self.user.roles[i].id == role.id) {
                        self.user.roles.splice(i, 1);
                    }
                }
            }
        };

        function updatePassword() {
            if (isValidPassword(self.newPassword) == false) {
                toasterService.error(PASSWORD_REQUIREMENTS);
            }
            else if (self.newPassword !== self.confirmPassword) {
               toasterService.error("The passwords don't match. Please reenter your password.");
            }
            else {
                userService.changePassword(self.user, self.newPassword).then(function(response){
                    if (response.status == 200) {
                        toasterService.info("Successfully changed password.");
                    }
                    else {
                        toasterService.ajaxInfo(response.data);
                    }
                    self.showPasswordForm = false;
                })
            }
        };

    }

    EndpointController.$inject = ['endpointService', 'toasterService', 'settingsService'];

    function EndpointController(endpointService, toasterService, settingsService) {
        var self = this;
        self.showForm = showForm;
        self.save = save;
        self.edit = edit;
        self.documentFormats = [];
        self.deliveryMethods = [];
        self.documentTypes = [];
        self.departments = [];
        self.modes = [ 'TEST', 'LIVE'];

        initialize();

        function initialize() {
            settingsService.getDocumentFormats().then(function (data) {
                self.documentFormats = data;
            });
            settingsService.getDeliveryMethods().then(function (data) {
                self.deliveryMethods = data;
            });
            settingsService.getDocumentTypes().then(function (data) {
                self.documentTypes = data;
            });
            settingsService.getDepartments().then(function (data) {
                self.departments = data;
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
                endpointService.update(endpoint).then(function (data) {

                    toasterService.success("Successfully updated endpoint.");

                }, function error(data){
                    toasterService.error("Failed to update endpoint.");
                    endpoint.editing = true;
                });

            }
            else {
                //create
                endpointService.create(endpoint).then(function (data) {

                    toasterService.success("Successfully created endpoint.");

                },function error(data){
                    toasterService.error("Failed to create endpoint.");
                    endpoint.editing = true;
                });

            }
        };
    }

    EndpointSelectorController.$inject = ['institutions', 'endpoints', 'organizationService', 'endpointService', 'toasterService', 'userService'];

    function EndpointSelectorController(institutions, endpoints, organizationService, endpointService, toasterService, userService) {
        var self = this;

        self.isAssignedToInstitution = isAssignedToInstitution;
        self.institution = institutions[0];
        self.serviceProviders = [];
        self.selectableEndpoints = [];
        self.addEndpointToInstitution = addEndpointToInstitution;
        self.removeEndpointToInstitution = removeEndpointToInstitution;
        self.endpoints = endpoints;

        initialize();

        function indexOfEndpoint(endpoint, endpoints) {
            for (var i = 0; i < endpoints.length; i++) {
                if (endpoints[i].id === endpoint.id) {
                    return i;
                }
            }
            return -1;
        }

        function isAssignedToInstitution(endpoint) {
            return indexOfEndpoint(endpoint, self.endpoints) != -1;
        };

        function addEndpointToInstitution(endpoint) {

            organizationService.updateEndpoints(self.institution, endpoint, 'ADD').then(function (data) {
                self.endpoints.push(endpoint);
                toasterService.success("Endpoint " + endpoint.address + " has been added to " + self.institution.name);
            });
        };
        function removeEndpointToInstitution(endpoint) {

            organizationService.updateEndpoints(self.institution, endpoint, 'REMOVE').then(function (data) {
                var index = indexOfEndpoint(endpoint, self.endpoints);

                if (index != -1) {
                    self.endpoints.splice(index, 1);
                }
                toasterService.success("Endpoint " + endpoint.address + " has been removed from " + self.institution.name);
            });


        };

        function initialize() {
            organizationService.getServiceProvidersForInstitution(self.institution).then(function (data) {
                self.serviceProviders = data;

                if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                    endpointService.getEndpointsForServiceProviders(self.serviceProviders).then(function (data) {
                        self.selectableEndpoints = data;
                    });
                }
                else {
                    endpointService.getEndpoints(organizationService.getActiveOrg()).then(function (data) {
                        self.selectableEndpoints = data;
                    });
                }

            });

        }
    }

    ActuatorController.$inject = ['actuatorService', 'toasterService'];
    function ActuatorController(actuatorService, toasterService) {
        var self = this;

        self.getActuatorPage = getActuatorPage;
        self.getEndpoint = getEndpoint;
        self.serverInfo = null;
        self.view = {};


        function getEndpoint(endpointName) {
            self.view[endpointName] = !self.view[endpointName];
            if (self.view[endpointName] == true) {
                actuatorService.getEndpoint(endpointName).then(function(response){
                    if (response.status == 200) {

                        self.serverInfo[endpointName] = response.data;

                        console.log(response.data);
                    }
                    else {
                        toasterService.error("Failed to retrieve " + endpointName +  " data.");

                    }
                });
            }

        }

        function getActuatorPage() {
            actuatorService.getEndpoints().then(function(response){
                if (response.status == 200) {

                    response.data.links.splice(0, 1);
                    self.serverInfo = response.data;

                }
                else {
                    toasterService.error("Failed to retrieve Actuator data.");
                }
            });
        }

        getActuatorPage();


    }

    actuatorService.$inject = ['$http', '$q', '$cacheFactory', 'toasterService', '$window'];

    function actuatorService($http, $q, $cacheFactory, toasterService, $window) {
        var service = {
            getEndpoints: getEndpoints,
            getEndpoint: getEndpoint
        } ;

        return service;

        function getEndpoint(name){
            var deferred = $q.defer();

            $http.get('/' + name).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }

        function getEndpoints() {
            var deferred = $q.defer();

            $http.get('/actuator').then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }

    }



    UsageController.$inject = ['usageService', 'toasterService'];
    function UsageController(usageService, toasterService) {
        var self = this;
        self.totalRecords = 0;
        self.offset = 1;
        self.limit = 15;
        self.records = [];
        self.dashboardData = {};
        self.documentTypeCount = {};
        self.documentFormatCount = {};
        self.departmentCount = {};
        self.organizationCount = {};
        self.refreshData = refreshData;
        self.getUsageData = getUsageData;
        self.toggleParameters = toggleParameters;


        function toggleParameters(apiRequestRecord) {

            apiRequestRecord.showParameters = !apiRequestRecord.showParameters;

            if (!apiRequestRecord.hasOwnProperty("parameters")) {
                usageService.getRequestParameters(apiRequestRecord.id).then(function(response){
                    if (response.status == 200) {
                        apiRequestRecord.parameters = response.data;
                    }
                    else {
                        toasterService.ajaxInfo(response.data);
                    }
                });
            }

        }


        function refreshData() {
            usageService.getDashboardData().then(function(response){
                if (response.status == 200) {
                    self.dashboardData = response.data['queryCount'][0];
                    self.documentTypeCount = response.data['documentTypeCount'];
                    self.documentFormatCount =  response.data['documentFormatCount'];
                    self.departmentCount =  response.data['departmentCount'];
                    self.organizationCount =  response.data['organizationCount'];
                }
                else {
                    toasterService.ajaxInfo(response.data);
                }
            });

            getUsageData();
        }

        function getUsageData() {

            usageService.getUsageData(self.limit, (self.offset-1)*self.limit).then(function(response){

                    if (response.status == 200) {
                        self.totalRecords = response.headers('X-Total-Count');
                        self.records = response.data;
                    }
                    else {
                        toasterService.ajaxInfo(response.data);
                    }

                });

        }

        refreshData();
    }

    usageService.$inject = ['$http', '$q', '$cacheFactory', 'toasterService', '$window'];

    function usageService($http, $q, $cacheFactory, toasterService, $window) {
        var service = {
            getUsageData: getUsageData,
            getDashboardData: getDashboardData,
            getEndpointParameterCount: getEndpointParameterCount,
            getEndpointParameterCounts: getEndpointParameterCounts,
            getRequestParameters: getParametersByRecordId,
        } ;

        return service;

        function getUsageData(limit, offset){
            var deferred = $q.defer();

            $http.get('/usage-data',  {
                'params': {
                    'limit': limit,
                    'offset' : offset
                },
                cache: true
            }).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }

        function getDashboardData(){
            var deferred = $q.defer();

            $http.get('/usage-dashboard-data',  {
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }

        function getParametersByRecordId(recordId){
            var deferred = $q.defer();

            $http.get('/usage-request-parameters',  {
                'params' : {'api-request-id' : recordId },
                cache: true
            }).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }


        function getEndpointParameterCount(parameterName){
            var deferred = $q.defer();

            $http.get('/usage-endpoint-data',  {
                'params': {'parameter-name': parameterName},
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }


        function getEndpointParameterCounts(){
            var deferred = $q.defer();

            $http.get('/usage-endpoint-data',  {
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }
    }





    OrgController.$inject = ['$routeParams', 'organizationService', 'org', 'userService', 'endpointService', 'schoolCodesService', 'toasterService', '$window', '$uibModal', '$log', 'contactService'];

    function OrgController($routeParams, organizationService, org, userService, endpointService, schoolCodesService, toasterService, $window, $uibModal, $log, contactService) {
        var self = this;

        self.org = org[0];  //should be an array with a single element
        self.editOrg = editOrg;
        self.showOrgForm = showOrgForm;
        self.saveOrg = saveOrg;
        self.isEditableByUser = isEditableByUser;
        self.hostedBy = hostedBy;
        self.createEndpoint = createEndpoint;
        self.getEndpoints = getEndpoints;
        self.deleteEndpoint = deleteEndpoint;
        self.endpoints = [];
        self.pemCertificate = '';
        self.pemNetworkCertificate = '';
        self.certificateInfo = {};
        self.networkCertificateInfo = {};
        self.publicKey = '';
        self.addSchoolCode = addSchoolCode;
        self.editingSchoolCode = editingSchoolCode;
        self.removeSchoolCode = removeSchoolCode;
        self.saveSchoolCode = saveSchoolCode;
        self.editSchoolCode = editSchoolCode;
        self.setShowServiceProviderForm = setShowServiceProviderForm;
        self.serviceProviders = [];
        self.institutions = [];
        self.hasServiceProvider = hasServiceProvider;
        self.updateSelectedServiceProviders = updateSelectedServiceProviders;
        self.selectedServiceProviders = [];
        self.isMyOrgServiceProviderForInstitution = isMyOrgServiceProviderForInstitution;
        self.selectEndpoint = selectEndpoint;
        self.selectableEndpoints = [];
        self.organizationTypes = $window.organizationTypes;
        self.hasOrgType=hasOrgType;
        self.updateOrgType=updateOrgType;
        self.isServiceProvider=isServiceProvider;
        self.isInstitution=isInstitution;
        self.showServiceProviderForm = false;
        self.showUploadForm = showUploadForm;
        self.getInstitutionsForServiceProvider = getInstitutionsForServiceProvider;
        self.totalServicedSchools = 0;
        self.servicedSchoolsOffset = 1;
        self.showCertificateForm = false;
        self.showNetworkCertificateForm = false;
        self.setCertificate = setCertificate;
        self.setNetworkCertificate = setNetworkCertificate;
        self.toggleCertificateForm = toggleCertificateForm;
        self.toggleNetworkCertificateForm = toggleNetworkCertificateForm;
        self.getCertificate = getCertificate;
        self.getNetworkCertificate = getNetworkCertificate;
        self.saveContact = saveContact;
        self.showContactForm = showContactForm;
        self.editContact = editContact;
        self.deleteContact = deleteContact;
        self.createContact = createContact;
        self.setEnabled = setEnabled;
        self.deleteOrg = deleteOrg;
        self.showOAuthSecret = false;
        self.oauthSecret = '';
        self.showOAuthForm = false;
        self.toggleOAuthForm = toggleOAuthForm;
        self.setOAuthSecret = setOAuthSecret;
        self.getOAuthSecret = getOAuthSecret;
        self.oauthSecretReplacement = '';
        self.limit = 30;
        self.showInstitutionForm = false;
        self.institution;
        self.saveInstitution = saveInstitution;
        self.createInstitution = createInstitution;
        self.addSchoolCodeToInstitution = addSchoolCodeToInstitution;
        self.saveSchoolCodeForInstitution = saveSchoolCodeForInstitution;


        function saveInstitution() {
            if (self.institution.schoolCodes.length == 0) {
                toasterService.error("You must provide at least one school code for the institution.");
                return;
            }

            organizationService.createInstitution(self.institution).then(function (data) {
                toasterService.info("Successfully created an institution with id " + data.id);
                organizationService.createRelation(self.org.id, data.id).then(function(response){

                    if (response.status == 200) {
                        toasterService.info("Successfully added " + data.name + " as a serviced institution.");
                        self.institutions.push(data);
                        self.showInstitutionForm = false;
                    }
                    else {
                        toasterService.error("Failed to add " + data.name + " as a serviceable institution.  Contact PESC to associate this institution.");
                    }

                });

            });

        }

        function createInstitution() {
            self.showInstitutionForm = true;
            self.institution = {
                name: '',
                organizationTypes: [$window.organizationTypes[1]],
                street: '',
                city: '',
                state: '',
                zip: '',
                schoolCodes: [],
                telephone: '',
                website: 'http://'
            };

        }

        function getOAuthSecret() {
           organizationService.getOAuthSecret(self.org).then(function(response){
               if (response.status == 200) {
                   self.oauthSecret = response.data;
               }
           });
        }

        function deleteOrg() {
            organizationService.deleteOrg(self.org);
        }

        function setEnabled(enable) {

            organizationService.updateEnabled(self.org,enable).then(function(data){
                self.org.enabled = enable;
            },function(data){
                toasterService.error(data);
            })

        };

        function createContact() {
            var contact = {
                editing : true,
                name : '',
                email : '',
                phone1: '',
                phone2: '',
                address: '',
                title: '',
                organizationId : self.org.id
            };
            self.org.contacts.unshift(contact);
        }

        function removeContactFromModel(contact) {
            var index = self.org.contacts.indexOf(contact);
            if (index > -1) {
                self.org.contacts.splice(index, 1);
            }
        };

        function deleteContact(contact) {

            if (contact.hasOwnProperty('id')) {

                contactService.deleteContact(contact).then(function (data) {
                    removeContactFromModel(contact);
                });

            }
            else {
                removeContactFromModel(contact);

            }
        };


        function editContact(contact) {
            contact['editing'] = true;
        }

        function showContactForm(contact) {

            return contact.hasOwnProperty('editing') && contact.editing === true;

        }

        function saveContact(contact) {

            delete contact.editing;

            if (contact.hasOwnProperty('id')) {
                //update

                contactService.updateContact(contact).then(function (data) {
                    console.log("Successfully updated contact.");
                });

            }
            else {
                //create
                contactService.createContact(contact).then(function (data) {
                    console.log("Successfully created contact with id " + data.id);
                }).catch(function (e) {
                    self.editContact(contact);
                });

            }
        };


        function getCertificate() {
            organizationService.getCertificate(self.org).then(function(response){
                if (response.status = 200) {
                    self.certificateInfo = response.data;
                    self.pemCertificate = self.certificateInfo.pem;
                }
                else {
                    toasterService.error("Failed to retrieve current signing certificate information.");
                }

            });
        }


        function getNetworkCertificate() {
            organizationService.getNetworkCertificate(self.org).then(function(response){
                if (response.status = 200) {
                    self.networkCertificateInfo = response.data;
                    self.pemNetworkCertificate = self.networkCertificateInfo.pem;
                }
                else {
                    toasterService.error("Failed to retrieve current network certificate information.");
                }

            });
        }


        function toggleCertificateForm () {
            self.showCertificateForm = !self.showCertificateForm;
            if (self.showCertificateForm == true && self.pemCertificate == '') {
                 getCertificate();
            }
        }

        function toggleOAuthForm () {
            self.showOAuthForm = !self.showOAuthForm;
        }

        function toggleNetworkCertificateForm () {
            self.showNetworkCertificateForm = !self.showNetworkCertificateForm;
            if (self.showNetworkCertificateForm == true && self.pemNetworkCertificate == '') {
                getNetworkCertificate();
            }
        }

        function setCertificate() {

            organizationService.updateCertificate(self.org,self.pemCertificate).then(function(response){

                if (response.status == 200) {
                    self.certificateInfo = response.data;
                    self.showCertificateForm = false;
                    toasterService.success("Successfully updated certificate.");
                }
                else {
                    toasterService.error("Failed to uopdate signing certificate.");
                }

            },function(data){
                toasterService.error(data);
            });

        };

        function setOAuthSecret() {

            if (isValidPassword(self.oauthSecretReplacement) == false) {
                toasterService.error(OAUTH_SECRET_REQUIREMENTS);
                return;
            }

            organizationService.updateOAuthSecret(self.org,self.oauthSecretReplacement).then(function(response){

                if (response.status == 200) {
                    self.showOAuthForm = false;
                    self.oauthSecret = response.data;
                    toasterService.success("Successfully updated OAuth secret.");
                }
                else {
                    toasterService.error("Failed to update OAuth secret.");
                }

            },function(data){
                toasterService.error(data);
            });

        };



        function setNetworkCertificate() {

            organizationService.updateNetworkCertificate(self.org,self.pemNetworkCertificate).then(function(response){

                if (response.status == 200) {
                    self.networkCertificateInfo = response.data;
                    self.showNetworkCertificateForm = false;
                    toasterService.success("Successfully updated network certificate.");
                }
                else {
                    toasterService.error("Failed to udpate network certificate.");
                }

            },function(data){
                toasterService.error(data);
            })

        };


        function hasOrgType(orgType) {
            return organizationService.hasOrgType(self.org, orgType);
        }
        function isServiceProvider() {
            return organizationService.isServiceProvider(self.org);
        }
        function isInstitution() {
            return organizationService.isInstitution(self.org);
        }

        function showUploadForm() {

                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'upload-institutions.html',
                    controller: 'UploadController',
                    controllerAs: 'uploadCtrl',
                    size: 'lg',
                    resolve: {
                        org: function () {
                            return self.org;
                        }
                    }
                });

                modalInstance.result.then(function (historyObj) {

                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });


        }

        function updateOrgType($event, orgType) {

            var checkbox = $event.target;

            if (checkbox.checked === true) {
                self.org.organizationTypes.push(orgType);
            } else {
                // remove item
                for (var i = 0; i < self.org.organizationTypes.length; i++) {
                    if (self.org.organizationTypes[i].id == orgType.id) {
                        self.org.organizationTypes.splice(i, 1);
                    }
                }
            }
        };



        function isMyOrgServiceProviderForInstitution() {

            if (!organizationService.isInstitution(self.org) || userService.activeUser == null) { //if the current org isn't an institution, false
                return false;
            }

            //If current user is system admin, they assign endpoints if the institution
            //has any service providers.
            if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                return true;
            }

            //Otherwise, check if this institution is used by the current user's org as a service provider.

            var myorg = organizationService.getActiveOrg();
            return hasServiceProvider(myorg) && userService.hasRoleByName(userService.activeUser, 'ROLE_ORG_ADMIN');

        }

        function getServiceProviders() {
            organizationService.getServiceProviders().then(function (data) {
                self.serviceProviders = data;
            });
        };

        function setShowServiceProviderForm(show) {

            if (show === true && self.serviceProviders.length == 0) {
                getServiceProviders();
            }
            else if (show == false) {

                organizationService.updateServiceProvidersForInstitition(self.org, self.selectedServiceProviders)
                    .then(function (data) {

                    });
            }
            self.showServiceProviderForm = show;
        }


        function indexOfProvider(provider) {
            for (var i = 0; i < self.selectedServiceProviders.length; i++) {
                if (self.selectedServiceProviders[i].id === provider.id) {
                    return i;
                }
            }

            return -1;
        }


        function hasServiceProvider(provider) {
            return indexOfProvider(provider) != -1;
        }

        function updateSelectedServiceProviders($event, provider) {
            var index = indexOfProvider(provider);

            if (index > -1) {
                self.selectedServiceProviders.splice(index, 1);
            }
            else {
                self.selectedServiceProviders.push(provider);
            }
        }

        /* TOOD: combine into single call */
        getEndpoints();
        getServiceProvidersForInstitution();
        getInstitutionsForServiceProvider();
        getCertificate();
        getNetworkCertificate();

        if (isEditableByUser()) {
            getOAuthSecret();
        }

        function getEndpoints() {
            endpointService.getEndpoints(self.org).then(function (data) {
                self.endpoints = data;
            });
        }

        function getServiceProvidersForInstitution() {

            if (organizationService.isInstitution(self.org)) {
                organizationService.getServiceProvidersForInstitution(self.org).then(function (data) {
                    self.selectedServiceProviders = data;
                });
            }

        }

        function getInstitutionsForServiceProvider() {

            if (organizationService.isServiceProvider(self.org)) {
                organizationService.getInstitutionsForServiceProvider(self.org,  self.limit, (self.servicedSchoolsOffset-1)* self.limit).then(function (response) {
                    self.institutions = response.data;
                    self.totalServicedSchools = response.headers('X-Total-Count');
                });
            }

        }

        function editSchoolCode(schoolCode) {
            schoolCode.editing = true;
        }

        function editingSchoolCode(schoolCode) {
            return schoolCode.hasOwnProperty('editing');
        }

        function removeSchoolCodeFromModel(schoolCode, org) {
            var index = org.schoolCodes.indexOf(schoolCode);
            if (index > -1) {
                org.schoolCodes.splice(index, 1);
            }
        }

        function removeSchoolCode(schoolCode, org) {
            if (schoolCode.hasOwnProperty("id")) {
                removeSchoolCodeFromModel(schoolCode, org);
                schoolCodesService.removeSchoolCode(schoolCode).then(function (data) {
                    delete schoolCode.editing;
                    console.log("Successfully remove school code.");
                });

            }
            else {
                removeSchoolCodeFromModel(schoolCode, org);
            }
        }

        function isValidSchoolCode(schoolCode) {
            if (schoolCode.code == '' || schoolCode.codeType == '') {
                toasterService.info("Invalid school code.  Please select a code and type and provide the code.");
                return false;
            }

            return true;
        }

        function isDuplicateSchoolCode(schoolCodes, schoolCode) {
            for (var i = 0; i < schoolCodes.length; i++) {
                if (schoolCodes[i] === schoolCode) {
                    continue;
                }
                if (schoolCodes[i].codeType === schoolCode.codeType) {
                    toasterService.info("An " + schoolCode.codeType + " is already defined for this school. " +
                        " Please edit the existing " + schoolCode.codeType + " code or choose an unused code type.");
                    return true;
                }
            }

            return false
        }
        function saveSchoolCodeForInstitution(schoolCode) {
            if (!isValidSchoolCode(schoolCode)) {
                return;
            }

            //Also make sure a duplicate code isn't being used.
            if (isDuplicateSchoolCode(self.institution.schoolCodes, schoolCode)) {
                return;
            }

            delete schoolCode.editing;
        }

        function saveSchoolCode(schoolCode) {

            //Validate code parameters
            if (!isValidSchoolCode(schoolCode)) {
                return;
            }

            //Also make sure a duplicate code isn't being used.
            if (isDuplicateSchoolCode(self.org.schoolCodes, schoolCode)) {
                return;
            }

            delete schoolCode.editing;

            schoolCodesService.saveSchoolCode(schoolCode).then(function (data) {
                console.log("Successfully created new school code.");
            }, function (error) {
                schoolCode.editing = true;
            });
        }

        function addSchoolCode() {
            var schoolCode = {
                code: '',
                codeType: '',
                organizationId: self.org.id,
                editing: true
            }

            self.org.schoolCodes.unshift(schoolCode);
        }


        function addSchoolCodeToInstitution() {
            var schoolCode = {
                code: '',
                codeType: '',
                editing: true
            }

            self.institution.schoolCodes.unshift(schoolCode);
        }

        function createEndpoint() {
            var endpoint = {
                organization: self.org,
                organizations: [self.org],
                confirmDelivery: false,
                address: '',
                error: false,
                mode: 'LIVE',
                operationalStatus: 'ACTIVE',
                documentFormat: {},
                deliveryMethod: {},
                documentType: {},
                department: {},
                instructions: '',
                editing: true
            }

            if (self.networkCertificateInfo.hasOwnProperty('commonName')) {
                endpoint.address = 'https://' + self.networkCertificateInfo.commonName + '/';
            }

            self.endpoints.unshift(endpoint);

        }

        function selectEndpoint() {
            //get provider's endpoints and allow user to select/assign one to the current org.

            if (self.selectedServiceProviders == null || self.selectedServiceProviders == undefined || self.selectedServiceProviders.length == 0) {
                toasterService.info("There are no service providers for this institution.  At least one service provider " +
                    "must be assigned to the institution to use third party endpoints.");
                return;
            }

            if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                endpointService.getEndpointsForServiceProviders(self.selectedServiceProviders).then(function (data) {
                    self.selectableEndpoints = data;
                });
            }
            else {
                endpointService.getEndpoints(organizationService.getActiveOrg()).then(function (data) {
                    self.selectableEndpoints = data;
                });
            }

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

                endpointService.deleteEndpoint(endpoint).then(function (data) {
                    removeEndpointFromModel(endpoint);
                });

            }
            else {
                //it's a new organization that hasn't been persisted.
                removeEndpointFromModel(endpoint);

            }
        }

        function hostedBy(endpoint) {
            return self.org.id == endpoint.organization.id;
        }

        function isEditableByUser() {

            if (userService.activeUser === null) {
                return false;
            }

            if (userService.hasRoleByName(userService.activeUser, 'ROLE_SYSTEM_ADMIN')) {
                return true;
            }

            if (self.org.id === userService.activeUser.organizationId) {
                return userService.hasRoleByName(userService.activeUser, 'ROLE_ORG_ADMIN');
            }

            return false;
        }


        function activate() {

            organizationService.find($routeParams.org_id).then(function (data) {
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

            organizationService.updateOrg(self.org).then(function (data) {
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

    DirectoryController.$inject = ['$log', 'organizationService', '$location', 'toasterService', '$window'];

    function DirectoryController($log, organizationService, $location, toasterService, $window) {

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
        self.setEnabled = setEnabled;
        self.schoolCodeType = '';
        self.schoolCode = '';
        self.orgName = '';
        self.directoryID = null,
        self.resetSearch = resetSearch;
        self.isEnabled = true;
        self.isInstitution = true;
        self.isServiceProvider = false;
        self.updateOrgType=updateOrgType;
        self.hasOrgType=hasOrgType;
        self.organizationTypes = $window.organizationTypes;
        self.totalRecords = 0;
        self.limit = 5;
        self.offset = 1;

        activate();

        function activate() {

           findOrganizations();

        }

        function hasOrgType(orgType, org) {
            return organizationService.hasOrgType(org, orgType);
        }


        function updateOrgType($event, orgType, org) {

            var checkbox = $event.target;

            if (checkbox.checked === true) {
                org.organizationTypes.push(orgType);
            } else {
                // remove item
                for (var i = 0; i < org.organizationTypes.length; i++) {
                    if (org.organizationTypes[i].id == orgType.id) {
                        org.organizationTypes.splice(i, 1);
                    }
                }
            }
        };


        function resetSearch() {
            self.orgName = '';
            self.schoolCode = '';
            self.schoolCodeType = '';
            self.directoryID = null;
        }


        function getOrganizations() {
            organizationService.getOrganizations().then(function (response) {
                self.totalRecords = response.headers('X-Total-Count');

                self.organizations = response.data;
            });
        }

        function createUser(org) {
            organizationService.setActiveOrg(org);

            $location.path("users");
        };

        function setEnabled(org,enable) {

            organizationService.updateEnabled(org,enable).then(function(data){
                org.enabled = enable;
            },function(data){
                toasterService.error(data);
            })

        };


        function create() {

            var organization = {
                name: '',
                organizationTypes: [],
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
            organizationService.search(
                self.directoryID,
                self.orgName,
                self.schoolCode,
                self.schoolCodeType,
                self.isEnabled,
                self.isServiceProvider,
                self.isInstitution,
                self.limit,
                (self.offset-1)*self.limit).then(function (response) {
                    self.totalRecords = response.headers('X-Total-Count');
                    self.organizations = response.data;
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

                organizationService.deleteOrg(org).then(function (data) {
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

                organizationService.updateOrg(org).then(function (data) {
                    $log.info("Successfully update org.");
                });

            }
            else {
                //create
                organizationService.createOrg(org).then(function (data) {
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


    toasterService.$inject = ['toaster'];

    function toasterService(toaster) {
        var service = {
            success: success,
            error: error,
            ajaxInfo: ajaxInfo,
            info: info
        };

        return service;

        function info(text) {
            toaster.pop('warning', "Info", text);
        }

        function success(text) {
            toaster.pop('success', "Success", text);
        }

        function ajaxInfo(responseObject) {
            if (responseObject.hasOwnProperty('error') && responseObject.hasOwnProperty('message')){
                toaster.pop('info', responseObject.error, responseObject.message);
            }

        }

        function error(text) {
            toaster.pop('error', "Error", text);
        }
    }


    endpointService.$inject = ['$http', '$q', '$cacheFactory', 'toasterService'];

    function endpointService($http, $q, $cacheFactory, toasterService) {
        var service = {
            getEndpoints: getEndpoints,
            getEndpointsById: getEndpointsById,
            update: update,
            create: create,
            deleteEndpoint: deleteEndpoint,
            getEndpointsForServiceProviders: getEndpointsForServiceProviders
        }

        return service;

        function getAttributes(input, attr) {
            var output = [];
            for (var i = 0; i < input.length; ++i)
                output.push(input[i][attr]);
            return output;
        }


        function getEndpointsForServiceProviders(serviceProviders) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/endpoints', {
                'params': {'organizationId': getAttributes(serviceProviders, "id")},
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching endpoints for list of service providers.");
            });

            return deferred.promise;
        }


        function deleteEndpoint(endpoint) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/endpoints/' + endpoint.id).success(function (data) {
                deferred.resolve(endpoint);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting an organization.");
            });

            return deferred.promise;
        }

        function update(endpoint) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/endpoints/' + endpoint.id, endpoint).success(function (data) {
                deferred.resolve(endpoint);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating an endpoint.");
            });

            return deferred.promise;
        }


        function create(endpoint) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/endpoints/', endpoint).success(function (data) {
                angular.extend(endpoint, data);
                deferred.resolve(endpoint);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while creating an endpoint.");
            });

            return deferred.promise;
        }


        function getEndpointsById(org_id) {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/endpoints', {
                'params': {
                    'organizationId': [org_id]
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching endpoints for " + org.name);
            });

            return deferred.promise;
        }

        function getEndpoints(org) {

            return getEndpointsById(org.id);
        }
    }

    messageService.$inject = ['$http', '$q', '$cacheFactory', '$filter', 'toasterService'];

    function messageService($http, $q, $cacheFactory, $filter, toasterService) {

        var service = {
            getMessages: getMessages,
            dismissMessage: dismiss
        };

        return service;

        function dismiss(msg) {
            var deferred = $q.defer();

            $http.put('/services/rest/v1/messages/' + msg.id,{},{
                'params' : {
                    'dismiss': true
                }
            }).success(function (data) {
                deferred.resolve(msg);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while dismissing a message.");
            });

            return deferred.promise;
        }

        function getMessages(orgID, limit, offset) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/messages', {
                'params': {
                    'limit' : limit,
                    'offset' : offset,
                    'organization_id': orgID
                },
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
            }, function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }
    }

    organizationService.$inject = ['$window', '$http', '$q', '$cacheFactory', '$filter', 'toasterService'];

    function organizationService($window, $http, $q, $cacheFactory, $filter, toasterService) {


        var service = {
            getOrganizations: getOrganizations,
            getById: getById,
            getByName: getByName,
            deleteOrg: deleteOrg,
            updateOrg: updateOrg,
            createOrg: createOrg,
            createInstitution: createInstitution,
            find: find,
            getActiveOrg: getActiveOrg,
            setActiveOrg: setActiveOrg,
            search: search,
            getServiceProviders: getServiceProviders,
            getServiceProvidersForInstitution: getServiceProvidersForInstitution,
            updateServiceProvidersForInstitition: setServiceProvidersForInstitution,
            getInstitutionsForServiceProvider: getInstitutionsForServiceProvider,
            updateEndpoints: updateEndpoints,
            register: register,
            updateEnabled: updateEnabled,
            updateCertificate: updateCertificate,
            initialize: initialize,
            hasOrgType: hasOrgType,
            isServiceProvider: isServiceProvider,
            isInstitution: isInstitution,
            getUploads: getUploads,
            getUploadResults: getUploadResults,
            getCertificate: getCertificate,
            getNetworkCertificate: getNetworkCertificate,
            updateNetworkCertificate: updateNetworkCertificate,
            updateOAuthSecret: udpateOAuthSecret,
            getOAuthSecret: getOAuthSecret,
            createRelation: createRelation
        };

        return service;

        var activeOrg;

        function initialize() {
            if ($window.activeUser !== null) {
                find($window.activeUser.organizationId).then(function (orgArray) {
                    activeOrg = orgArray[0];

                    getInstitutionsForServiceProvider(activeOrg).then(function (response) {
                        activeOrg.institutions = response.data;
                    });

                    //organizationService.getCertificate(activeOrg);
                });
            }
        }


        function indexOfType(org, typeName) {
            for (var i = 0; i < org.organizationTypes.length; i++) {
                if (org.organizationTypes[i].name == typeName){
                    return i;
                }

            }
            return -1;
        }

        function isServiceProvider(org) {
            return indexOfType(org, 'Service Provider') != -1;
        }

        function isInstitution(org) {
            return indexOfType(org, 'Institution') != -1;
        }


        function getUploads(org) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/service-providers/' + org.id + "/uploads", {
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching upload history.");
            });

            return deferred.promise;

        }

        function getUploadResults(org, uploadID) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/service-providers/' + org.id + "/upload-results", {
                params: {
                    'upload_id' : uploadID
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching institutions upload results.");
            });

            return deferred.promise;

        }

        function hasOrgType(org, orgType) {

            var found = false;

            for (var i = 0; i < org.organizationTypes.length; i++) {
                if (org.organizationTypes[i].id == orgType.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        function getActiveOrg() {
            return activeOrg;
        }

        function setActiveOrg(orgObj) {
            activeOrg = orgObj;
        }

        function updateEndpoints(org, endpoint, operation) {
            var deferred = $q.defer();

            $http.post('/services/rest/v1/organizations/' + org.id, null, {
                'params': {'endpoint_id': endpoint.id, 'operation': operation}
            }).success(function (data) {
                deferred.resolve(endpoint);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating the endpoints for " + org.name + ".");
            });

            return deferred.promise;
        }


        function deleteOrg(org) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/organizations/' + org.id).success(function (data) {
                deferred.resolve(org);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting an organization.");
            });

            return deferred.promise;
        }

        function updateOrg(org) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id, org).success(function (data) {
                deferred.resolve(org);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating an organization.");
            });

            return deferred.promise;
        }

        function updateEnabled(org, enabled) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id + '/enabled', enabled ).success(function (data) {
                deferred.resolve(org);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating the organization's enabled status.");
            });

            return deferred.promise;
        }

        function updateCertificate(org, pemCert) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id + '/signing-certificate', pemCert ).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }

        function udpateOAuthSecret(org, oauthSecret) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id + '/oauth-secret', oauthSecret ).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }

        function updateNetworkCertificate(org, pemCert) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/organizations/' + org.id + '/network-certificate', pemCert ).then(function (response) {
                deferred.resolve(response);
            },function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }

        function getCertificate(org) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations/' + org.id + '/signing-certificate' ).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }

        function getOAuthSecret(org) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations/' + org.id + '/oauth-secret' ).then(function (response) {
                deferred.resolve(response);
            },function(response){
                deferred.resolve(response);
            });

            return deferred.promise;
        }


        function getNetworkCertificate(org) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations/' + org.id + '/network-certificate' ).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }


        function createOrg(org) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/organizations/', org).success(function (data) {
                angular.extend(org, data);
                deferred.resolve(org);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating an organization.");
            });

            return deferred.promise;
        }

        function createInstitution(org) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/institutions/', org).success(function (data) {
                angular.extend(org, data);
                deferred.resolve(org);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating an institution.");
            });

            return deferred.promise;
        }

        function createRelation(serviceProviderID, institutionID) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/institutions/relation', { 'serviceProviderID' : serviceProviderID, 'institutionID' : institutionID }).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }


        function register(formObject) {

            var deferred = $q.defer();

            $http.post('/registration/', formObject).success(function (data) {
                deferred.resolve(formObject);
            }).error(function (data) {

                deferred.reject(data);
            });

            return deferred.promise;
        }


        function getOrganizations() {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                params: {'enabled': true},
                cache: false
            }).then(function (response) {
                organizations = response.data;
                deferred.resolve(response);
            });

            return deferred.promise;
        };

        function getById(id) {
            var org = $filter('getByProperty')('id', id, organizations);

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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the organization.");
            });

            return deferred.promise;
        }


        function search(directoryId, name, organizationCode, organizationCodeType, enabled, isServiceProvider,isInstitution, limit, offset) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {
                    'id' : directoryId,
                    'name': name,
                    'organizationCode': organizationCode,
                    'organizationCodeType': organizationCodeType,
                    'enabled': enabled,
                    'serviceprovider' : isServiceProvider,
                    'institution': isInstitution,
                    'limit': limit,
                    'offset': offset
                },
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }

        function getServiceProviders() {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/organizations', {
                'params': {
                    'type': 'Service Provider'
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the service providers.");
            });

            return deferred.promise;
        }

        function getInstitutionsForServiceProvider(org,limit,offset) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/institutions', {
                'params': {
                    'service_provider_id': org.id,
                    'limit': limit,
                    'offset' : offset
                },
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating the service providers for institution.");
            });

            return deferred.promise;
        }

    }


    schoolCodesService.$inject = ['$http', '$q', 'toasterService'];

    function schoolCodesService($http, $q, toasterService) {


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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a school code.");
            });

            return deferred.promise;
        }

        function updateSchoolCode(schoolCode) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/school-codes/' + schoolCode.id, schoolCode).success(function (data) {
                deferred.resolve(schoolCode);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating a school code.");
            });

            return deferred.promise;
        }


        function createSchoolCode(schoolCode) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/school-codes/', schoolCode).success(function (data) {
                angular.extend(schoolCode, data);
                deferred.resolve(schoolCode);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while creating a school code.");
            });

            return deferred.promise;
        }
    }

    settingsService.$inject = ['$http', '$q', '$cacheFactory', 'toasterService'];

    function settingsService($http, $q, $cacheFactory, toasterService) {


        var service = {
            getDeliveryMethods: getDeliveryMethods,
            getDocumentFormats: getDocumentFormats,
            getDocumentTypes: getDocumentTypes,
            getDepartments: getDepartments,
            createObject: createObject,
            updateObject: updateObject,
            deleteObject: deleteObject
        };

        return service;

        function createObject(resourceName, obj) {
            var deferred = $q.defer();

            $http.post('/services/rest/v1/' + resourceName, obj).success(function (data) {
                angular.extend(obj, data);
                deferred.resolve(obj);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while creating a " + resourceName + " resource.");
            });

            return deferred.promise;
        }

        function updateObject(resourceName, obj) {
            var deferred = $q.defer();

            $http.put('/services/rest/v1/' + resourceName + '/' + obj.id, obj).success(function (data) {
                deferred.resolve(obj);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating a " + resourceName + " resource.");
            });

            return deferred.promise;
        }

        function deleteObject(resourceName, obj) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/' + resourceName + '/' + obj.id).success(function (data) {
                deferred.resolve(obj);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a " + resourceName + " resource.");
            });

            return deferred.promise;
        }

        function getDeliveryMethods() {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/delivery-methods', {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching document formats.");
            });

            return deferred.promise;
        }

        function getDepartments() {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/departments', {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching department definitions.");
            });

            return deferred.promise;
        }

        function getDocumentTypes() {
            var deferred = $q.defer();

            $http.get('/services/rest/v1/document-types', {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching document type definitions.");
            });

            return deferred.promise;
        }


    }


    /* User Service */

    userService.$inject = ['$window', '$http', '$q', '$cacheFactory', '$filter', 'toasterService'];

    function userService($window, $http, $q, $cacheFactory, $filter, toasterService) {


        var service = {
            getUsers: getUsers,
            getById: getById,
            getByName: getByName,
            deleteUser: deleteUser,
            updateUser: updateUser,
            createUser: createUser,
            changePassword: changePassword,
            find: find,
            hasRole: hasRole,
            hasRoleByName: hasRoleByName,
            activeUser: $window.activeUser
        };

        return service;

        function deleteUser(user) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/users/' + user.id).success(function (data) {

                deferred.resolve(user);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a user.");
            });

            return deferred.promise;
        }

        function hasRole(user, role) {

            var found = false;

            for (var i = 0; i < user.roles.length; i++) {
                if (user.roles[i].id == role.id) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        function hasRoleByName(user, roleName) {
            if (user == null) {
                return false;
            }
            var found = false;

            for (var i = 0; i < user.roles.length; i++) {
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating a user.");
            });

            return deferred.promise;
        }


        function changePassword(user, password) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/users/' + user.id + '/password', password).then(function (response) {
                deferred.resolve(response);
            }, function (response) {
                deferred.resolve(response);
            });

            return deferred.promise;
        }


        function createUser(user) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/users/', user).success(function (data) {
                angular.extend(user, data);
                deferred.resolve(user);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching users.");
            });

            return deferred.promise;
        };

        function getById(id) {
            var user = $filter('getByProperty')('id', id, users);

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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
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
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching the user.");
            });

            return deferred.promise;
        }

    }


    /* Contact Service */

    contactService.$inject = ['$window', '$http', '$q', '$cacheFactory', '$filter', 'toasterService'];

    function contactService($window, $http, $q, $cacheFactory, $filter, toasterService) {


        var service = {
            deleteContact: deleteContact,
            updateContact: updateContact,
            createContact: createContact
        };

        return service;

        function deleteContact(contact) {
            var deferred = $q.defer();

            $http.delete('/services/rest/v1/contacts/' + contact.id).success(function (data) {
                deferred.resolve(contact);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while deleting a contact.");
            });

            return deferred.promise;
        }

        function updateContact(contact) {

            var deferred = $q.defer();

            $http.put('/services/rest/v1/contacts/' + contact.id, contact).success(function (data) {
                deferred.resolve(contact);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating a contact.");
            });

            return deferred.promise;
        }


        function createContact(contact) {

            var deferred = $q.defer();

            $http.post('/services/rest/v1/contacts/', contact).success(function (data) {
                angular.extend(contact, data);
                deferred.resolve(contact);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while updating a contact.");
            });

            return deferred.promise;
        }


        function getContacts(orgID) {

            var deferred = $q.defer();

            $http.get('/services/rest/v1/contacts', {
                'params': {'organizationId': orgID},
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data) {
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while fetching contacts.");
            });

            return deferred.promise;
        };

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
        return function (propertyName, propertyValue, collection) {
            var i = 0, len = collection.length;
            for (; i < len; i++) {
                if (collection[i][propertyName] == +propertyValue) {
                    return collection[i];
                }
            }
            return null;
        }
    }


    function trueFalse(){
        return function(text, length, end) {
            if (text) {
                return 'Y';
            }
            return 'N';
        };
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

    function isValidPassword(password) {
        return /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$_! %*#?&])[A-Za-z\d$@$_! %*#?&]{15,}$/.test(password);
    }

    function emptyToNull() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                ctrl.$parsers.push(function(viewValue) {
                    if(viewValue === "") {
                        return null;
                    }
                    return viewValue;
                });
            }
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

    var PASSWORD_REQUIREMENTS = "The password must be at least 15 characters long, contain 1 upper case letter, 1 lower case letter, 1 number and 1 special character $@$ _!%*#?&.";
    var OAUTH_SECRET_REQUIREMENTS = "The OAuth secret must be at least 15 characters long, contain 1 upper case letter, 1 lower case letter, 1 number and 1 special character $@$ _!%*#?&.";
})();
