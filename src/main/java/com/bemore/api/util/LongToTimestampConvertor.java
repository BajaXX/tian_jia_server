package com.bemore.api.util;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

public class LongToTimestampConvertor implements AttributeConverter<Long, Timestamp> {

    private static final long STS = LocalDate.parse("1900-01-01").atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

    @Override
    public Timestamp convertToDatabaseColumn(Long ts) {
        if (ts < STS) {
            ts = STS;
        }
        return Timestamp.from(Instant.ofEpochMilli(ts));
    }

    @Override
    public Long convertToEntityAttribute(Timestamp ts) {
        long t = ts.getTime();
        return t > STS ? t : 0;
    }

}
