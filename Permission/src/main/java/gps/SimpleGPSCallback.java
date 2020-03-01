package gps;

/**
 * <pre>
 *     GPS回调接口({@link IGPSCallBack})的简单实现类
 * </pre>
 * Author：hyj
 * Date：2020/3/1 22:20
 */
public class SimpleGPSCallback implements IGPSCallBack {
    @Override
    public void grantedGPS() {
    }

    @Override
    public void denidedGPS() {
    }

    @Override
    public CharSequence requestGPSTip() {
        return "GPS高精位置服务未开启，请打开";
    }
}