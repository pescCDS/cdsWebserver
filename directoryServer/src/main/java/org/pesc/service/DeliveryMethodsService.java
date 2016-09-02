package org.pesc.service;

import org.pesc.api.model.DeliveryMethod;
import org.pesc.api.repository.DeliveryMethodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DeliveryMethod create(DeliveryMethod deliveryMethod) {
        return deliveryMethodsRepository.save(deliveryMethod);

    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.deliveryMethodsRepository.delete(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DeliveryMethod update(DeliveryMethod deliveryMethod) {
        return deliveryMethodsRepository.save(deliveryMethod);

    }

}
