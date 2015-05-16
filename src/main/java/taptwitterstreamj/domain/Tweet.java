package taptwitterstreamj.domain;

import lombok.Data;

@Data
public class Tweet {

    private final String username;
    private final String location;
    private final String content;

}
