package exe2.learningapp.logineko.lesson.repositories.specifications;

import exe2.learningapp.logineko.lesson.entities.Lesson;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LessonSpecifications {
    public static Specification<Lesson> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Lesson> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? null : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static Specification<Lesson> hasOrder(Long order) {
        return (root, query, cb) ->
                order == null ? null : cb.equal(root.get("order"), order);
    }

    public static Specification<Lesson> hasMinAge(Long minAge) {
        return (root, query, cb) ->
                minAge == null ? null : cb.greaterThanOrEqualTo(root.get("minAge"), minAge);
    }

    public static Specification<Lesson> hasMaxAge(Long maxAge) {
        return (root, query, cb) ->
                maxAge == null ? null : cb.lessThanOrEqualTo(root.get("maxAge"), maxAge);
    }

    public static Specification<Lesson> hasDifficultyLevel(Long difficultyLevel) {
        return (root, query, cb) ->
                difficultyLevel == null ? null : cb.equal(root.get("difficultyLevel"), difficultyLevel);
    }

    public static Specification<Lesson> hasDuration(Long duration) {
        return (root, query, cb) ->
                duration == null ? null : cb.equal(root.get("duration"), duration);
    }

    public static Specification<Lesson> isPremium(Boolean isPremium) {
        return (root, query, cb) ->
                isPremium == null ? null : cb.equal(root.get("isPremium"), isPremium);
    }

    public static Specification<Lesson> createdAfter(LocalDateTime createdAfter) {
        return (root, query, cb) ->
                createdAfter == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter);
    }

    public static Specification<Lesson> createdBefore(LocalDateTime createdBefore) {
        return (root, query, cb) ->
                createdBefore == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore);
    }
}
