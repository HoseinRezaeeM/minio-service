import io.minio.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {

    private final Logger log = LoggerFactory.getLogger(MinioServiceImpl.class);

    private final MinioClient photosMinioClient;


    private final MinioConfigurationProperties minioProperties;

    private String photosBucket() {
        return minioProperties.getPhotosBucket();
    }

    @Autowired
    public MinioServiceImpl(MinioClient photosMinioClient,
                            MinioConfigurationProperties minioProperties) {
        this.photosMinioClient = photosMinioClient;
        this.minioProperties = minioProperties;
    }


    @Override
    public String upload(MinioClient minioClient, MultipartFile file, String bucketName, String objectName) {
        InputStream is = null;
        try {
            is = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .contentType(file.getContentType())
                    .stream(is, -1L, ObjectWriteArgs.MIN_MULTIPART_SIZE)
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new MinIOUploadException();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GetObjectResponse download(MinioClient minioClient, String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MinIODownloadException(e);
        }
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        try {
            return bucketName + File.separator + objectName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPhotoUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) return null;
        return getUrl(photosBucket(), objectName);
    }

    @Override
    public String uploadPhoto(MultipartFile file) {
        String newFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        return upload(photosMinioClient, file, photosBucket(), newFileName);
    }

    @Override
    public GetObjectResponse getPhoto(String objectName) {
        return download(photosMinioClient, photosBucket(), objectName);
    }

    @Override
    public void deletePhoto(String objectName) {
        try {
            photosMinioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(photosBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new MinIODownloadException(e);
        }
    }

  //  @Bean(name = "photosMinio")
    public MinioClient getPhotosMinioClient(){
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(minioProperties.getPhotosEndpoint())
                .credentials(minioProperties.getPhotosAccessKey(), minioProperties.getPhotosSecretKey())
                .build();

        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getPhotosBucket()).build());

            if(!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getPhotosBucket())
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minioClient;
    }
}
