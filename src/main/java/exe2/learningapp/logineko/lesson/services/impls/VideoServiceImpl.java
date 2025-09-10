package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.lesson.dtos.requests.VideoRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoDTO;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.repositories.LessonRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoRepository;
import exe2.learningapp.logineko.lesson.services.FileService;
import exe2.learningapp.logineko.lesson.services.VideoService;
import exe2.learningapp.logineko.lesson.utils.FileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    VideoRepository videoRepository;
    LessonRepository lessonRepository;
    FileService fileService;
    FileUtil fileUtil;

    @Override
    public VideoDTO create(VideoRequest request, MultipartFile thumbnail, MultipartFile video) {
        Video videoEntity = Video
                .builder()
                .title(request.getTitle())
                .order(request.getOrder())
                .type(request.getType())
                .isActive(request.getIsActive())
                .build();

        videoRepository.save(videoEntity);

        Pair<String, String> thumbnailData;
        try {
            thumbnailData = fileService.uploadFile(thumbnail, "/" + videoEntity.getId());

            videoEntity.setThumbnailUrl(thumbnailData.getFirst());
            videoEntity.setThumbnailPublicId(thumbnailData.getSecond());
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Có lỗi trong quá trình tải thumbnail"
            );
        }

        Pair<String, String> videoData;
        try {
            videoData = fileService.uploadFile(video, "/" + videoEntity.getId());

            videoEntity.setVideoUrl(videoData.getFirst());
            videoEntity.setVideoPublicId(videoData.getSecond());
            videoEntity.setDuration(fileUtil.getDurationMp4(video));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Có lỗi trong quá trình tải video"
            );
        }

        videoRepository.save(videoEntity);

        return convertToDTO(videoEntity);
    }

    @Override
    public VideoDTO update(Long id, VideoRequest request, MultipartFile thumbnail, MultipartFile video) {
        Video videoEntity = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy video"));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài học"));

        videoEntity.setTitle(request.getTitle());
        videoEntity.setOrder(request.getOrder());
        videoEntity.setType(request.getType());
        videoEntity.setIsActive(request.getIsActive());
        videoEntity.setLesson(lesson);

        String oldThumbnailPublicId = videoEntity.getThumbnailPublicId();
        String oldVideoPublicId = videoEntity.getVideoPublicId();

        if (thumbnail != null) {
            Pair<String, String> thumbnailData;
            try {
                thumbnailData = fileService.uploadFile(thumbnail, "/" + videoEntity.getId());

                videoEntity.setThumbnailUrl(thumbnailData.getFirst());
                videoEntity.setThumbnailPublicId(thumbnailData.getSecond());
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Có lỗi trong quá trình tải thumbnail"
                );
            }
        }

        if (video != null) {
            Pair<String, String> videoData;
            try {
                videoData = fileService.uploadFile(video, "/" + videoEntity.getId());

                videoEntity.setVideoUrl(videoData.getFirst());
                videoEntity.setVideoPublicId(videoData.getSecond());
                videoEntity.setDuration(fileUtil.getDurationMp4(video));
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Có lỗi trong quá trình tải video"
                );
            }
        }

        if (thumbnail != null) {
            try {
                fileService.deleteFile(oldThumbnailPublicId);
            } catch (IOException ignored) {
            }
        }

        if (video != null) {
            try {
                fileService.deleteFile(oldVideoPublicId);
            } catch (IOException ignored) {
            }
        }

        return convertToDTO(videoEntity);
    }

    @Override
    public void delete(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy video"));

        String thumbnailPublicId = video.getThumbnailPublicId();
        String videoPublicId = video.getVideoPublicId();

        videoRepository.delete(video);

        try {
            fileService.deleteFile(thumbnailPublicId);
            fileService.deleteFile(videoPublicId);
        } catch (IOException ignored) {
        }
    }

    @Override
    public List<VideoDTO> findByLessonId(Long lessonId) {
        return videoRepository.findByLesson_Id(lessonId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public VideoDTO convertToDTO(Video video) {
        return VideoDTO
                .builder()
                .id(video.getId())
                .title(video.getTitle())
                .videoUrl(video.getVideoUrl())
                .videoPublicId(video.getVideoPublicId())
                .thumbnailUrl(video.getThumbnailUrl())
                .thumbnailPublicId(video.getThumbnailPublicId())
                .duration(video.getDuration())
                .order(video.getOrder())
                .videoType(video.getType())
                .isActive(video.getIsActive())
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }
}
