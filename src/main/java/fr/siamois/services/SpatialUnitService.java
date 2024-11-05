package fr.siamois.services;

import fr.siamois.models.SpatialUnit;
import fr.siamois.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpatialUnitService {

    @Autowired SpatialUnitRepository spatialUnitRepository;

    public List<SpatialUnit> findAllWithoutParents() {
        return spatialUnitRepository.findAllWithoutParents() ;
    }

    public List<SpatialUnit> findAllChildOfSpatialUnit (SpatialUnit spatialUnit) {

        return spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit.getId()) ;
    }

    public Optional<SpatialUnit> findById(int id) {
        return spatialUnitRepository.findById(id) ;
    }

    public Optional<SpatialUnit> findChildren(int id) {
        return spatialUnitRepository.findById(id) ;
    }

    // log.info() etc...
}
