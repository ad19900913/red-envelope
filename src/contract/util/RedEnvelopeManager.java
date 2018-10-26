package contract.util;

import contract.event.SnatchRedEnvelopeEvent;
import contract.model.RedEnvelopeEntity;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.*;

public class RedEnvelopeManager {

    public static Nuls process(RedEnvelopeEntity entity, Address sender) {
        Nuls nuls = Nuls.ZERO;
        if (entity.getParts() - entity.getMap().size() == 1) {
            nuls = entity.getBalance();
        } else {
            if (entity.getRandom()) {
                int remain = entity.getParts() - entity.getMap().size();
                Nuls expect = entity.getBalance().divide(remain);
                int mod = (int) expect.multiply(2).divide(Nuls.MIN_TRANSFER).value();
                int random = pseudoRandom((int) Block.timestamp());
                nuls = Nuls.MIN_TRANSFER.multiply(random % mod);
            } else {
                nuls = entity.getAmount().divide(entity.getParts());
            }
        }
        SnatchRedEnvelopeEvent event = new SnatchRedEnvelopeEvent();
        event.setId(entity.getId());
        event.setNuls(nuls);
        event.setSnatcher(sender);
        entity.getMap().put(sender, event);

        entity.setBalance(entity.getBalance().minus(nuls));
        if (entity.getMap().size() == entity.getParts()) {
            entity.setAvailable(false);
        }

        sender.transfer(BigInteger.valueOf(nuls.value()));
        return nuls;
    }
}
