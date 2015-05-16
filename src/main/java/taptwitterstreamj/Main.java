package taptwitterstreamj;

import taptwitterstreamj.aspects.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String args[]){
        SpringApplication.run(Main.class, args);
    }

    @Log
    @Override
    public void run(String... args) throws Exception {
    }
}
