package ch.scaille.tcwriter.server.webapi.controllers;

import static ch.scaille.tcwriter.server.webapi.controllers.exceptions.ValidationHelper.validateDictionarySet;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.DictionaryApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestDictionary;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestDictionaryMapper;

public class DictionaryController extends DictionaryApiController {

	private final ContextFacade contextService;

	private final DictionaryFacade dictionaryFacade;

	public DictionaryController(ContextFacade contextService, DictionaryFacade dictionaryFacade,
			NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryFacade = dictionaryFacade;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return ResponseEntity.ok(dictionaryFacade.listAll().stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Override
	public ResponseEntity<TestDictionary> current() {
		return ResponseEntity.ok(TestDictionaryMapper.MAPPER
				.convert(dictionaryFacade.load(validateDictionarySet(contextService.get().getDictionary()))));
	}
}
