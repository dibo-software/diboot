package diboot.core.test.util;

import com.diboot.core.util.Encryptor;
import diboot.core.test.StartupApplication;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
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
