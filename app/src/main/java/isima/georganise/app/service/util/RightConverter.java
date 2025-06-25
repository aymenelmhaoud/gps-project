package isima.georganise.app.service.util;

import isima.georganise.app.entity.util.Right;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.NotNull;

@Converter(autoApply = true)
public class RightConverter implements AttributeConverter<Right, Character> {
    @Override
    public @NotNull Character convertToDatabaseColumn(@NotNull Right right) {
        return right.toString().charAt(0);
    }

    @Override
    public @NotNull Right convertToEntityAttribute(Character character) {
        return Right.valueOf(character == 'W' ? "WRITER" : "READER");
    }
}
