package contract.func;

import contract.model.RedEnvelopeEntity;
import contract.util.Nuls;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.util.List;

public interface RedEnvelopeInterface {

    @Payable
    void newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark);

    Nuls snatchRedEnvelope(@Required Long id);

    @View
    RedEnvelopeEntity queryRedEnvelopeInfo(@Required Long id);

    @View
    List<String> allRedEnvelopeInfo();

}
