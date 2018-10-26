package contract;

import contract.event.SnatchRedEnvelopeEvent;
import contract.func.RedEnvelopeInterface;
import contract.model.RedEnvelopeEntity;
import contract.util.Nuls;
import contract.util.RedEnvelopeManager;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.require;

/**
 * a simple nuls-smart-contract implements function of RedEnvelope
 * @author ad19900913@outlook.com
 */
public class RedEnvelopeContract implements Contract, RedEnvelopeInterface {

    private Map<Long, RedEnvelopeEntity> map;
    private Long count = 1L;

    public RedEnvelopeContract() {
        map = new HashMap<Long, RedEnvelopeEntity>();
    }

    @Payable
    @Override
    public void newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark) {
        require(parts > 0, "parts must larger than 0");
        Long id = count++;
        RedEnvelopeEntity entity = new RedEnvelopeEntity();
        entity.setAmount(Nuls.valueOf(Msg.value().longValue()));
        entity.setBalance(entity.getAmount());
        entity.setId(id);
        entity.setParts(parts);
        entity.setMap(new HashMap<Address, SnatchRedEnvelopeEvent>(parts));
        entity.setRandom(random);
        if (remark != null && !remark.trim().equals("")) {
            entity.setRemark(remark);
        }
        entity.setSponsor(Msg.sender());
        map.put(id, entity);
    }

    @Override
    public Nuls snatchRedEnvelope(@Required Long id) {
        RedEnvelopeEntity entity = map.get(id);
        require(entity != null, "The specified RedEnvelope not exists");
        require(entity.getAvailable(), "The specified RedEnvelope is not available");
        require(entity.getMap().get(Msg.sender()) == null, "each address can only snatch one RedEnvelope once");
        return RedEnvelopeManager.process(entity, Msg.sender());
    }

    @View
    @Override
    public RedEnvelopeEntity queryRedEnvelopeInfo(@Required Long id) {
        RedEnvelopeEntity entity = map.get(id);
        require(entity != null, "The specified RedEnvelope not exists");
        return entity;
    }

    @View
    @Override
    public List<String> allRedEnvelopeInfo() {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (Long aLong : map.keySet()) {
            RedEnvelopeEntity entity = map.get(aLong);
            sb.append("id:").append(entity.getId())
                    .append(",available:").append(entity.getAvailable())
                    .append("\r\n");
            list.add(sb.toString());
            sb = new StringBuilder();
        }
        return list;
    }
}
