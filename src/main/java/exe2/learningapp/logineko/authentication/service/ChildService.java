package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.child.ChildCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child.ChildDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing Child entities.
 * Provides CRUD operations and business logic for child management.
 *
 * @author MinhAlfred
 * @since 1.0
 */
public interface ChildService {

    ChildDto createChild(ChildCreateDto childCreateDto);

    ChildDto updateChild(Long id, ChildCreateDto childUpdateDto);

    ChildDto getChildById(Long childId);

    List<ChildDto> getChildrenByParentId(Long parentId);

    List<ChildDto> getAllChildren();

    List<ChildDto> getChildrenByAgeRange(int minAge, int maxAge);

    ChildDto updateChildImage(Long childId, String imageUrl);

    boolean existsById(Long childId);

    boolean existsByParentAndName(Long parentId, String childName);

    void deleteChild(Long childId);

    void deleteChildrenByParent(Long parentId);

}
