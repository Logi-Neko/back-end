package exe2.learningapp.logineko.authentication.dtos.child;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ChildDto(
        Long id,
        String name,
        LocalDate birthDate,
        String gender,
        String imageUrl,
        Long parentId
) {}