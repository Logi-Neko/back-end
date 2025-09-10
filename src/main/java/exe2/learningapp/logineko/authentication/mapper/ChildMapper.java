package exe2.learningapp.logineko.authentication.mapper;

import exe2.learningapp.logineko.authentication.dtos.child.ChildCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child.ChildDto;
import exe2.learningapp.logineko.authentication.entity.Child;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildMapper {
    public ChildDto toDto(Child child) {
        if (child == null) {
            return null;
        }

        return ChildDto.builder()
                .id(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .imageUrl(child.getImageUrl())
                .parentId(child.getParent() != null ? child.getParent().getId() : null)
                .build();
    }

    public Child toEntity(ChildCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Child.ChildBuilder builder = Child.builder()
                .name(dto.name())
                .birthDate(dto.birthDate())
                .gender(dto.gender())
                .imageUrl(dto.imageUrl());

        // Note: Parent entity should be set separately in service layer
        return builder.build();
    }
}