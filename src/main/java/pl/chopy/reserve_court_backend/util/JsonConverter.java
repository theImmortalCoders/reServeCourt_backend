package pl.chopy.reserve_court_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Converter
public class JsonConverter implements AttributeConverter<List<String>, String> {

	private final static ObjectMapper objectMapper = new ObjectMapper();
	private final TypeReference<List<String>> listTypeRef = new TypeReference<>() {
	};

	@Override
	public String convertToDatabaseColumn(List<String> data) {
		if (null == data) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException ex) {
			return null;
		}
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (null == dbData || dbData.isEmpty()) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(dbData, listTypeRef);
		} catch (IOException ex) {
			return null;
		}
	}
}
