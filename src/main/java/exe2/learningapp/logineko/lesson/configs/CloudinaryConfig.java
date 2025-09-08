package exe2.learningapp.logineko.lesson.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dr58bpto1",
                "api_key", "217829992936926",
                "api_secret", "YgeUDzblHMvTc1cb7BB1j3vmqMw"));
    }
}
