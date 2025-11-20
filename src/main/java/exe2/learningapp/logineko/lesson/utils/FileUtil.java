package exe2.learningapp.logineko.lesson.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mp4parser.IsoFile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileUtil {

    public long getDurationMp4(MultipartFile file) throws IOException {
        // Validate file type
        if (!"video/mp4".equalsIgnoreCase(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không phải là video MP4");
        }

        // Validate file is not empty
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File video không được rỗng");
        }

        IsoFile isoFile = null;
        ReadableByteChannel channel = null;

        try {
            // Convert InputStream to ReadableByteChannel
            channel = Channels.newChannel(file.getInputStream());
            isoFile = new IsoFile(channel);

            double lengthInSeconds =
                    (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                            isoFile.getMovieBox().getMovieHeaderBox().getTimescale();

            return (long) lengthInSeconds;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể đọc thông tin video MP4: " + e.getMessage()
            );
        } finally {
            // Ensure resources are properly closed
            if (isoFile != null) {
                try {
                    isoFile.close();
                } catch (Exception ignored) {
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
