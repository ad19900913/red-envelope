package contract.func;

import contract.util.Nuls;

/**
 * Constant
 *
 * @author captain
 * @version 1.0
 * @date 19-1-31 下午12:46
 */
public interface Constant {
    Integer UNAVAILABLE_HEIGHT = 1000;//24 hours
    Nuls MAX = Nuls.ONE.multiply(20L);
    Nuls MIN = Nuls.MIN_TRANSFER;
}
