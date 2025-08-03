package vn.hoangshitposting.gapgapticket.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.hoangshitposting.gapgapticket.model.merch.MerchMeta;

@Converter
public class MerchMetaConverter implements AttributeConverter<MerchMeta, String> {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(MerchMeta address) {
            try {
                return objectMapper.writeValueAsString(address);
            } catch (JsonProcessingException jpe) {
                return null;
            }
        }

        @Override
        public MerchMeta convertToEntityAttribute(String value) {
            try {
                return objectMapper.readValue(value, MerchMeta.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
}
