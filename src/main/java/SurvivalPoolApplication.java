import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.nothingtothetable.survivalpool")
@MapperScan("com.nothingtothetable.survivalpool.repository")
public class SurvivalPoolApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SurvivalPoolApplication.class, args);
    }
}
