package com.david.beerclient.client;

import com.david.beerclient.config.WebClientConfig;
import com.david.beerclient.dao.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {

        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void getBeerById() {
    }

    @Test
    void listBeers() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listBeersPageSize10() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 10, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(10);
    }

    @Test
    void listBeersEmpty() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(10, 20, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(0);
    }

    @Test
    void listWheatBeers() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, "WHEAT", true);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        beerPagedList.forEach(beer -> assertThat(beer.getBeerStyle().equals("WHEAT")));
        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listWheatBeers2Pages() {

        // First Page
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(0, 2, null, "WHEAT", true);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        beerPagedList.forEach(beer -> assertThat(beer.getBeerStyle().equals("WHEAT")));
        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(2);
        assertThat(beerPagedList.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(beerPagedList.getPageable().getPageSize()).isEqualTo(2);

        // Second Page
        beerPagedListMono = beerClient.listBeers(1, 2, null, "WHEAT", true);

        beerPagedList = beerPagedListMono.block();

        beerPagedList.forEach(beer -> assertThat(beer.getBeerStyle().equals("WHEAT")));
        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(2);
        assertThat(beerPagedList.getPageable().getPageNumber()).isEqualTo(1);
        assertThat(beerPagedList.getPageable().getPageSize()).isEqualTo(2);

        // Third Non-Existent Page
        beerPagedListMono = beerClient.listBeers(2, 2, null, "WHEAT", true);

        beerPagedList = beerPagedListMono.block();

        assertThat(CollectionUtils.isEmpty(beerPagedList.getContent()));
    }

    @Test
    void createBeer() {
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }

    @Test
    void getBeerByUpc() {
    }
}