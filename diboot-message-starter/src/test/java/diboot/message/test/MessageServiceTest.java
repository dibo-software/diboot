package diboot.message.test;

import com.diboot.message.config.Cons;
import com.diboot.message.entity.Message;
import com.diboot.message.service.MessageService;
import com.diboot.message.service.MessageTemplateService;
import diboot.message.test.config.SpringMvcConfig;
import diboot.message.test.variable.MyVariableObj;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/28
 * Copyright © diboot.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageTemplateService messageTemplateService;

    @Test
    public void test() {
        Assert.assertTrue(messageTemplateService.getTemplateVariableList().contains("${手机号}"));
        Assert.assertTrue(messageTemplateService.getTemplateVariableList().contains("${验证码}"));

        MyVariableObj myVariableObj = new MyVariableObj();
        myVariableObj.setVcode("876622").setSn("UDYY-9JDF-MNF8-NBS7");
        Message message = new Message();
        message.setChannel(Cons.MESSAGE_CHANNEL.SMS.name()).setStatus(Cons.MESSAGE_STATUS.PENDING.name());
        message.setBusinessType("A").setBusinessCode("B").setSender("admin").setReceiver("123");
        message.setContent("您的验证码是: ${验证码}，产品序列号是: ${序列号}");
        messageService.send(message, myVariableObj);
        Assert.assertTrue(message.getContent().contains("876622"));
        Assert.assertTrue(message.getContent().contains("UDYY-9JDF-MNF8-NBS7"));
    }

}
