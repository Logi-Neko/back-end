package exe2.learningapp.logineko.authentication.dtos.account_character;

import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCharacterSearchRequest {
    private String characterName;
    private CharacterRarity characterRarity; // COMMON, RARE, EPIC, LEGENDARY
    private Boolean isFavorite;

    // Pagination
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "unlockedAt";

    @Builder.Default
    private String sortDir = "desc";
}
