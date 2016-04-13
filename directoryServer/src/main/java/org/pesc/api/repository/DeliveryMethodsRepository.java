package org.pesc.api.repository;

import org.pesc.api.model.DeliveryMethod;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by james on 4/6/16.
 */
@Repository
public interface DeliveryMethodsRepository extends CrudRepository<DeliveryMethod, Integer> {
}
