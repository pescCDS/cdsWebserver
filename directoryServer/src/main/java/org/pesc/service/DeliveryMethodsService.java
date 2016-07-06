package org.pesc.service;

import org.pesc.api.model.DeliveryMethod;
import org.pesc.api.repository.DeliveryMethodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Service
public class DeliveryMethodsService {

    @Autowired
    private DeliveryMethodsRepository deliveryMethodsRepository;

    public Iterable<DeliveryMethod> getDeliveryMethods() {
        return deliveryMethodsRepository.findAll();
    }
}
