package contract.event;

import contract.model.RedEnvelopeEntity;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;

public class NewRedEnvelopeEvent implements Event {

    private Long createTime;
    private Address buyer;
    private RedEnvelopeEntity pixelEntity;

    public NewRedEnvelopeEvent(Long createTime, Address buyer, RedEnvelopeEntity pixelEntity) {
        this.createTime = createTime;
        this.buyer = buyer;
        this.pixelEntity = pixelEntity;
    }


}
