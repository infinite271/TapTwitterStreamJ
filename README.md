# taptwitterstreamj

TapTwitterStreamJ is a tweet streaming application that usies the Twitter Stream API to receive tweets. It has a number of features which include the ability filter tweets on specific keywords, aggregate hashtags and keep track of the occurence of keywords.

The project is written in Java 8 and attempts to leverage the following frameworks/technologies:
<br/>
<br/>
<b>Backend</b> - [Akka.io](http://akka.io/), [SpringBoot](http://projects.spring.io/spring-boot/), [Lombok](https://projectlombok.org/)
<br/>
<b>Frontend</b> - [FoundationJS](http://foundation.zurb.com/)
<br/>
<b>Deployment</b> - [Docker](https://www.docker.com/)
<br/>
<b>Build</b> - [Gradle](http://gradle.org/)
<br/>
===================
Running locally
===================
<p>
To run the application locally just install Docker and then enter the following:
</p>
```docker run -d -p 8080:8080 mikechinaloy/taptwitterstreamj:1.0.3``` 

===================
Running in the Cloud
===================

The application is currently running in the cloud, hosted by the platform [Digital Ocean](https://cloud.digitalocean.com/)
<br/>
<br/>
http://178.62.41.152:8080/
