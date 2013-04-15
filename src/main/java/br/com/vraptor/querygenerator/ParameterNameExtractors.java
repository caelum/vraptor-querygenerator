package br.com.vraptor.querygenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.vraptor.querygenerator.condition.EqualsCondition;
import br.com.vraptor.querygenerator.condition.LikeCondition;

public class ParameterNameExtractors {

	private final List<NameAndConditionExtractor> extractors = new ArrayList<>();

	public ParameterNameExtractors(NameAndConditionExtractor... extractors) {
		this.extractors.add(new LikeCondition());
		this.extractors.addAll(Arrays.asList(extractors));
	}

	public NameAndConditionExtractor getExtractorFor(String parameterKey) {
		for (NameAndConditionExtractor extractor : extractors) {
			if (extractor.canHandle(parameterKey))
				return extractor;
		}
		return new EqualsCondition();
	}

}
