package taptwitterstreamj.domain;

import lombok.Data;

@Data
public class Tweet {

    private final String screenName;
    private final String location;
    private final String content;

}
