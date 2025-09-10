package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.character.CharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;

import java.util.List;

public interface CharacterService {
    CharacterDto createCharacter(CharacterCreateDto characterCreateDto);
    void deleteCharacter(Long characterId);
    CharacterDto updateCharacter(Long id, CharacterCreateDto characterCreateDto);
    CharacterDto getCharacter(Long characterId);
    List<CharacterDto> getAllCharacters();
    CharacterDto deactivateCharacter(Long characterId);
    List<CharacterDto> getCharactersByRarity(CharacterRarity rarity);
    List<CharacterDto> searchCharactersByName(String keyword);
}