package ch.scaille.tcwriter.server.webapi.v0.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.DictionaryApiController;
import ch.scaille.tcwriter.generated.api.model.v0.Metadata;
import ch.scaille.tcwriter.generated.api.model.v0.TestDictionary;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.webapi.v0.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.v0.mappers.TestDictionaryMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class DictionaryController extends DictionaryApiController {

	private final DictionaryFacade dictionaryFacade;

	public DictionaryController(DictionaryFacade dictionaryFacade,
			NativeWebRequest request) {
		super(request);
		this.dictionaryFacade = dictionaryFacade;
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return ResponseEntity.ok(dictionaryFacade.listAll().stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<TestDictionary> getDictionary(@Valid @NotNull String dictionary) {
		return ResponseEntity.ok(TestDictionaryMapper.MAPPER.convert(dictionaryFacade.load(dictionary)));
	}
}
