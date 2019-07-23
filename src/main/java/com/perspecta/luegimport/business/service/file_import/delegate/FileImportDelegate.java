package com.perspecta.luegimport.business.service.file_import.delegate;

import com.perspecta.luegimport.business.domain.document.Document;
import com.perspecta.luegimport.business.domain.document_wrapper.DocumentWrapper;
import com.perspecta.luegimport.business.service.file_import.dto.DocumentErrorTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileImportDelegate {

	public List<DocumentWrapper> parseFile(String userName, MultipartFile file) {

		List<DocumentWrapper> documentWrapperList = new ArrayList<>();

		try {
			InputStream inputFileStream = file.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream));
			documentWrapperList = br.lines().skip(1).map(line -> mapToDocumentWrapper(line, userName)).collect(Collectors.toList());
			br.close();
		} catch (Exception ex) {
			log.error("Error processing the file: " + ex);
		}

		return documentWrapperList;
	}

	public void validate(List<DocumentWrapper> documentWrapperList) {
		List<DocumentWrapper> successvalidations = new ArrayList<>();
		Map<DocumentErrorTypes, List<DocumentWrapper>> failedValidations = new HashMap<>();

		documentWrapperList
				.forEach(documentWrapper -> {
					if (StringUtils.isBlank(documentWrapper.getDocument().getObjectName())
							|| StringUtils.isBlank(documentWrapper.getDocument().getFileLocation())
							|| StringUtils.isBlank(documentWrapper.getDocument().getCpId())) {
						documentWrapper.setIsValidated(false);
						if (MapUtils.isNotEmpty(failedValidations) && CollectionUtils.isNotEmpty(failedValidations.get(DocumentErrorTypes.MISSING_FIELD))) {
							failedValidations.get(DocumentErrorTypes.MISSING_FIELD).add(documentWrapper);
						} else {
							failedValidations.put(DocumentErrorTypes.MISSING_FIELD, new ArrayList<>(Arrays.asList(documentWrapper)));
						}
					}
				});
	// TODO: validate with the local database if the cpId exists
	// TODO: validate with the remote database if the cpId isnt valid
}

	private DocumentWrapper mapToDocumentWrapper(String line, String userName) {
		String[] tokens = line.split(",");// a CSV has comma separated lines
		DocumentWrapper documentWrapper = new DocumentWrapper();
		Document document = new Document();

		if (StringUtils.isNotBlank(tokens[0])) {
			document.setObjectType(StringUtils.trimToEmpty(tokens[0]));
		}

		if (StringUtils.isNotBlank(tokens[1])) {
			document.setObjectName(StringUtils.trimToEmpty(tokens[1]));
		}

		if (StringUtils.isNotBlank(tokens[2])) {
			document.setFileLocation(StringUtils.trimToEmpty(tokens[2]));
		}

		if (StringUtils.isNotBlank(tokens[3])) {
			document.setCpId(StringUtils.trimToEmpty(tokens[3]));
		}

		if (StringUtils.isNotBlank(tokens[4])) {
			document.setSubType(StringUtils.trimToEmpty(tokens[4]));
		}

		if (StringUtils.isNotBlank(tokens[5])) {
			document.setYear(StringUtils.trimToEmpty(tokens[5]));
		}

		documentWrapper.setDocument(document);
		documentWrapper.setUserName(userName);

		return documentWrapper;
	}

}