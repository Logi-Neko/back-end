package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.child.ChildCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child.ChildDto;
import exe2.learningapp.logineko.authentication.entity.Child;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.ChildRepository;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.mapper.ChildMapper;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final AccountRepository accountRepository;
    private final ChildMapper childMapper;

    @Override
    public ChildDto createChild(ChildCreateDto childCreateDto) {
        log.info("Creating child with name: {} for parent ID: {}",
                childCreateDto.name(), childCreateDto.parentId());

        // Validate parent exists
        Account parent = accountRepository.findById(childCreateDto.parentId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        // Check if child name already exists for this parent
        if (childRepository.existsByParentIdAndName(childCreateDto.parentId(), childCreateDto.name())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        // Validate business rules
        validateChildData(childCreateDto);

        Child child = childMapper.toEntity(childCreateDto);
        child.setParent(parent);

        Child saved = childRepository.save(child);
        log.info("Created child with ID: {} and name: {}", saved.getId(), saved.getName());

        return childMapper.toDto(saved);
    }

    @Override
    public ChildDto updateChild(Long id, ChildCreateDto childUpdateDto) {
        log.info("Updating child with ID: {}", id);

        Child child = childRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        // Check if new name conflicts with existing child (excluding current one)
        if (!child.getName().equals(childUpdateDto.name()) &&
                childRepository.existsByParentIdAndName(child.getParent().getId(), childUpdateDto.name())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        // Update child fields
        child.setName(childUpdateDto.name());
        child.setBirthDate(childUpdateDto.birthDate());
        child.setGender(childUpdateDto.gender());
        if (childUpdateDto.imageUrl() != null) {
            child.setImageUrl(childUpdateDto.imageUrl());
        }

        Child updated = childRepository.save(child);
        log.info("Updated child with ID: {} and name: {}", updated.getId(), updated.getName());

        return childMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildDto getChildById(Long childId) {
        log.info("Getting child by ID: {}", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return childMapper.toDto(child);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildDto> getChildrenByParentId(Long parentId) {
        log.info("Getting children for parent ID: {}", parentId);

        // Validate parent exists
        if (!accountRepository.existsById(parentId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<Child> children = childRepository.findByParentId(parentId);
        return children.stream()
                .map(childMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildDto> getAllChildren() {
        log.info("Getting all children");

        List<Child> children = childRepository.findAll();
        return children.stream()
                .map(childMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildDto> getChildrenByAgeRange(int minAge, int maxAge) {
        log.info("Getting children by age range: {} to {}", minAge, maxAge);

        LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);
        LocalDate minBirthDate = LocalDate.now().minusYears(maxAge + 1);

        List<Child> children = childRepository.findByBirthDateBetween(minBirthDate, maxBirthDate);
        return children.stream()
                .map(childMapper::toDto)
                .toList();
    }

    @Override
    public ChildDto updateChildImage(Long childId, String imageUrl) {
        log.info("Updating image for child ID: {}", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        child.setImageUrl(imageUrl);
        Child updated = childRepository.save(child);

        log.info("Updated image for child ID: {}", updated.getId());
        return childMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long childId) {
        log.debug("Checking if child exists by ID: {}", childId);
        return childRepository.existsById(childId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByParentAndName(Long parentId, String childName) {
        log.debug("Checking if child exists by parent ID: {} and name: {}", parentId, childName);
        return childRepository.existsByParentIdAndName(parentId, childName);
    }

    @Override
    public void deleteChild(Long childId) {
        log.info("Deleting child with ID: {}", childId);

        if (!childRepository.existsById(childId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        childRepository.deleteById(childId);
        log.info("Deleted child with ID: {}", childId);
    }

    @Override
    public void deleteChildrenByParent(Long parentId) {
        log.info("Deleting all children for parent ID: {}", parentId);

        // Validate parent exists
        if (!accountRepository.existsById(parentId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        int deletedCount = childRepository.deleteByParentId(parentId);
        log.info("Deleted {} children for parent ID: {}", deletedCount, parentId);
    }

    /**
     * Validates child data according to business rules.
     */
    private void validateChildData(ChildCreateDto childCreateDto) {
        // Validate age (child should be between 0-18 years old)
        LocalDate birthDate = childCreateDto.birthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 0 || age > 18) {
            throw new AppException(ErrorCode.ERR_INVALID_INPUT);
        }

        // Validate gender
        String gender = childCreateDto.gender().toUpperCase();
        if (!List.of("MALE", "FEMALE", "OTHER").contains(gender)) {
            throw new AppException(ErrorCode.ERR_INVALID_INPUT);
        }
    }
}