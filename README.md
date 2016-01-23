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
Run
===================

<p>
The application is currently running in the cloud, hosted by the platform Digital Ocean
</p>
<p>
https://cloud.digitalocean.com/
</p>
<p>
It is deployed using Docker and all images can be built by Gradle and then uploaded to the repository
</p>
<p>
https://registry.hub.docker.com/u/mikechinaloy/taptwitterstreamj/tags/manage/
</p>
<p>
To run the application locally just download the image from Docker and then you can run it for example -
</p>
<p>
<b>docker run -d -p 8080:8080 mikechinaloy/taptwitterstreamj:1.0.3</b>
</o>
<p>
<b>Application URL</b>
</p>
<p>
http://178.62.41.152:8080/
</p>
