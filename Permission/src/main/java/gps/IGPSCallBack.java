package gps;

/**
 * <pre>
 * </pre>
 * Author：hyj
 * Date：2020/3/1 21:25
 */
interface IGPSCallBack {
    /**
     * 同意GPS请求
     */
    void grantedGPS();

    /**
     * 拒绝GPS请求
     */
    void denidedGPS();

    /**
     * 请求GPS时的提示语
     */
    CharSequence requestGPSTip();
}
