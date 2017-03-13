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

    var app = angular.module('networkServer', ['ui.bootstrap', 'ngRoute', 'toaster', 'ngAnimate'])
        .filter('friendlyRoleName', friendlyRoleName)
        .filter('getByProperty', getByProperty)
            .filter('bytes', function() {
            return function(bytes, precision) {
                if (bytes === 0) { return '0 bytes' }
                if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';
                if (typeof precision === 'undefined') precision = 1;

                var units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'],
                    number = Math.floor(Math.log(bytes) / Math.log(1024)),
                    val = (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision);

                return  (val.match(/\.0*$/) ? val.substr(0, val.indexOf('.')) : val) +  ' ' + units[number];
            }
        })
        .directive('toNumber', toNumber)
        .directive('fileModel', fileModel)
        .service('transactionService', transactionService)
        .service('toasterService', toasterService)
        .service('fileUpload', fileUpload)
        .service('settingsService', settingsService)
        .service('transcriptRequestService', transcriptRequestService)
        .controller("NavController", NavController)
        .controller("TransactionController", TransactionController)
        .controller("UploadController", UploadController)
        .controller("TranscriptRequestController", TranscriptRequestController)
        .config(config)
        .run(['transactionService', function(transactionService) {
            transactionService.initialize();
        }]);



    function config($routeProvider) {
        $routeProvider
            .when("/transaction-report", {
                templateUrl: "transaction-report",
                controller: "TransactionController",
                controllerAs: 'transactionCtrl'
            })
            .when("/upload", {
                templateUrl: "upload",
                controller: "UploadController",
                controllerAs: "uploadCtrl"
            })
            .when("/transcript-request-form", {
                templateUrl: "transcript-request-form",
                controller: "TranscriptRequestController",
                controllerAs: "trCtrl"
            })
            .when("/home", {
                templateUrl: "about"
            })

            .otherwise({
                redirectTo: "home"
            });
    }

    TransactionController.$inject = ['transactionService', 'toasterService', '$scope'];
    function TransactionController(transactionService, toasterService, $scope) {
        var self = this;

        self.transactions = [];
        self.status = '';
        self.operation = 'Both';
        self.error = null;
        self.deliveryStatus = '';

        self.startDate = '';
        self.stopDate = '';
        self.openStartDatePopup = openStartDatePopup;
        self.openEndDatePopup = openEndDatePopup;
        self.resend = resend;

        self.getOrgURL = getOrgURL;

        self.totalRecords = 0;
        self.offset = 1;
        self.limit = 10;
        self.reset = resetParameters;


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

        self.getTransactions = getTransactions;

        activate();

        function activate() {

            getTransactions();

        }

        function resetParameters() {
            $scope.transactionFrom.$setPristine();

            self.status = '';
            self.operation = 'Both';
            self.error = null;
            self.deliveryStatus = '';

            self.startDate = '';
            self.stopDate = '';
        }

        function getOrgURL(orgID) {
            return directoryServer + "/services/rest/v1/organizations/" + orgID;
        }

        function resend(tran) {
            transactionService.resend(tran).then(function(data){
                self.transactions.push(data);
                toasterService.success("Successfully resent document.");
            },function(error){
                toasterService.error("Failed to resend document.");
            })
        }


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
            transactionService.getTransactions(
                self.status,
                self.operation,
                self.startDate,
                self.endDate,
                self.deliveryStatus,
                self.limit,
                (self.offset-1) * self.limit).then(function(response){
                    self.transactions = response.data;
                    self.totalRecords = response.headers('X-Total-Count');

            });
        }
    }


    UploadController.$inject = ['fileUpload', 'settingsService'];
    function UploadController(fileUpload, settingsService) {
        var self = this;

        self.uploadFile = uploadFile;
        self.documentFormats = getSupportedDocumentFormatsForEndpoint();
        self.documentFormat = ''; //selected document format.
        self.recipientName = '';
        self.senderName = '';
        self.endpointURL = 'http://';
        self.fileToUpload = '';
        self.sourceSchoolCode = '';
        self.sourceSchoolCodeType = '';
        self.destinationSchoolCode = '';
        self.destinationSchoolCodeType = '';
        self.deliveryMethods = [];

        self.documentTypes = getSupportedDocumentTypes();

        self.documentType = '';

        self.departments = getSupportedDepartments();
        self.department = '';
        //Transcript Request
        self.studentRelease = false;
        self.studentReleasedMethod = '';
        self.studentBirthDate = '';
        self.studentFirstName = '';
        self.studentMiddleName = '';
        self.studentLastName = '';
        self.studentEmail = '';
        self.studentPartialSSN = '';
        self.studentCurrentlyEnrolled = false;
        //Transcript Request

        initialize();

        function uploadFile() {
            console.log("Transfer file.");
            fileUpload.uploadFileToUrl(self.fileToUpload,
                self.documentFormat.name,
                self.documentType.name,
                self.department.name,
                self.sourceSchoolCode,
                self.sourceSchoolCodeType,
                self.destinationSchoolCode,
                self.destinationSchoolCodeType,
                self.studentRelease,
                self.studentReleasedMethod,
                self.studentBirthDate,
                self.studentFirstName,
                self.studentMiddleName,
                self.studentLastName,
                self.studentEmail,
                self.studentPartialSSN,
                self.studentCurrentlyEnrolled);
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




        function initialize() {
            getDocumentFormats();
            getDocumentTypes();
            getDepartments();
            getDeliveryMethods();
        }

        function getSupportedDocumentTypes() {
            return [
                {
                    "id": 1,
                    "name": "College Transcript",
                    "description": "College level transcript."
                },
                {
                    "id": 2,
                    "name": "Transcript Request",
                    "description": "Academic transcript request."
                }
            ] ;

        }

        function getSupportedDepartments() {
            return [
                {
                    "id": 1,
                    "name": "English",
                    "description": "The English department."
                },
                {
                    "id": 2,
                    "name": "Administration",
                    "description": "Administration department"
                }
            ];
        }
        function getSupportedDocumentFormatsForEndpoint() {

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

    toasterService.$inject = [ 'toaster'] ;

    function toasterService(toaster) {
        var service = {
            success: success,
            error: error,
            ajaxInfo: ajaxInfo,
            renderHtml: renderHtml
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

        function renderHtml(type, title, html) {
            toaster.pop(type, title, html,null, 'trustedHtml');
        }
    }


    fileUpload.$inject = [ '$http', 'toasterService'] ;

    function fileUpload($http, toasterService) {
        var service = {
            uploadFileToUrl: uploadFileToUrl
        } ;

        return service;

        function uploadFileToUrl(file, fileFormat, documentType, department, sourceSchoolCode, sourceSchoolCodeType, destinationSchoolCode, destinationSchoolCodeType, studentRelease,
                                 studentReleasedMethod, studentBirthDate, studentFirstName, studentMiddleName, studentLastName,
                                 studentEmail, studentPartialSSN, studentCurrentlyEnrolled){
            var fd = new FormData();
            fd.append('file', file);
            fd.append('file_format', fileFormat );
            fd.append('source_school_code', sourceSchoolCode);
            fd.append('source_school_code_type', sourceSchoolCodeType);
            fd.append('destination_school_code', destinationSchoolCode);
            fd.append('destination_school_code_type', destinationSchoolCodeType);
            fd.append('document_type', documentType);
            fd.append('department', department);
            fd.append('student_release', studentRelease);
            fd.append('student_released_method', studentReleasedMethod);
            fd.append('student_birth_date', studentBirthDate);
            fd.append('student_first_name', studentFirstName);
            fd.append('student_middle_name', studentMiddleName);
            fd.append('student_last_name', studentLastName);
            fd.append('student_email', studentEmail);
            fd.append('student_partial_ssn', studentPartialSSN);
            fd.append('student_currently_enrolled', studentCurrentlyEnrolled);

            $http.post('/api/v1/documents/outbox', fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
            .success(function(data){
                toasterService.success('Successfully sent document.');
            })
            .error(function(data){
                toasterService.ajaxInfo(data);
            });
        }

    }

    settingsService.$inject = ['$http', '$q', '$cacheFactory', 'toasterService', '$window'];

    function settingsService($http, $q, $cacheFactory, toasterService, $window) {


        var service = {
            getDeliveryMethods: getDeliveryMethods,
            getDocumentFormats: getDocumentFormats,
            getDocumentTypes: getDocumentTypes,
            getDepartments: getDepartments
        };

        return service;

        function getDeliveryMethods() {
            var deferred = $q.defer();

            $http.get($window.directoryServer + '/services/rest/v1/delivery-methods', {
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

            $http.get( $window.directoryServer + '/services/rest/v1/document-formats', {
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

            $http.get($window.directoryServer + '/services/rest/v1/departments', {
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

            $http.get($window.directoryServer + '/services/rest/v1/document-types', {
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


    transcriptRequestService.$inject = ['$http', '$q'];

    function transcriptRequestService($http, $q) {


        var service = {
            create: create
        };

        return service;

        function create(obj) {
            var deferred = $q.defer();

            $http.post('/api/v1/transcript-requests', obj).then(function (response) {
                deferred.resolve(response);
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        }
    }



    transactionService.$inject = [ '$http', '$q', '$cacheFactory', 'toasterService'];

    function transactionService($http, $q, $cacheFactory, toasterService) {
        var service = {
            getTransactions: getTransactions,
            resend: resend,
            initialize: initialize
        };

        return service;

        function initialize() {
            console.log("TODO: transactionService initialize")
        }

        function resend(tran) {
            var deferred = $q.defer();

            $http.get('/api/v1/documents/send', {
                'params': {
                    'transaction_id': tran.id
                },
                cache: false
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function(data){
                toasterService.ajaxInfo(data);
                deferred.reject("An error occured while sending a document for transaction id " + tran.id);
            });

            return deferred.promise;
        }


        function getTransactions(status, operation, startDate, endDate, deliveryStatus, limit, offset) {

            var deferred = $q.defer();

            $http.get('/api/v1/transactions', {
                'params': {
                    'operation' : operation,
                    'delivery-status': deliveryStatus,
                    'offset' : offset,
                    'limit' : limit,
                    'from': startDate,
                    'to' : endDate,
                    'status': status
                },
                cache: false
            }).then(function (response) {
                deferred.resolve(response);
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

    TranscriptRequestController.$inject = ['transcriptRequestService', 'toasterService'];
    function TranscriptRequestController(transcriptRequestService, toasterService) {


        function Source(schoolCode, schoolCodeType, studentCurrentlyEnrolled, editing) {
            this.schoolCode = schoolCode;
            this.schoolCodeType = schoolCodeType;
            this.studentCurrentlyEnrolled = studentCurrentlyEnrolled;
            if (editing == true) {
                this.editing = true;
            }
        }

        function Destination(schoolCode, schoolCodeType, editing) {
            this.schoolCode = schoolCode;
            this.schoolCodeType = schoolCodeType;

            if (editing == true) {
                this.editing = true;
            }

        }

        var self = this;
        self.request = {
            sourceInstitutions : [],
            destinationInstitutions : [],
            studentRelease : false,
            studentReleasedMethod : '',
            studentBirthDate : '',
            studentFirstName : '',
            studentMiddleName : '',
            studentLastName : '',
            studentEmail : '',
            studentPartialSSN : '',
            fileFormat: 'PESCXML',
            mode: 'LIVE'
        };
        self.submitTranscriptRequest = submitTranscriptRequest;
        self.createSource = createSource;
        self.createDestination = createDestination;
        self.edit  = edit;
        self.save = save;
        self.remove = remove;
        self.showForm = showForm;

        function showForm(obj) {
            return obj.hasOwnProperty('editing') && obj.editing == true;
        }

        function remove(institution) {
            var index = self.request.destinationInstitutions.indexOf(institution);
            if (index > -1) {
                self.request.destinationInstitutions.splice(index, 1);
                return;
            }

            index = self.request.sourceInstitutions.indexOf(institution);
            if (index > -1) {
                self.request.sourceInstitutions.splice(index, 1);
                return;
            }

        }

        function edit(institution) {
            institution.editing = true;
        }

        function save(institution){
            delete institution.editing;
        }

        function createSource() {
            self.request.sourceInstitutions.unshift(new Source('','',false, true));
        }
        function createDestination() {
            self.request.destinationInstitutions.unshift(new Destination('','', true));
        }

        function submitTranscriptRequest() {
            console.log(self.request);
            transcriptRequestService.create(self.request).then(function(response){
                if (response.status == 200) {
                    toasterService.success("Your transcript request has been successfully delivered to the recipient with directory id " + response.data[0].recipientId);
                }
                else {
                    toasterService.ajaxInfo(response.data);
                }
            });
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
