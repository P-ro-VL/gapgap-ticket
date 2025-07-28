package vn.hoangshitposting.gapgapticket.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;

@Converter(autoApply = true)
public class TimestampToLongConverter implements AttributeConverter<Long, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Long attribute) {
        return attribute != null ? new Timestamp(attribute) : null;
    }

    @Override
    public Long convertToEntityAttribute(Timestamp dbData) {
        return dbData != null ? dbData.getTime() : null;
    }
}