package fr.siamois.infrastructure.database.repositories;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SpatialUnitRepositoryIntegrationTest {

    @Autowired
    private SpatialUnitRepository spatialUnitRepository;

    @Test
    void findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining() {

        SpatialUnit su = new SpatialUnit();
        su.setName("A");

        Pageable p = PageRequest.of(0, 10);

        Page<SpatialUnit> res = spatialUnitRepository.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                1L, "A", new Long[3], new Long[3], "", "fr", p
        );

        assertEquals(res.getContent().get(0),su);
    }
}



