package org.pesc.service.dao;

import org.pesc.edexchange.v1_0.DeliveryOption;

import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface DeliveryOptionsDao extends DBDataSourceDao<DeliveryOption> {
	List<DeliveryOption> search(
			Integer id,
			Integer memberId,
			Integer formatId,
			String webserviceUrl,
			Integer deliveryMethodId,
			Boolean deliveryConfirm,
			Boolean error,
			String operationalStatus
	);
}
