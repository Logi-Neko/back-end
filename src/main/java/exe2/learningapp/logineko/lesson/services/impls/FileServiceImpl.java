package exe2.learningapp.logineko.lesson.services.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import exe2.learningapp.logineko.lesson.services.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    Cloudinary cloudinary;

    @Override
    public Pair<String, String> uploadFile(MultipartFile file, String destination) throws IOException {
        Map uploadResult = cloudinary
                .uploader()
                .upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "exe201" + destination,
                                "resource_type", "auto")
                );

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        return Pair.of(url, publicId);
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
