package contract;

import contract.event.SnatchRedEnvelopeEvent;
import contract.func.RedEnvelopeInterface;
import contract.model.RedEnvelopeEntity;
import contract.util.Nuls;
import contract.util.RedEnvelopeManager;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static contract.func.RedEnvelopeConstant.UNAVAILABLE_HEIGHT;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author ad19900913@outlook.com
 */
public class RedEnvelopeContract implements Contract, RedEnvelopeInterface {

    private Map<Long, RedEnvelopeEntity> map;
    private Long count = 1L;

    public RedEnvelopeContract() {
        map = new HashMap<Long, RedEnvelopeEntity>();
    }

    /**
     * send a new RedEnvelope
     * @param parts
     * @param random
     * @param remark
     */
    @Payable
    @Override
    public void newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark) {
        require(parts > 0, "parts must larger than 0");
        Nuls money = Nuls.valueOf(Msg.value().longValue());
        require(Nuls.MIN_TRANSFER.value() <= money.divide(parts).value(), "parts must larger than money/min_transfer");
        Long id = count++;
        RedEnvelopeEntity entity = new RedEnvelopeEntity();
        entity.setId(id);
        entity.setInitialHeight(Block.number());
        entity.setParts(parts);
        entity.setRandom(random);
        entity.setAmount(money);
        entity.setBalance(entity.getAmount());
        entity.setMap(new HashMap<Address, SnatchRedEnvelopeEvent>(parts));
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
        require(Block.number() - entity.getInitialHeight() < UNAVAILABLE_HEIGHT, "timeout");
        return RedEnvelopeManager.process(entity, Msg.sender());
    }

    @Override
    public void retrieveRedEnvelope(Long id) {
        RedEnvelopeEntity entity = map.get(id);
        require(entity != null, "The specified RedEnvelope not exists");
        require(Msg.sender().equals(entity.getSponsor()), "Only the RedEnvelope's Sponsor can retrieve it");
        require(Block.number() - entity.getInitialHeight() > UNAVAILABLE_HEIGHT, "wait wait");
        require(entity.getBalance().value() > 0, "RedEnvelope's empty");
        entity.getSponsor().transfer(BigInteger.valueOf(entity.getBalance().value()));
        entity.setAvailable(false);
    }

    @View
    @Override
    public RedEnvelopeEntity detailInfo(@Required Long id) {
        RedEnvelopeEntity entity = map.get(id);
        require(entity != null, "The specified RedEnvelope not exists");
        return entity;
    }

    @View
    @Override
    public Boolean availableInfo(@Required Long id) {
        RedEnvelopeEntity entity = map.get(id);
        require(entity != null, "The specified RedEnvelope not exists");
        if (Block.number() - entity.getInitialHeight() > UNAVAILABLE_HEIGHT) {
            return false;
        }
        return true;
    }

    @View
    @Override
    public List<String> allInfo() {
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
