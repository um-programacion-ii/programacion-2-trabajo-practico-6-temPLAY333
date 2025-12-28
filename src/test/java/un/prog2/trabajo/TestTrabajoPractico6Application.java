package un.prog2.trabajo;

import org.springframework.boot.SpringApplication;

public class TestTrabajoPractico6Application {

	public static void main(String[] args) {
		SpringApplication.from(TrabajoPractico6Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
