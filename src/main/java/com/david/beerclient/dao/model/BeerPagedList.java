package com.david.beerclient.dao.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

public class BeerPagedList extends PageImpl<BeerDto> implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerPagedList(@JsonProperty("content") List<BeerDto> content,
                         @JsonProperty("pageable") Pageable pageable,
                         @JsonProperty("totalPages") Long total,
                         @JsonProperty("last") Boolean last,
                         @JsonProperty("totalElements") Integer totalElements,
                         @JsonProperty("size") Integer size,
                         @JsonProperty("number") Integer number,
                         @JsonProperty("numberOfElements") Integer numberOfElements,
                         @JsonProperty("sort") Sort sort,
                         @JsonProperty("first") Boolean first) {

        super(content, pageable, total);
    }

    public BeerPagedList(List<BeerDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public BeerPagedList(List<BeerDto> content) {
        super(content);
    }
}
