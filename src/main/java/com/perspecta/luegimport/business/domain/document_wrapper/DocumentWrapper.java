package com.perspecta.luegimport.business.domain.document_wrapper;

import com.perspecta.luegimport.business.domain.document.Document;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
public class DocumentWrapper {

	@Id
	@GeneratedValue
	@Column(name = "documentWrapperId")
	private Long id;

	@OneToOne
	@JoinColumn(name = "documentId")
	private Document document;

	private Boolean validated;
	private Boolean locked;
	private Boolean processed;

	private UUID batchId;
}
