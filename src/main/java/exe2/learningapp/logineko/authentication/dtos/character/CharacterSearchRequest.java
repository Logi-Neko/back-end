package exe2.learningapp.logineko.authentication.dtos.character;

import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import lombok.Data;

@Data
public class CharacterSearchRequest {
    private String keyword;
    private CharacterRarity rarity; // COMMON, RARE, EPIC, LEGENDARY
    private Boolean isPremium;
    private Boolean isActive;
    private Integer minStars;
    private Integer maxStars;

    // Pagination parameters
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "asc"; // asc hoặc desc
}
