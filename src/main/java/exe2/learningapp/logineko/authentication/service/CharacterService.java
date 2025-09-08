package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;

import java.util.List;

public interface CharacterService {
    CharacterDto createCharacter(String username, String characterName);
    CharacterDto deleteCharacter(String username, String characterName);
    CharacterDto updateCharacter(String username, String characterName, String newCharacterName);
    CharacterDto getCharacter(String username);
    List<CharacterDto> getAllCharacters();
}
