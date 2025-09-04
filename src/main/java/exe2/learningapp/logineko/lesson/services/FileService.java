package exe2.learningapp.logineko.lesson.services;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    Pair<String, String> uploadFile(MultipartFile file, String destination) throws IOException;

    void deleteFile(String publicId) throws IOException;
}
