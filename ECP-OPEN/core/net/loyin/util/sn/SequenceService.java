package net.loyin.util.sn;

/**
 *生成机器码的接口，不同平台有不同实现
 * @author 杨尚川
 */
public interface SequenceService {
    /**
     * 获取机器码
     * @return  机器码
     */
    public String getSequence();    
}
