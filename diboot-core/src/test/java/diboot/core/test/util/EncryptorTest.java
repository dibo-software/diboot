package diboot.core.test.util;

import com.diboot.core.util.Encryptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
public class EncryptorTest {

    @Test
    public void testEncrypt(){
        String text = "Hello World";
        String encryptText = Encryptor.encrypt(text);
        System.out.println(encryptText);
        // 加密后长度
        Assert.assertTrue(encryptText.length() >= 24);
        // 解密
        Assert.assertTrue(Encryptor.decrypt(encryptText).equals(text));

        String seed = "ABCDEF";
        encryptText = Encryptor.encrypt(text, seed);
        System.out.println(encryptText);
        // 加密后长度
        Assert.assertTrue(encryptText.length() >= 24);
        // 解密
        Assert.assertTrue(Encryptor.decrypt(encryptText, seed).equals(text));
    }

}
