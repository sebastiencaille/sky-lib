package ch.scaille.tcwriter.server.webapi.v0.controllers;

import static ch.scaille.tcwriter.server.facade.ValidationHelper.validateDictionarySet;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.DictionaryApiController;
import ch.scaille.tcwriter.generated.api.model.v0.Metadata;
import ch.scaille.tcwriter.generated.api.model.v0.TestDictionary;
import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import ch.scaille.tcwriter.server.webapi.v0.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.v0.mappers.TestDictionaryMapper;

public class DictionaryController extends DictionaryApiController {

	private final DictionaryFacade dictionaryFacade;

	private final Context context;

	public DictionaryController(Context context, DictionaryFacade dictionaryFacade, NativeWebRequest request) {
		super(request);
		this.dictionaryFacade = dictionaryFacade;
		this.context = context;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return ResponseEntity.ok(dictionaryFacade.listAll().stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Override
	public ResponseEntity<TestDictionary> current() {
		return ResponseEntity.ok(TestDictionaryMapper.MAPPER
				.convert(dictionaryFacade.load(validateDictionarySet(context.getDictionary()))));
	}
}
