package exe2.learningapp.logineko.lesson.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mp4parser.IsoFile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileUtil {

    public long getDurationMp4(MultipartFile file) throws IOException {
        if (!"video/mp4".equalsIgnoreCase(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không phải là video MP4");
        }

        File tempFile = File.createTempFile("video", ".mp4");
        file.transferTo(tempFile);

        IsoFile isoFile = new IsoFile(tempFile.getAbsolutePath());
        double lengthInSeconds =
                (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                        isoFile.getMovieBox().getMovieHeaderBox().getTimescale();

        isoFile.close();
        tempFile.delete();

        return (long) lengthInSeconds;
    }
}
