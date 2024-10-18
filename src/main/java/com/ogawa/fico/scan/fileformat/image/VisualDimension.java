package com.ogawa.fico.scan.fileformat.image;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class VisualDimension {

    private final Integer width;
    private final Integer height;
    private final Integer depth;

    VisualDimension(Integer width, Integer height, Integer depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

}
