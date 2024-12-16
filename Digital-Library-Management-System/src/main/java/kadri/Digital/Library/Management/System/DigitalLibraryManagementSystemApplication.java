package kadri.Digital.Library.Management.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@EnableCaching
@SpringBootApplication
public class DigitalLibraryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalLibraryManagementSystemApplication.class, args);
	}

}
