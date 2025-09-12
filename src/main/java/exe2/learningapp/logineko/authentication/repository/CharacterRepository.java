package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long>, JpaSpecificationExecutor<Character> {

    boolean existsByName(String name);

    List<Character> findByRarity(CharacterRarity rarity);

    List<Character> findByNameContainingIgnoreCase(String keyword);

}