import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nothingtothetable.survivalpool")
public class SurvivalPoolApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SurvivalPoolApplication.class, args);
    }
}
