
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "com.si.uaa.minio")
@Getter
@Setter
@NoArgsConstructor
@Validated
public class MinioConfigurationProperties {
    @NotNull private String photosEndpoint;
    @NotNull private String photosAccessKey;
    @NotNull private String photosSecretKey;
    @NotNull private String photosBucket;
    @NotNull private String photosBaseUrl;

}
