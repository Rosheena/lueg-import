LuegImportApp.service('DocumentUploadService', function ($rootScope, $http, FileUploader, PromptService, SpinnerService, StorageService) {

    var ACCEPTED_FILE_TYPES = ['csv', 'text/csv'];

    this.uploader = null;

    this.initUploader = function(afterUploadCompleteCallback, addingFileCallback){
        let uploader = new FileUploader({
            url: 'app/lueg/document/validate',
            headers: {
                Authorization: StorageService.getItem("AUTHORIZATION")
            },
            formData: [
                { "userName":  $rootScope.current_user.username }
            ]
        });

        uploader.filters.push({
            name: 'customFilter',
            fn: function (item, options) {
                let extension = item.name.split('.').pop().toLowerCase();

                if (!_.includes(ACCEPTED_FILE_TYPES, extension)) {
                    return false;
                }

                if (item.size < 10) {
                    return false;
                }

                return this.queue.length < 1;
            }
        });

        uploader.onWhenAddingFileFailed = function (item, filter, options) {
            let extension = item.name.split('.').pop();

            if (!_.includes(ACCEPTED_FILE_TYPES, extension)) {
                PromptService.message('Invalid File', 'Only CSV Files are accepted.', 'prompt-modal-lg-widow');
                return;
            }

            if (item.size < 10) {
                PromptService.message('Invalid File', 'File doesn\'t seem to be a valid CSV file', 'prompt-modal-lg-widow');
                return;
            }

            if (this.queue.length === 1) {
                PromptService.message('Invalid File', 'A file is already selected [' + item.name + ']', 'prompt-modal-lg-widow');
            }
        };

        uploader.onAfterAddingFile = function (fileItem) {
            addingFileCallback(fileItem.file);
        }

        uploader.onCompleteItem = function (fileItem, response, status, headers) {
            SpinnerService.stop();

            if (status !== 200) {
                afterUploadCompleteCallback('There was an error uploading the file', response, "selector");
            } else {
                afterUploadCompleteCallback(null, response, "result");
            }

            uploader.clearQueue();

        }

        return this.uploader = uploader;
    }

});
