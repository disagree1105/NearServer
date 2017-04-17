import io.rong.RongCloud;
import io.rong.models.TokenResult;
import org.apache.commons.codec.binary.Hex;

import java.io.Reader;

/**
 * Created by disagree on 2017/4/17.
 */
public class Test {
    public static void main(String[] args) {
        String appKey = "0vnjpoad0cjqz";		String appSecret = "7u53M7PNGT";
        Reader reader = null ;
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Hex.encodeHex( new byte[0]);
        try {
            // 获取 Token
            TokenResult userGetTokenResult = rongCloud.user.getToken("2333", "黑血姬", "http://www.rongcloud.cn/images/logo.png");
            System.out.println("getToken:  " + userGetTokenResult.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
