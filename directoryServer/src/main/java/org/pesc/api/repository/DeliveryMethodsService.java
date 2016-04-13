package org.pesc.api.repository;

import org.pesc.api.model.DeliveryMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by james on 4/6/16.
 */
@Service
public class DeliveryMethodsService {

    @Autowired
    private DeliveryMethodsRepository deliveryMethodsRepository;

    public Iterable<DeliveryMethod> getDeliveryMethods() {
        return deliveryMethodsRepository.findAll();
    }
}
