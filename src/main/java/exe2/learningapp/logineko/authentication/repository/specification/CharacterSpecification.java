package exe2.learningapp.logineko.authentication.repository.specification;

import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import org.springframework.data.jpa.domain.Specification;

public class CharacterSpecification {
    public static Specification<Character> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likeKeyword),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likeKeyword)
            );
        };
    }

    public static Specification<Character> hasRarity(CharacterRarity rarity) {
        return (root, query, criteriaBuilder) -> {
            if (rarity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("rarity"), rarity);
        };
    }

    public static Specification<Character> isPremium(Boolean isPremium) {
        return (root, query, criteriaBuilder) -> {
            if (isPremium == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isPremium"), isPremium);
        };
    }

    public static Specification<Character> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<Character> starRequiredBetween(Integer minStars, Integer maxStars) {
        return (root, query, criteriaBuilder) -> {
            if (minStars == null && maxStars == null) {
                return criteriaBuilder.conjunction();
            }
            if (minStars != null && maxStars != null) {
                return criteriaBuilder.between(root.get("starRequired"), minStars, maxStars);
            }
            if (minStars != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("starRequired"), minStars);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("starRequired"), maxStars);
        };
    }
}
