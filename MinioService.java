import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String upload(MinioClient minioClient, MultipartFile file, String bucketName, String objectName);

    GetObjectResponse download(MinioClient minioClient, String bucketName, String objectName);

    String getUrl(String bucketName, String objectName);

    String getPhotoUrl(String objectName);

    String uploadPhoto(MultipartFile file);

    GetObjectResponse getPhoto(String objectName);

    void deletePhoto(String objectName);
}
