package taptwitterstreamj.extensions;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static taptwitterstreamj.extensions.SpringExtension.SpringExtProvider;

/**
 * The application configuration.
 */
@Configuration
class AppConfiguration {

  // the application context is needed to initialize the Akka Spring Extension
  @Inject
  private ApplicationContext applicationContext;

  /**
   * Actor system singleton for this application.
   */
  @Bean
  public ActorSystem actorSystem() {
    ActorSystem system = ActorSystem.create("AkkaJavaSpring");
    // initialize the application context in the Akka Spring Extension
    SpringExtProvider.get(system).initialize(applicationContext);
    return system;
  }
}
