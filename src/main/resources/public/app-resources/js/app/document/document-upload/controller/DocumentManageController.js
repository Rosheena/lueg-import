LuegImportApp.controller('DocumentManageController', ['DocumentUploadService', 'DocumentManageService', 'Notification', 'SpinnerService', 'PromptService', 'StorageService', 'NgTableParams', function (DocumentUploadService, DocumentManageService, Notification, SpinnerService, PromptService, StorageService, NgTableParams) {

    let vm = this;
    vm.uploader = null;
    vm.file = null;
    vm.display_view = "selector";
    vm.uploadingResults = null;
    vm.disableImport = false;

    vm.openFileDialog = function () {
        document.getElementById("openFileSelector").click();
    }

    vm.init = function () {
        vm.uploader = DocumentUploadService.initUploader(this.afterUploadComplete, this.addingFile);
        (DocumentManageService.userHasSearched) ? loadDocuments(DocumentManageService.documentResults, DocumentManageService.fileName) : false;
    }

    vm.submitSpreadsheet = function () {
        SpinnerService.start();
        vm.display_view = "uploading";
        vm.uploader.uploadAll();
    };

    vm.addingFile = function(file){
        vm.file = file;
        vm.uploadingResults = null;
    }

    vm.afterUploadComplete = function (err, response, display) {

        if (err) {
            vm.clearFile();
            handleError(err, "Errors while uploading file");
        } else {
            vm.uploadingResults = response;
            console.log("response");
            console.log(vm.uploadingResults);
            vm.disableImportCheck();
            vm.tableParams = new NgTableParams({ count : 100 }, { dataset : vm.uploadingResults });
            vm.fileName = vm.file.name;
            DocumentManageService.documentResults = vm.uploadingResults;
            DocumentManageService.fileName = vm.fileName;
            DocumentManageService.userHasSearched = true;
        }

        vm.clearFile();
        vm.display_view = display;

    };

    vm.initializeUploader = function () {
        vm.clearFile();
        vm.display_view = "selector";
        vm.uploadingResults = null;
    };

    vm.clearFile = function () {
        vm.file = null;
        $("#openFileSelector").val(null);
    };

    vm.beginDocumentEdits = function (document) {
        document.edits = angular.copy(document);
        document.editing = true;
    };

    vm.cancelDocumentEdits = function (document) {
        document.editing = false;
        delete document.edits;
    };

    vm.disableImportCheck = function() {
        for(var i=0; i<vm.uploadingResults.length; i++){
            if(vm.uploadingResults[i].documentErrorType != null){
                vm.disableImport = true;
                break;
            }
        }
    }

    vm.saveDocumentEdits = function (document, documentEdits, idx) {
       /* if (!DocumentManageService.isDocumentEditValid(document)) {
            return;
        }*/

        SpinnerService.start();
        DocumentManageService.saveDocumentEdits(documentEdits, function (err, response) {
            SpinnerService.stop();

            if (err) {
                Notification.error({ title: 'Error saving Document Information', message: 'Please try again later' });
            } else {
                for(var i=0; i<vm.uploadingResults.length; i++){
                    if(vm.uploadingResults[i].id === response.id){
                        console.log("response");
                        console.log(response);
                        vm.uploadingResults[i]=response;
                    }
                }

                vm.tableParams.reload();
                vm.disableImportCheck();
                DocumentManageService.documentResults = vm.uploadingResults;
                DocumentManageService.fileName = vm.fileName;
                DocumentManageService.userHasSearched = true;
              //  vm.tableParams = new NgTableParams({ count : 100 }, { dataset : vm.uploadingResults });
               // vm.tableParams.data.splice(idx, 1, documentEdits);
                handleSuccessMessage('Successfully saved Document information');
            }
        });
        document.editing = false;
        delete document.edits;
    };

    vm.importDocuments = function () {
        DocumentManageService.importDocuments(vm.uploadingResults, function (err, response) {

        });
        vm.uploadingResults = null;
        DocumentManageService.documentResults = vm.uploadingResults;
        handleSuccessMessage('Entries submitted for import');
    }

    // Private functions

    let loadDocuments = function (documentResults, fileName) {
        vm.uploadingResults = documentResults;
        vm.fileName = fileName;
        DocumentManageService.documentResults =  vm.uploadingResults;   //  to save state when switching tabs
        vm.tableParams = new NgTableParams({ count : 100 }, { dataset : vm.uploadingResults });
    };

}]);