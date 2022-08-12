package com.david.beerclient.client;

import com.david.beerclient.config.WebClientConfig;
import com.david.beerclient.dao.model.BeerDto;
import com.david.beerclient.dao.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {

        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void getBeerById() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 1, null, null, null);
        BeerDto beerToFind = beerPagedListMono.block().getContent().get(0);
        UUID beerToFindId = beerToFind.getId();
        String beerToFindName = beerToFind.getBeerName();
        String beerToFindStyle = beerToFind.getBeerStyle();

        Mono<BeerDto> beerMono = beerClient.getBeerById(beerToFindId, false);
        BeerDto beer = beerMono.block();

        assertThat(beer).isNotNull();
        assertThat(beer.getId().toString()).isEqualTo(beerToFindId.toString());
        assertThat(beer.getBeerName()).isEqualTo(beerToFindName);
        assertThat(beer.getBeerStyle()).isEqualTo(beerToFindStyle);
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
        beerPagedListMono = beerClient.listBeers(1, 2, null, "WHEAT", false);

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

        BeerDto beerDto = BeerDto.builder()
                .beerName("Zarudsky")
                .beerStyle("IPA")
                .upc("1122334455")
                .price(BigDecimal.valueOf(7.21))
                .quantityOnHand(null)
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);

        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {

        // Create beer and get id (last in list)
        createBeer();
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, 200, null, null, null);
        BeerDto createdBeer = beerPagedListMono.block().getContent().get(beerPagedListMono.block().getContent().size() - 1);
        UUID createdBeerId = createdBeer.getId();
        assertThat(createdBeer).isNotNull();
        assertThat(createdBeer.getBeerName()).isEqualTo("Zarudsky");
        assertThat(createdBeer.getBeerStyle()).isEqualTo("IPA");

        // Create update beer dto
        BeerDto beerToUpdate = BeerDto.builder()
                .beerStyle("WHEAT")
                .beerName(createdBeer.getBeerName())
                .price(createdBeer.getPrice())
                .upc(createdBeer.getUpc())
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(createdBeerId, beerToUpdate);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        BeerDto updatedBeer = beerPagedListMono.block().getContent().get(beerPagedListMono.block().getContent().size() - 1);
        assertThat(updatedBeer.getId().toString()).isEqualTo(createdBeerId.toString());
        assertThat(updatedBeer.getBeerStyle()).isEqualTo("WHEAT");
    }

    @Test
    void deleteBeerById() {

        // List all beers and find last
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, 200, null, null, null);
        List<BeerDto> beerDtoList = beerPagedListMono.block().getContent();
        BeerDto firstBeer = beerDtoList.stream().findFirst().get();
        UUID lastBeerId = firstBeer.getId();
        Integer beerDtoListSize = beerDtoList.size();

        // Delete first beer by uuid
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(lastBeerId);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Mono<BeerPagedList> beerPagedListMonoAfterDelete = beerClient.listBeers(null, 200, null, null, null);
        assertThat(beerPagedListMonoAfterDelete.block().getContent().size()).isEqualTo(beerDtoListSize - 1);

        // Try to delete again
        assertThrows(WebClientResponseException.class, () -> beerClient.deleteBeerById(lastBeerId).block());
    }

    @Test
    void getBeerByUpc() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 2, null, null, null);
        BeerDto beerToFind = beerPagedListMono.block().getContent().get(0);
        String beerToFindUpc = beerToFind.getUpc();
        UUID beerToFindId = beerToFind.getId();
        String beerToFindName = beerToFind.getBeerName();
        String beerToFindStyle = beerToFind.getBeerStyle();

        Mono<BeerDto> beerMono = beerClient.getBeerByUpc(beerToFindUpc);
        BeerDto beer = beerMono.block();
        assertThat(beer).isNotNull();
        assertThat(beer.getId().toString()).isEqualTo(beerToFindId.toString());
        assertThat(beer.getBeerName()).isEqualTo(beerToFindName);
        assertThat(beer.getBeerStyle()).isEqualTo(beerToFindStyle);
    }
}