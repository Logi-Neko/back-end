package exe2.learningapp.logineko.authentication.repository.specification;

import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import org.springframework.data.jpa.domain.Specification;

public class AccountCharacterSpecification {
    public static Specification<AccountCharacter> belongsToAccount(Long accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("account").get("id"), accountId);
        };
    }

    public static Specification<AccountCharacter> hasCharacterName(String characterName) {
        return (root, query, criteriaBuilder) -> {
            if (characterName == null || characterName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likeKeyword = "%" + characterName.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("character").get("name")),
                    likeKeyword
            );
        };
    }
    public static Specification<AccountCharacter> hasCharacterRarity(CharacterRarity rarity) {
        return (root, query, criteriaBuilder) -> {
            if (rarity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("character").get("rarity"), rarity);
        };
    }

    public static Specification<AccountCharacter> isFavorite(Boolean favorite) {
        return (root, query, criteriaBuilder) -> {
            if (favorite == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isFavorite"), favorite);
        };
    }

}
