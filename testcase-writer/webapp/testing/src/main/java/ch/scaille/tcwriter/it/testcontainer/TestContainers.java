package ch.scaille.tcwriter.it.testcontainer;

import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class TestContainers {

	public static GenericContainer<?> app(String imageName) {
		return new GenericContainer<>(DockerImageName.parse(imageName)).withExposedPorts(9000)
				.withStartupTimeout(Duration.ofSeconds(2)).waitingFor(
						Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(30)));
	}

}
