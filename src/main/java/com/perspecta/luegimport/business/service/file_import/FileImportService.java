package com.perspecta.luegimport.business.service.file_import;

import com.perspecta.luegimport.business.domain.document.Document;
import com.perspecta.luegimport.business.domain.document.DocumentRepository;
import com.perspecta.luegimport.business.domain.document_wrapper.DocumentWrapper;
import com.perspecta.luegimport.business.domain.document_wrapper.DocumentWrapperRepository;
import com.perspecta.luegimport.business.domain.user.User;
import com.perspecta.luegimport.business.service.file_import.delegate.FileImportDelegate;
import com.perspecta.luegimport.business.service.file_import.dto.DocumentErrorTypes;
import com.perspecta.luegimport.business.service.file_import.dto.DocumentView;
import com.perspecta.luegimport.business.service.file_import.util.DocumentCsvExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileImportService {

	private final FileImportDelegate fileImportDelegate;
	private final DocumentCsvExtractor documentCsvExtractor;
	private final DocumentWrapperRepository documentWrapperRepository;
	private final DocumentRepository documentRepository;

	public DocumentView process(InputStream csvInputStream){
		DocumentView documentView = new DocumentView();
		List<DocumentWrapper> successValidations = new ArrayList<>();
		Map<DocumentErrorTypes, List<DocumentWrapper>> failedValidations = new HashMap<>();
		// parse file
		List<DocumentWrapper> documentWrapperList = documentCsvExtractor.extract(csvInputStream);

		if(CollectionUtils.isNotEmpty(documentWrapperList)){
			// validate file fields
			fileImportDelegate.validateFields(documentWrapperList, successValidations, failedValidations);

			// check if file location is valid
//			fileImportDelegate.checkFilePath(documentWrapperList, successValidations, failedValidations);

			// validate to check if document is a duplicate
			fileImportDelegate.existingDocumentCheck(documentWrapperList, successValidations, failedValidations);

			// TODO: validate with the remote database if the cpId isnt valid
		}

		fileImportDelegate.persistValidations(documentView, successValidations, failedValidations);

		return documentView;
	}

	public void upload(User user, MultipartFile file){

		// process file
		// update database

	}




}
