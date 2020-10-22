package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-22 19:23
 * @Version V1.0
 **/
public class AlipayConfig {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600764231";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCv7HzZiosPjogxouttT4uv3+AL+uoQ6dcTae030U6paElRlFe6lRLHOrn7ap9DaXmIltXrKvY6vz2bHswh1Q3S1n/7e5T6RfBzW02weSHbd+cVcuaxbZj8k8kSai92wd13+sPS998dzcm4Pi7Q4HbMx3hlKxjQOExMgax6XGQwXyjKW6IpqYT5tcavUnurjd/AuvhwYsZFyavfL8X1iIEKuVCfGAhEBZruFniqj2GjyeKEc8iZY59cLpGhuAenjDJF/smRrsQtmkvcXe8pZWxcGHZ7b4O6Vec2nS5NGZROUpzQcGOZBz0v3YpNXfn7ItKJtXxyPJcUqwk1NxhoyLbZAgMBAAECgf9X9eZ23s6o+JsgNlWAUkWFqmpbkxl1W3YuPshdIEo0afA+2e0DXYYGF1bmXmlDS3l40fIjo1d6CCTFaqCmSeCfiqBzHVdfy5huFTjDA83p7gmcr9ao4COeJkZi7rTMrCIP21IOKeFlBX/Wn0DJL3I28YVBEEtX0M5VcoDKrWdqx1S5ReCnH7pv0dbGUzoBu4shcCoxRQvA0XfIDPH4+Zl9D/BZQ4DFiCPi/p/ROPxz3qicyrXWcQIw3gBVOeBYrVXOsXZ6/0LAKhgUil2qf14jssGmQULbj2yq2DtAcfWBEPejAOOgKuuaOTQ/vps7bgrjOvJSdTvSniG+YYou6AECgYEA5dSi0ByEDJdt1CCJNoNdkBRvLAlWcW89+uAFRLwVcj9HdDYq3FwW3BlBWyht1PdaCyWPv8k2DtLStW264UIIKABcqdl9L3XmDRC7HLyruYt453uZnP39QTWmuR62NqvEA6l3jjhhzYMITOVrf1i1dJj56m+2cn6Yag1WVUZghk0CgYEAw/SDnjMktUMvZV51RH5FNfJVNSQt2srRP8whDS2b6d4fAZaFd9Xc6/6QF+Jv8VK5nmf9Y43f30Wbec2Iu0BorU2TlV9GGTjajk5d7gL/oYp9xE3QFIK0FYxsqP0I8byBp78HyB6IWCKLoHYMxK5a0U4v5ue3CAZetm7aMgGD0L0CgYEAgEnvD8e7FeQ5FB2NOfad9VzSqXwU2oDD0hPWHQX32qvj0MSjrBljUTxvtNieZjAj/PDciqtgF2oq38b+d9qJ/vokv1J9hE6FOWnLaEEQgiOOO3hMyMsl4I54IAE6qM28PmcMK0DhpvurWHD/TjhQIWN6rRfqeYKcxSXouPTt0EkCgYEAp2/EEyaCRDwvQH/Q1gDLJMh3lFvgrqZE3C2RRFBnfBMnDVVPoFDmX+R5vV7n5h2Yq8kRk8KDu6Ufr5k3L0Fe8E5sP+NT43NYFObTje+T/LV4t4cBuLTiKdN+eXsU7HH1m584h//G4wBJrIeSa3DL+zLC7ZurIrlGrr3TxYzT1EUCgYBy5xJDRDEyeB3/+tB8CznszXPaA0Mvbjh3xHzZc6JQPvn3iYcwQ1glu05xZHHiIiHbUlbPqAt148kLywRzQ0BPSeXxMc6Wkm5XhV0i/2QJsDC5gHXbJTtmd/aRAtLDIXQX7ka17/266enZ1y77AWx7nESudIBgblpPwqv4XRaJHA==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSmBzHutu5ljz4X5cGXdIsNFxnnVecwM6vIJxbfYDGXwWD9j1y0ZcaoXNkVHwmOikEnF6qfvikIiAtK55X802O7cptvpZRmCyMFgBiWGNEatiGptKQml4wVJlzO5sJMS6TtiRHmoV+3t1JNAXRrMb1RmwZy53YJdrSu+TeZAVA38f5jy4Aplq4ZNsDXFBBXK080Z3GxkTdlvH8MukV7hunsIjy0yKmeNnTnprgGqSwVSz6bkSIUE5URq9nAFc/+NxzPUtHuPEgidgr2TWuy5osNd2aysSKWL8PoxQhYhzkmY83OT70BJab01TZ+2vA6Px2Pw7MNePAMqzEOcA+/3twIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http:localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http:localhost:8900/pay/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
