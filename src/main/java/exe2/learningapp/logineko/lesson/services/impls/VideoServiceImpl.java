package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.requests.VideoRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoDTO;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.entities.VideoQuestion;
import exe2.learningapp.logineko.lesson.repositories.LessonRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoQuestionRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoRepository;
import exe2.learningapp.logineko.lesson.services.FileService;
import exe2.learningapp.logineko.lesson.services.VideoQuestionService;
import exe2.learningapp.logineko.lesson.services.VideoService;
import exe2.learningapp.logineko.lesson.utils.FileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    VideoRepository videoRepository;
    VideoQuestionRepository videoQuestionRepository;
    LessonRepository lessonRepository;
    FileService fileService;
    FileUtil fileUtil;
    VideoQuestionService videoQuestionService;

    @Override
    @Transactional
    public VideoDTO create(VideoRequest request, MultipartFile thumbnail, MultipartFile video) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        VideoQuestion videoQuestion = VideoQuestion.builder()
                .question(request.getQuestion())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .answer(request.getAnswer())
                .build();

        Video videoEntity = Video
                .builder()
                .title(request.getTitle())
                .index(request.getOrder())
                .isActive(request.getIsActive())
                .videoQuestion(videoQuestion)
                .lesson(lesson)
                .build();

        videoQuestionRepository.save(videoQuestion);
        videoRepository.save(videoEntity);

        Pair<String, String> thumbnailData;
        try {
            thumbnailData = fileService.uploadFile(thumbnail, "/" + videoEntity.getId());

            videoEntity.setThumbnailUrl(thumbnailData.getFirst());
            videoEntity.setThumbnailPublicId(thumbnailData.getSecond());
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }

        Pair<String, String> videoData;
        try {
            videoData = fileService.uploadFile(video, "/" + videoEntity.getId());

            videoEntity.setVideoUrl(videoData.getFirst());
            videoEntity.setVideoPublicId(videoData.getSecond());
            videoEntity.setDuration(fileUtil.getDurationMp4(video));
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }

        videoRepository.save(videoEntity);

        lesson.setTotalVideo(lesson.getTotalVideo() + 1);
        lessonRepository.save(lesson);

        return convertToDTO(videoEntity);
    }

    @Override
    @Transactional
    public VideoDTO update(Long id, VideoRequest request, MultipartFile thumbnail, MultipartFile video) {
        Video videoEntity = videoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        videoEntity.setTitle(request.getTitle());
        videoEntity.setIndex(request.getOrder());
        videoEntity.setIsActive(request.getIsActive());
        videoEntity.setLesson(lesson);

        VideoQuestion videoQuestion = videoEntity.getVideoQuestion();
        videoQuestion.setQuestion(request.getQuestion());
        videoQuestion.setOptionA(request.getOptionA());
        videoQuestion.setOptionB(request.getOptionB());
        videoQuestion.setOptionC(request.getOptionC());
        videoQuestion.setOptionD(request.getOptionD());
        videoQuestion.setAnswer(request.getAnswer());
        videoQuestionRepository.save(videoQuestion);

        videoEntity.setVideoQuestion(videoQuestion);

        String oldThumbnailPublicId = videoEntity.getThumbnailPublicId();
        String oldVideoPublicId = videoEntity.getVideoPublicId();

        if (thumbnail != null) {
            Pair<String, String> thumbnailData;
            try {
                thumbnailData = fileService.uploadFile(thumbnail, "/" + videoEntity.getId());

                videoEntity.setThumbnailUrl(thumbnailData.getFirst());
                videoEntity.setThumbnailPublicId(thumbnailData.getSecond());
            } catch (IOException e) {
                throw new AppException(ErrorCode.ERR_SERVER_ERROR);
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
                throw new AppException(ErrorCode.ERR_SERVER_ERROR);
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

        videoRepository.save(videoEntity);

        return convertToDTO(videoEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        String thumbnailPublicId = video.getThumbnailPublicId();
        String videoPublicId = video.getVideoPublicId();

        videoRepository.delete(video);

        Lesson lesson = video.getLesson();
        lesson.setTotalVideo(lesson.getTotalVideo() - 1);
        lessonRepository.save(lesson);

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
                .order(video.getIndex())
                .isActive(video.getIsActive())
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .videoQuestion(videoQuestionService.convertToVideoQuestionDTO(video.getVideoQuestion()))
                .build();
    }
}
